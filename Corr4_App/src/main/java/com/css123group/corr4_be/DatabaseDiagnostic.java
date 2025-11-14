package com.css123group.corr4_be;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseDiagnostic {
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║     CORR4 Bank - Database Connection Diagnostic Tool      ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");
        
        // Step 1: Test basic connection
        System.out.println("Step 1: Testing basic database connection...");
        Connection conn = testBasicConnection();
        if (conn == null) {
            System.out.println("\n❌ Cannot proceed without database connection.");
            System.out.println("\nTroubleshooting steps:");
            System.out.println("  1. Verify Supabase hostname: db.mcrkbayvjgoqdxykhngc.supabase.co");
            System.out.println("  2. Check network connectivity: ping db.mcrkbayvjgoqdxykhngc.supabase.co");
            System.out.println("  3. Verify credentials in DatabaseConnection.java");
            System.out.println("  4. Check if PostgreSQL port 5432 is accessible");
            return;
        }
        
        // Step 2: Verify required tables exist
        System.out.println("\nStep 2: Checking required database tables...");
        verifyTables(conn);
        
        // Step 3: Check table structure
        System.out.println("\nStep 3: Verifying table structures...");
        checkTableStructure(conn);
        
        // Step 4: Test basic operations
        System.out.println("\nStep 4: Testing basic database operations...");
        testBasicOperations(conn);
        
        try {
            conn.close();
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
        
        System.out.println("\n✓ Database diagnostic complete!");
    }
    
    private static Connection testBasicConnection() {
        try {
            System.out.println("  Attempting to connect to: db.mcrkbayvjgoqdxykhngc.supabase.co:5432");
            Connection conn = DatabaseConnection.getConnection();
            if (conn != null) {
                System.out.println("  ✓ Connection established successfully!");
                System.out.println("    Host: db.mcrkbayvjgoqdxykhngc.supabase.co");
                System.out.println("    Port: 5432");
                System.out.println("    Database: postgres");
                System.out.println("    SSL: Required");
                return conn;
            }
        } catch (SQLException e) {
            System.out.println("  ✗ Connection failed!");
            System.out.println("    Error: " + e.getMessage());
            System.out.println("    SQL State: " + e.getSQLState());
            System.out.println("\n  TROUBLESHOOTING GUIDE:");
            
            String error = e.getMessage().toLowerCase();
            if (error.contains("unknown host") || error.contains("hostname")) {
                System.out.println("    → Network Issue: Cannot resolve Supabase hostname");
                System.out.println("    → Solutions:");
                System.out.println("      1. Check internet connection");
                System.out.println("      2. Verify hostname: db.mcrkbayvjgoqdxykhngc.supabase.co");
                System.out.println("      3. Try: ping db.mcrkbayvjgoqdxykhngc.supabase.co");
            } else if (error.contains("connection refused") || error.contains("refused")) {
                System.out.println("    → Connection Refused: Port 5432 may be blocked");
                System.out.println("    → Solutions:");
                System.out.println("      1. Check firewall settings");
                System.out.println("      2. Verify Supabase project is running");
                System.out.println("      3. Check if port 5432 is open");
            } else if (error.contains("password") || error.contains("authentication")) {
                System.out.println("    → Authentication Failed: Check credentials");
                System.out.println("    → Solutions:");
                System.out.println("      1. Verify username (postgres)");
                System.out.println("      2. Verify password in DatabaseConnection.java");
                System.out.println("      3. Reset password in Supabase dashboard if needed");
            } else if (error.contains("ssl") || error.contains("certificate")) {
                System.out.println("    → SSL/TLS Issue: Certificate verification failed");
                System.out.println("    → Solutions:");
                System.out.println("      1. Ensure sslmode=require is set");
                System.out.println("      2. Update Java security certificates");
                System.out.println("      3. Try: keytool -import -alias supabase -file cert.pem -keystore cacerts");
            } else {
                System.out.println("    → Unknown Error: " + e.getClass().getName());
                e.printStackTrace();
            }
        }
        return null;
    }
    
    private static void verifyTables(Connection conn) {
        String[] requiredTables = {"registrations", "accounts", "transactions", "user_credentials"};
        
        try (Statement stmt = conn.createStatement()) {
            for (String tableName : requiredTables) {
                String query = "SELECT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = '" + tableName + "')";
                ResultSet rs = stmt.executeQuery(query);
                if (rs.next() && rs.getBoolean(1)) {
                    System.out.println("  ✓ Table '" + tableName + "' exists");
                } else {
                    System.out.println("  ⚠ Table '" + tableName + "' NOT found");
                }
                rs.close();
            }
        } catch (SQLException e) {
            System.out.println("  ✗ Error checking tables: " + e.getMessage());
        }
    }
    
    private static void checkTableStructure(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // Check registrations table
            String query = "SELECT column_name, data_type FROM information_schema.columns WHERE table_name = 'registrations' ORDER BY ordinal_position";
            ResultSet rs = stmt.executeQuery(query);
            
            System.out.println("  'registrations' table columns:");
            boolean found = false;
            while (rs.next()) {
                System.out.println("    - " + rs.getString("column_name") + " (" + rs.getString("data_type") + ")");
                found = true;
            }
            if (!found) {
                System.out.println("    ⚠ Table not accessible");
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println("  ✗ Error checking table structure: " + e.getMessage());
        }
    }
    
    private static void testBasicOperations(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // Simple query to verify read access
            String query = "SELECT COUNT(*) as count FROM registrations";
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                int count = rs.getInt("count");
                System.out.println("  ✓ Read access verified. Current registrations: " + count);
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println("  ⚠ Read operation test failed: " + e.getMessage());
        }
    }
}
