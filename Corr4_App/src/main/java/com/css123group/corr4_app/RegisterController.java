package com.css123group.corr4_app;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
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

import com.css123group.corr4_be.Auth;
import com.css123group.corr4_be.Customer;

public class RegisterController {

    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField initialDepositField;
    @FXML private Label statusLabel;

    private Auth auth;

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

        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String deposit = initialDepositField.getText();
        
        // --- 1. Basic Emptiness Check ---
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() ||
                confirmPassword.isEmpty() || deposit.isEmpty()) {
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
        
        // At this point, all client-side validation passed.
        statusLabel.setText("✅ Registration data validated! Redirecting to login...");
        System.out.println("Validated registration for: " + email);

        // Parse full name into first and last name
        String[] nameParts = fullName.split(" ", 2);
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[1] : "";

        // Create Customer object and register using Auth (stores credentials)
        try {
            if (auth == null) {
                auth = new Auth();
            }
            Customer customer = new Customer(firstName, lastName, email, "", "", java.time.LocalDate.parse("1990-01-01"));
            Customer created = auth.registerCustomer(customer, password);
            if (created == null) {
                statusLabel.setText("Failed to create customer account.");
                return;
            }

            statusLabel.setText("Registration successful! Redirecting to login...");
            System.out.println("New registration: " + email);

            // Switch back to login after 1 second
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    javafx.application.Platform.runLater(() -> switchScene(event, "/com/css123group/corr4_app/Login.fxml", "Failed to load login page."));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Error during registration: " + e.getMessage());
        }
    }

    @FXML
    protected void handleBack(ActionEvent event) {
        switchScene(event, "/com/css123group/corr4_app/Login.fxml", "Error returning to login.");
    }
}