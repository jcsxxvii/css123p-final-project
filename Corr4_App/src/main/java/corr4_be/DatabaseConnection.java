package corr4_be;

import java.sql.*;
import java.util.Properties;

public class DatabaseConnection {
    private static final String URL = "postgresql://postgres:[passnggroupnijade]@db.mcrkbayvjgoqdxykhngc.supabase.co:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "passnggroupnijade";
    
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL JDBC Driver not found", e);
        }
        
        Properties props = new Properties();
        props.setProperty("user", USER);
        props.setProperty("password", PASSWORD);
        props.setProperty("ssl", "true");
        props.setProperty("sslmode", "verify-full");
        
        return DriverManager.getConnection(URL, props);
    }
    
    // Test connection
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return true;
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }
}
