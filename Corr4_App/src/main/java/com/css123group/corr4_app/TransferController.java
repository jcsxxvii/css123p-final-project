package com.css123group.corr4_app;

import java.math.BigDecimal;

import com.css123group.corr4_be.BankingService; 

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
// Removed: javafx.scene.control.Alert and Alert.AlertType imports

public class TransferController {

    @FXML
    private TextField amountField;

    @FXML
    private TextField fromAccountField;

    @FXML
    private TextField toAccountField;

    @FXML
    private void initialize() {
        // initialization if needed
    }

    @FXML
    private void handleConfirm() {
        String amount = amountField.getText();
        String from = fromAccountField.getText().trim(); 
        String to = toAccountField.getText().trim();     

        // --- 1. Basic Emptiness Check ---
        if (amount == null || amount.isEmpty() || from.isEmpty() || to.isEmpty()) {
            Alerts.showWarning("Input Missing", "Please fill all fields before confirming.");
            return;
        }

        // --- 2. Account Number Format Validation ---
        if (!from.matches("\\d+") || !to.matches("\\d+")) {
            Alerts.showError("Invalid Account Number", "Account numbers must contain only digits.");
            return;
        }

        // --- 3. Self-Transfer Prevention ---
        if (from.equals(to)) {
            Alerts.showError("Invalid Transfer", "The source and destination accounts cannot be the same.");
            return;
        }

        // --- 4. Numeric and Positive Amount Check ---
        BigDecimal amt;
        try {
            amt = new BigDecimal(amount.trim());
            if (amt.compareTo(BigDecimal.ZERO) <= 0) {
                Alerts.showWarning("Invalid Amount", "Amount must be greater than zero.");
                return;
            }
        } catch (NumberFormatException ex) {
            Alerts.showError("Invalid Amount Format", "Invalid amount. Please enter a valid number.");
            return;
        }

        // --- 5. Backend Logic (Existing Simulated Logic) ---
        BankingService bankingService = new BankingService();
        boolean withdrawn = bankingService.withdraw(from, amt, "Transfer to " + to);
        
        if (!withdrawn) {
            Alerts.showError("Withdrawal Failed", "Failed to withdraw from source account. Check balance or account number.");
            return;
        }

        boolean deposited = bankingService.deposit(to, amt, "Transfer from " + from);
        if (!deposited) {
            // Attempt to rollback by re-depositing to source (best-effort)
            bankingService.deposit(from, amt, "Rollback transfer due to failure");
            Alerts.showError("Deposit Failed - Rolled Back", "Transfer failed while depositing to destination. Rolled back the withdrawal.");
            return;
        }

        Alerts.showInfo("Transfer Complete", "Transfer successful:\nFrom: " + from + "\nTo: " + to + "\nAmount: " + amt);
        
        // Clear fields on successful transfer
        handleClear();
    }

    @FXML
    private void handleClear() {
        amountField.clear();
        fromAccountField.clear();
        toAccountField.clear();
    }
}