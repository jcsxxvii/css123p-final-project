package corr4_be;

import models.*;
import dao.*;
import java.math.BigDecimal;
import java.sql.SQLException;

public class BankingService {
    private CustomerDAO customerDAO;
    private AccountDAO accountDAO;
    private TransactionDAO transactionDAO;
    
    public BankingService() {
        this.customerDAO = new CustomerDAO();
        this.accountDAO = new AccountDAO();
        this.transactionDAO = new TransactionDAO();
    }
    
    public boolean createNewCustomer(String firstName, String lastName, String email, 
                                   String phone, String address, String dateOfBirth) {
        try {
            Customer customer = new Customer(firstName, lastName, email, phone, address, 
                                           java.time.LocalDate.parse(dateOfBirth));
            return customerDAO.createCustomer(customer);
        } catch (SQLException e) {
            System.err.println("Error creating customer: " + e.getMessage());
            return false;
        }
    }
    
    public boolean createAccount(int customerId, String accountType, BigDecimal initialDeposit) {
        try {
            // Generate account number
            String accountNumber = generateAccountNumber(accountType);
            
            Account account = new Account(customerId, accountNumber, accountType.toUpperCase(), initialDeposit);
            boolean accountCreated = accountDAO.createAccount(account);
            
            if (accountCreated && initialDeposit.compareTo(BigDecimal.ZERO) > 0) {
                // Record initial deposit transaction
                Account createdAccount = accountDAO.getAccountByNumber(accountNumber);
                transactionDAO.recordTransaction(createdAccount.getId(), "DEPOSIT", 
                                               initialDeposit, "Initial deposit", initialDeposit);
            }
            
            return accountCreated;
        } catch (SQLException e) {
            System.err.println("Error creating account: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deposit(String accountNumber, BigDecimal amount, String description) {
        try {
            Account account = accountDAO.getAccountByNumber(accountNumber);
            if (account == null) {
                System.out.println("Account not found!");
                return false;
            }
            
            BigDecimal newBalance = account.getBalance().add(amount);
            boolean updated = accountDAO.updateBalance(account.getId(), newBalance);
            
            if (updated) {
                transactionDAO.recordTransaction(account.getId(), "DEPOSIT", amount, 
                                               description, newBalance);
                System.out.println("Deposit successful! New balance: $" + newBalance);
            }
            
            return updated;
        } catch (SQLException e) {
            System.err.println("Error processing deposit: " + e.getMessage());
            return false;
        }
    }
    
    public boolean withdraw(String accountNumber, BigDecimal amount, String description) {
        try {
            Account account = accountDAO.getAccountByNumber(accountNumber);
            if (account == null) {
                System.out.println("Account not found!");
                return false;
            }
            
            if (account.getBalance().compareTo(amount) < 0) {
                System.out.println("Insufficient funds!");
                return false;
            }
            
            BigDecimal newBalance = account.getBalance().subtract(amount);
            boolean updated = accountDAO.updateBalance(account.getId(), newBalance);
            
            if (updated) {
                transactionDAO.recordTransaction(account.getId(), "WITHDRAWAL", amount, 
                                               description, newBalance);
                System.out.println("Withdrawal successful! New balance: $" + newBalance);
            }
            
            return updated;
        } catch (SQLException e) {
            System.err.println("Error processing withdrawal: " + e.getMessage());
            return false;
        }
    }
    
    private String generateAccountNumber(String accountType) {
        String prefix = "";
        switch (accountType.toUpperCase()) {
            case "SAVINGS": prefix = "SAV"; break;
            case "CHECKING": prefix = "CHK"; break;
            case "BUSINESS": prefix = "BUS"; break;
            default: prefix = "GEN"; break;
        }
        
        long timestamp = System.currentTimeMillis() % 1000000;
        return prefix + String.format("%07d", timestamp);
    }
}
