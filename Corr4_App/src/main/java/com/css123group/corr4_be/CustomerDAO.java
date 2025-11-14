package com.css123group.corr4_be;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    
    public boolean createCustomer(Customer customer) throws SQLException {
        // Support optional credential columns in registrations if provided by the DB schema.
        boolean includeCreds = customer.getPasswordHash() != null && customer.getSalt() != null;
        String sql;
        if (includeCreds) {
            sql = "INSERT INTO registrations (fname, lname, email, phone, address, date_of_birth, password_hash, salt, provider, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        } else {
            sql = "INSERT INTO registrations (fname, lname, email, phone, address, date_of_birth, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            int idx = 1;
            pstmt.setString(idx++, customer.getFirstName());
            pstmt.setString(idx++, customer.getLastName());
            pstmt.setString(idx++, customer.getEmail());
            pstmt.setString(idx++, customer.getPhone());
            pstmt.setString(idx++, customer.getAddress());
            pstmt.setDate(idx++, Date.valueOf(customer.getDateOfBirth()));

            if (includeCreds) {
                pstmt.setString(idx++, customer.getPasswordHash());
                pstmt.setString(idx++, customer.getSalt());
                pstmt.setString(idx++, customer.getProvider() == null ? "local" : customer.getProvider());
                pstmt.setString(idx++, customer.getStatus() == null ? "active" : customer.getStatus());
            }
            else {
                pstmt.setString(idx++, customer.getStatus() == null ? "active" : customer.getStatus());
            }

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        customer.setId(generatedKeys.getInt(1));
                    }
                }
            }

            return affectedRows > 0;
        }
    }
    
    public Customer getCustomerById(int id) throws SQLException {
        String sql1 = "SELECT r.*, uc.password_hash AS uc_password_hash, uc.salt AS uc_salt, uc.provider AS uc_provider, uc.disabled AS uc_disabled "
                   + "FROM registrations r LEFT JOIN user_credentials uc ON r.reg_id = uc.c_id WHERE r.reg_id = ?";

        String sql2 = "SELECT r.*, uc.password_hash AS uc_password_hash, uc.salt AS uc_salt, uc.provider AS uc_provider, uc.disabled AS uc_disabled "
                   + "FROM registrations r LEFT JOIN user_credentials uc ON r.id = uc.registration_id WHERE r.id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql1)) {
                pstmt.setInt(1, id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) return extractCustomerFromResultSet(rs);
                }
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql2)) {
                pstmt.setInt(1, id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) return extractCustomerFromResultSet(rs);
                }
            }
        }
        return null;
    }
    
    public Customer getCustomerByEmail(String email) throws SQLException {
        String sql1 = "SELECT r.*, uc.password_hash AS uc_password_hash, uc.salt AS uc_salt, uc.provider AS uc_provider, uc.disabled AS uc_disabled "
                   + "FROM registrations r LEFT JOIN user_credentials uc ON r.reg_id = uc.c_id WHERE LOWER(r.email) = LOWER(?)";

        String sql2 = "SELECT r.*, uc.password_hash AS uc_password_hash, uc.salt AS uc_salt, uc.provider AS uc_provider, uc.disabled AS uc_disabled "
                   + "FROM registrations r LEFT JOIN user_credentials uc ON r.id = uc.registration_id WHERE LOWER(r.email) = LOWER(?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql1)) {
                pstmt.setString(1, email);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) return extractCustomerFromResultSet(rs);
                }
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql2)) {
                pstmt.setString(1, email);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) return extractCustomerFromResultSet(rs);
                }
            }
        }
        return null;
    }
    
    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT r.*, uc.password_hash AS uc_password_hash, uc.salt AS uc_salt, uc.provider AS uc_provider, uc.disabled AS uc_disabled "
               + "FROM registrations r LEFT JOIN user_credentials uc ON r.reg_id = uc.c_id ORDER BY r.reg_id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                customers.add(extractCustomerFromResultSet(rs));
            }
        }
        return customers;
    }
    
    private Customer extractCustomerFromResultSet(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setId(rs.getInt("reg_id"));
        customer.setFirstName(rs.getString("fname"));
        customer.setLastName(rs.getString("lname"));
        customer.setEmail(rs.getString("email"));
        customer.setPhone(rs.getString("phone"));
        customer.setAddress(rs.getString("address"));
        java.sql.Date dob = rs.getDate("date_of_birth");
        if (dob != null) {
            customer.setDateOfBirth(dob.toLocalDate());
        }
        // populate credential fields if the joined columns exist
        try {
            String h = rs.getString("uc_password_hash");
            String s = rs.getString("uc_salt");
            String p = rs.getString("uc_provider");
            boolean d = false;
            try { d = rs.getBoolean("uc_disabled"); } catch (Exception ex) {}
            customer.setPasswordHash(h);
            customer.setSalt(s);
            customer.setProvider(p);
            customer.setDisabled(d);
        } catch (SQLException ignore) {
            // If user_credentials not present in join, ignore
        }
        return customer;
    }

    /**
     * Return a Customer object constructed from the credentials table only (no registrations row).
     * Useful when credentials exist but registration row is missing.
     */
    public Customer getCustomerWithCredentialsByEmail(String email) throws SQLException {
        String sql1 = "SELECT c_id AS cid, email, password_hash, salt, provider, disabled FROM user_credentials WHERE LOWER(email) = LOWER(?) AND disabled = false";
        String sql2 = "SELECT registration_id AS cid, email, password_hash, salt, provider, disabled FROM user_credentials WHERE LOWER(email) = LOWER(?) AND disabled = false";

        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql1)) {
                pstmt.setString(1, email);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        Customer c = new Customer();
                        c.setId(rs.getInt("cid"));
                        c.setEmail(rs.getString("email"));
                        c.setPasswordHash(rs.getString("password_hash"));
                        c.setSalt(rs.getString("salt"));
                        c.setProvider(rs.getString("provider"));
                        c.setDisabled(rs.getBoolean("disabled"));
                        return c;
                    }
                }
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sql2)) {
                pstmt.setString(1, email);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        Customer c = new Customer();
                        c.setId(rs.getInt("cid"));
                        c.setEmail(rs.getString("email"));
                        c.setPasswordHash(rs.getString("password_hash"));
                        c.setSalt(rs.getString("salt"));
                        c.setProvider(rs.getString("provider"));
                        c.setDisabled(rs.getBoolean("disabled"));
                        return c;
                    }
                }
            }
        }
        return null;
    }
}