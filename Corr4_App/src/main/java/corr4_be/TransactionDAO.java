package corr4_be;

import database.DatabaseConnection;
import java.sql.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionDAO {
    
    public void recordTransaction(int accountId, String transactionType, 
                                BigDecimal amount, String description, 
                                BigDecimal balanceAfter) throws SQLException {
        String sql = "INSERT INTO transactions (account_id, transaction_type, amount, description, balance_after) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, accountId);
            pstmt.setString(2, transactionType);
            pstmt.setBigDecimal(3, amount);
            pstmt.setString(4, description);
            pstmt.setBigDecimal(5, balanceAfter);
            
            pstmt.executeUpdate();
        }
    }
}