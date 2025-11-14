package com.css123group.corr4_be;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private int id;
    private int accountId;
    private String transactionType;
    private BigDecimal amount;
    private String description;
    private LocalDateTime transactionDate;
    private BigDecimal balanceAfter;
    
    // Constructors
    public Transaction() {}
    
    public Transaction(int accountId, String transactionType, BigDecimal amount, 
                      String description, BigDecimal balanceAfter) {
        this.accountId = accountId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.description = description;
        this.balanceAfter = balanceAfter;
        this.transactionDate = LocalDateTime.now();
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }
    
    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }
    
    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(BigDecimal balanceAfter) { this.balanceAfter = balanceAfter; }
    
    @Override
    public String toString() {
        return String.format("Transaction{id=%d, accountId=%d, type=%s, amount=%s, description=%s, date=%s, balanceAfter=%s}",
                id, accountId, transactionType, amount, description, transactionDate, balanceAfter);
    }
}