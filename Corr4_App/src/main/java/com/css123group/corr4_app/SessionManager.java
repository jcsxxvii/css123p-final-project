package com.css123group.corr4_app;

import com.css123group.corr4_be.Customer;
import com.css123group.corr4_be.Account;
import java.util.ArrayList;
import java.util.List;

/**
 * SessionManager handles the current user session state.
 * This allows controllers to access customer and account data
 * without tightly coupling them to the backend.
 */
public class SessionManager {
    private static SessionManager instance;
    private Customer currentCustomer;
    private List<Account> currentAccounts;
    
    private SessionManager() {
        this.currentAccounts = new ArrayList<>();
    }
    
    /**
     * Get the singleton instance of SessionManager
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    /**
     * Set the current logged-in customer
     */
    public void setCurrentCustomer(Customer customer) {
        this.currentCustomer = customer;
    }
    
    /**
     * Get the current logged-in customer
     */
    public Customer getCurrentCustomer() {
        return currentCustomer;
    }
    
    /**
     * Check if a customer is currently logged in
     */
    public boolean isLoggedIn() {
        return currentCustomer != null;
    }
    
    /**
     * Set the accounts for the current customer
     */
    public void setCurrentAccounts(List<Account> accounts) {
        this.currentAccounts = accounts;
    }
    
    /**
     * Get the accounts for the current customer
     */
    public List<Account> getCurrentAccounts() {
        return currentAccounts;
    }
    
    /**
     * Clear the session (logout)
     */
    public void clearSession() {
        this.currentCustomer = null;
        this.currentAccounts.clear();
    }
}
