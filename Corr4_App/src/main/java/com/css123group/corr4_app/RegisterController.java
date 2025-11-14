package com.css123group.corr4_app;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterController {

    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField accountNumberField;
    @FXML private TextField initialDepositField;
    @FXML private Label statusLabel;

    // Email validation pattern for a basic check
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX = 
        Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private void switchScene(ActionEvent event, String fxmlPath, String errorMessage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            if (statusLabel != null) {
                statusLabel.setText(errorMessage);
            }
        }
    }

    private boolean validateEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    @FXML
    protected void handleRegister(ActionEvent event) {
        System.out.println("REGISTER button clicked."); 

        String fullName = fullNameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String accountNumber = accountNumberField.getText();
        String deposit = initialDepositField.getText();
        
        // --- 1. Basic Emptiness Check ---
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() ||
                confirmPassword.isEmpty() || accountNumber.isEmpty() || deposit.isEmpty()) {
            statusLabel.setText("❌ Please fill in all fields.");
            return;
        }

        // --- 2. Email Format Validation ---
        if (!validateEmail(email)) {
            statusLabel.setText("❌ Invalid email format.");
            return;
        }

        // --- 3. Password Match Check ---
        if (!password.equals(confirmPassword)) {
            statusLabel.setText("❌ Passwords do not match.");
            return;
        }
        
        // --- 4. Numeric and Positive Deposit Check ---
        BigDecimal initialDeposit;
        try {
            initialDeposit = new BigDecimal(deposit.trim());
            if (initialDeposit.compareTo(BigDecimal.ZERO) <= 0) {
                statusLabel.setText("❌ Initial deposit must be greater than zero.");
                return;
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("❌ Invalid deposit amount format.");
            return;
        }
        
        // --- 5. Account Number Format (Simple numeric check) ---
        if (!accountNumber.matches("\\d+")) {
            statusLabel.setText("❌ Account number must be numeric.");
            return;
        }
        
        // At this point, all client-side validation passed.
        // The future backend call will go here.
        
        // Temporary success and redirect without backend call
        statusLabel.setText("✅ Registration data validated! Redirecting to login...");
        System.out.println("Validated registration for: " + email);

        switchScene(event, "/com/css123group/corr4_app/Login.fxml", "Failed to load login page.");
    }

    @FXML
    protected void handleBack(ActionEvent event) {
        switchScene(event, "/com/css123group/corr4_app/Login.fxml", "Error returning to login.");
    }
}