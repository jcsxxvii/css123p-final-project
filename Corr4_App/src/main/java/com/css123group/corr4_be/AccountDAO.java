package com.css123group.corr4_be;

import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {
    
    public boolean createAccount(Account account) throws SQLException {
        String sql = "INSERT INTO accounts (c_id, acct_no, acct_type, bal) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, account.getCustomerId());
            pstmt.setString(2, account.getAccountNumber());
            pstmt.setString(3, account.getAccountType());
            pstmt.setBigDecimal(4, account.getBalance());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public Account getAccountByNumber(String accountNumber) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE acct_no = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractAccountFromResultSet(rs);
            }
        }
        return null;
    }
    
    public List<Account> getAccountsByCustomerId(int customerId) throws SQLException {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE c_id = ? ORDER BY opened_on DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                accounts.add(extractAccountFromResultSet(rs));
            }
        }
        return accounts;
    }
    
    public boolean updateBalance(int accountId, BigDecimal newBalance) throws SQLException {
        String sql = "UPDATE accounts SET bal = ? WHERE a_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBigDecimal(1, newBalance);
            pstmt.setInt(2, accountId);
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    private Account extractAccountFromResultSet(ResultSet rs) throws SQLException {
        Account account = new Account();
        account.setId(rs.getInt("a_id"));
        account.setCustomerId(rs.getInt("c_id"));
        account.setAccountNumber(rs.getString("acct_no"));
        account.setAccountType(rs.getString("acct_type"));
        account.setBalance(rs.getBigDecimal("bal"));
        account.setOpenedDate(rs.getDate("opened_on").toLocalDate());
        account.setStatus(rs.getString("status"));
        return account;
    }
}