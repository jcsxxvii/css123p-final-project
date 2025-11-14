package com.css123group.corr4_be;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

        // Create customer record
        boolean created = customerDAO.createCustomer(customer);
        if (!created) {
            return null;
        }

        int customerId = customer.getId();
        // Hash password
        byte[] salt = generateSalt();
        byte[] hash;
        try {
            hash = pbkdf2(plainPassword.toCharArray(), salt, DEFAULT_ITERATIONS, HASH_BYTES);
        } catch (Exception e) {
            // Rollback: optionally delete created customer? For now, log and return null
            System.err.println("Error hashing password: " + e.getMessage());
            return null;
        }

        // Store credentials
        String insertSql = "INSERT INTO " + CREDENTIALS_TABLE + " (c_id, email, password_hash, salt, provider) VALUES (?, ?, ?, ?, 'local')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            pstmt.setInt(1, customerId);
            pstmt.setString(2, customer.getEmail());
            pstmt.setString(3, Base64.getEncoder().encodeToString(hash));
            pstmt.setString(4, Base64.getEncoder().encodeToString(salt));
            pstmt.executeUpdate();
        }

        return customer;
    }

    /**
     * Authenticate user by email and password.
     * Returns the Customer on success, or null on failure.
     */
    public Customer authenticate(String email, String plainPassword) throws SQLException {
        // Find customer by email
        Customer customer = customerDAO.getCustomerByEmail(email);
        if (customer == null) {
            return null;
        }

        ensureCredentialsTable();

        String query = "SELECT password_hash, salt FROM " + CREDENTIALS_TABLE + " WHERE c_id = ? AND disabled = false";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, customer.getId());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    return null; // no credentials for this customer
                }
                String hashB64 = rs.getString("password_hash");
                String saltB64 = rs.getString("salt");

                byte[] storedHash = Base64.getDecoder().decode(hashB64);
                byte[] salt = Base64.getDecoder().decode(saltB64);

                byte[] computedHash;
                try {
                    computedHash = pbkdf2(plainPassword.toCharArray(), salt, DEFAULT_ITERATIONS, storedHash.length);
                } catch (Exception e) {
                    System.err.println("Error computing hash: " + e.getMessage());
                    return null;
                }

                if (slowEquals(storedHash, computedHash)) {
                    return customer;
                } else {
                    return null;
                }
            }
        }
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
