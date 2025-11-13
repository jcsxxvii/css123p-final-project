package corr4_be;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Account {
    private int id;
    private int customerId;
    private String accountNumber;
    private String accountType;
    private BigDecimal balance;
    private LocalDate openedDate;
    private String status;
    
    // Constructors, getters, and setters
    public Account() {}
    
    public Account(int customerId, String accountNumber, String accountType, BigDecimal balance) {
        this.customerId = customerId;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
    }
    
    // Getters and setters...
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    
    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
    
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    
    public LocalDate getOpenedDate() { return openedDate; }
    public void setOpenedDate(LocalDate openedDate) { this.openedDate = openedDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}