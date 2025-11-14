package com.css123group.corr4_be;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.security.SecureRandom;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Auth helper to register and authenticate users.
 *
 * - Registers a customer (via CustomerDAO) and stores password credentials
 *   in a new `user_credentials` table (created automatically if missing).
 * - Authenticates an email+password by checking the stored salted PBKDF2 hash.
 *
 * Note: This class stores password hashes using PBKDF2WithHmacSHA256.
 */
public class Auth {
    private static final String CREDENTIALS_TABLE = "user_credentials";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String HASH_ALGO = "PBKDF2WithHmacSHA256";
    private static final int SALT_BYTES = 16;
    private static final int HASH_BYTES = 32; // 256 bits
    private static final int DEFAULT_ITERATIONS = 65536;

    private final CustomerDAO customerDAO;

    public Auth() {
        this.customerDAO = new CustomerDAO();
    }

    /**
     * Register a new customer and store credentials.
     * Returns the created Customer with id populated on success, or null on failure.
     */
    public Customer registerCustomer(Customer customer, String plainPassword) throws SQLException {
        // Create credentials table if it doesn't exist
        ensureCredentialsTable();

        // Hash password first and attach to customer so createCustomer can persist them if registrations requires it
        byte[] salt = generateSalt();
        byte[] hash;
        try {
            hash = pbkdf2(plainPassword.toCharArray(), salt, DEFAULT_ITERATIONS, HASH_BYTES);
        } catch (Exception e) {
            System.err.println("Error hashing password: " + e.getMessage());
            return null;
        }
        String hashB64 = Base64.getEncoder().encodeToString(hash);
        String saltB64 = Base64.getEncoder().encodeToString(salt);
        customer.setPasswordHash(hashB64);
        customer.setSalt(saltB64);
        customer.setProvider("local");

        // Create customer record (may write credentials into registrations if schema supports it)
        boolean created = customerDAO.createCustomer(customer);
        if (!created) {
            return null;
        }

        int customerId = customer.getId();

        // Store credentials in user_credentials table as well (best-effort)
        String insertSql = "INSERT INTO " + CREDENTIALS_TABLE + " (c_id, email, password_hash, salt, provider) VALUES (?, ?, ?, ?, 'local')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            pstmt.setInt(1, customerId);
            pstmt.setString(2, customer.getEmail());
            pstmt.setString(3, hashB64);
            pstmt.setString(4, saltB64);
            try {
                pstmt.executeUpdate();
            } catch (SQLException e) {
                // If a credentials row already exists for this email, update its c_id to point to the newly created registration
                System.err.println("Auth: could not insert into " + CREDENTIALS_TABLE + " (attempting to attach existing credential): " + e.getMessage());
                String updateSql = "UPDATE " + CREDENTIALS_TABLE + " SET c_id = ? WHERE LOWER(email) = LOWER(?)";
                try (PreparedStatement up = conn.prepareStatement(updateSql)) {
                    up.setInt(1, customerId);
                    up.setString(2, customer.getEmail());
                    int updated = up.executeUpdate();
                    System.out.println("Auth: updated existing credential rows to new c_id, count=" + updated);
                } catch (SQLException ex) {
                    System.err.println("Auth: failed to attach existing credential: " + ex.getMessage());
                }
            }
        }
        // Also ensure any existing user_credentials rows for this email reference the new registration id
        try (Connection conn2 = DatabaseConnection.getConnection()) {
            String ensureSql = "UPDATE " + CREDENTIALS_TABLE + " SET c_id = ? WHERE LOWER(email) = LOWER(?)";
            try (PreparedStatement up2 = conn2.prepareStatement(ensureSql)) {
                up2.setInt(1, customerId);
                up2.setString(2, customer.getEmail());
                int updated2 = up2.executeUpdate();
                if (updated2 > 0) System.out.println("Auth: ensured user_credentials attached to reg id=" + customerId + ", updated=" + updated2);
            }
        } catch (SQLException e) {
            System.err.println("Auth: final attach attempt failed: " + e.getMessage());
        }

        return customer;
    }

    /**
     * Authenticate user by email and password.
     * Returns the Customer on success, or null on failure.
     */
    public Customer authenticate(String email, String plainPassword) throws SQLException {
        // Unified authenticate: try to get a Customer (with credentials) from DAO
        System.out.println("Auth: authenticating email='" + email + "'");
        Customer customer = null;
        try {
            customer = customerDAO.getCustomerByEmail(email);
        } catch (SQLException e) {
            System.err.println("Auth: error while fetching customer by email: " + e.getMessage());
            throw e;
        }

        if (customer == null) {
            // Try credentials-only lookup (when registrations row is missing)
            try {
                customer = customerDAO.getCustomerWithCredentialsByEmail(email);
            } catch (SQLException e) {
                System.err.println("Auth: error while fetching credentials-only customer: " + e.getMessage());
                throw e;
            }
        } else {
            // registration exists but might not have credentials attached; try credentials-only lookup to find credential row
            if (customer.getPasswordHash() == null) {
                try {
                    Customer credOnly = customerDAO.getCustomerWithCredentialsByEmail(email);
                    if (credOnly != null) {
                        // prefer credential data from credentials table
                        customer.setPasswordHash(credOnly.getPasswordHash());
                        customer.setSalt(credOnly.getSalt());
                        customer.setProvider(credOnly.getProvider());
                        customer.setDisabled(credOnly.isDisabled());
                        // Keep registration id on customer (the app prefers registration details), but we have credentials
                    }
                } catch (SQLException e) {
                    System.err.println("Auth: error fetching credentials-only record: " + e.getMessage());
                }
            }
        }

        if (customer == null) {
            System.out.println("Auth: no customer or credentials found for email");
            return null;
        }

        String hashB64 = customer.getPasswordHash();
        String saltB64 = customer.getSalt();
        if (hashB64 == null || saltB64 == null) {
            System.out.println("Auth: credentials not present for customer id=" + customer.getId());
            return null;
        }

        byte[] storedHash;
        byte[] salt;
        try {
            storedHash = Base64.getDecoder().decode(hashB64);
            salt = Base64.getDecoder().decode(saltB64);
        } catch (IllegalArgumentException iae) {
            System.err.println("Auth: invalid base64 in stored credentials: " + iae.getMessage());
            return null;
        }

        byte[] computedHash;
        try {
            computedHash = pbkdf2(plainPassword.toCharArray(), salt, DEFAULT_ITERATIONS, storedHash.length);
        } catch (Exception e) {
            System.err.println("Error computing hash: " + e.getMessage());
            return null;
        }

        if (slowEquals(storedHash, computedHash)) {
            return customer;
        }
        return null;
    }

    /** Create credentials table if missing. */
    private void ensureCredentialsTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS " + CREDENTIALS_TABLE + " ("
               + "auth_id SERIAL PRIMARY KEY,"
               + "c_id INTEGER REFERENCES registrations(reg_id) ON DELETE CASCADE,"
               + "email VARCHAR(100) UNIQUE NOT NULL,"
               + "password_hash TEXT NOT NULL,"
               + "salt TEXT NOT NULL,"
               + "provider VARCHAR(50) DEFAULT 'local',"
               + "is_verified BOOLEAN DEFAULT false,"
               + "disabled BOOLEAN DEFAULT false,"
               + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
               + ")";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    private static byte[] generateSalt() {
        byte[] salt = new byte[SALT_BYTES];
        RANDOM.nextBytes(salt);
        return salt;
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int bytes) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, bytes * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(HASH_ALGO);
        return skf.generateSecret(spec).getEncoded();
    }

    // Constant-time comparison to prevent timing attacks
    private static boolean slowEquals(byte[] a, byte[] b) {
        int diff = a.length ^ b.length;
        for (int i = 0; i < a.length && i < b.length; i++) {
            diff |= a[i] ^ b[i];
        }
        return diff == 0;
    }
}
