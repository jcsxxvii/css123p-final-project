package com.css123group.corr4_be;

import java.sql.*;
import java.util.Properties;

public class DatabaseConnection {

    // Supabase POOLER connection details (Singapore region)
    private static final String HOST = "aws-0-ap-southeast-1.pooler.supabase.com";
    private static final int PORT = 5432;
    private static final String DATABASE = "postgres";

    // Supabase username (original-style)
    private static final String USER = "postgres.mcrkbayvjgoqdxykhngc";

    // Supabase password
    private static final String PASSWORD = "7hpAe0VQIv3nhhGY";

    // Build JDBC URL
    private static final String URL =
        "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DATABASE + "?sslmode=require";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL JDBC Driver not found in pom.xml", e);
        }

        // Connection properties
        Properties props = new Properties();
        props.setProperty("user", USER);
        props.setProperty("password", PASSWORD);

        // Optional timeouts
        props.setProperty("connectTimeout", "10"); // seconds
        props.setProperty("socketTimeout", "30");  // seconds

        // Optional application name
        props.setProperty("ApplicationName", "CORR4Bank");

        try {
            Connection conn = DriverManager.getConnection(URL, props);
            System.out.println("✓ Connected to Supabase via Pooler!");
            return conn;
        } catch (SQLException e) {
            System.err.println("✗ Failed to connect: " + e.getMessage());
            throw e;
        }
    }

    // Optional: simple test connection
    public static boolean testConnection() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT NOW()")) {

            if (rs.next()) {
                System.out.println("✓ Database responded: " + rs.getString(1));
                return true;
            }
            return false;

        } catch (Exception e) {
            System.err.println("✗ Connection test failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
