package com.css123group.corr4_app;

import java.io.IOException;
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
import javafx.stage.Stage; // Import Pattern and Matcher

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label welcomeText; // We will minimize its use, preferring Alerts

    // Email validation pattern (same as in RegisterController)
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX = 
        Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private boolean validateEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    private BankingService bankingService;
    private Auth auth;

    @FXML
    private void initialize() {
        // Initialize the banking service
        this.bankingService = new BankingService();
        this.auth = new Auth();
    }

    @FXML
    protected void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        System.out.println("Login clicked: " + email + " / " + password);

        // --- 1. Basic Emptiness Check ---
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            // Use the new utility class
            Alerts.showWarning("Input Missing", "Please enter both email and password.");
            return;
        }

        // --- 2. Email Format Validation ---
        if (!validateEmail(email)) {
            // Use the new utility class
            Alerts.showError("Invalid Email Format", "Please enter a valid email address.");
            return;
        }

        // --- Backend Call Simulation ---
        // Since we are not touching the backend:
        // Assume successful validation and proceed to HomePage
        
        System.out.println("Client-side validation passed for: " + email);

        // Clear the status label (if it was used before)
        if (welcomeText != null) {
            welcomeText.setText("");
        }

        // NOTE: No authentication backend exists — proceed to HomePage for now.
        try {
            // Authenticate using Auth (verifies password)
            Customer customer = auth.authenticate(email, password);

            if (customer == null) {
                if (welcomeText != null) {
                    welcomeText.setText("Invalid credentials. Please check email/password.");
                }
                return;
            }

            // Get customer's accounts
            List<Account> accounts = bankingService.getCustomerAccounts(customer.getId());

            // Store in session
            SessionManager.getInstance().setCurrentCustomer(customer);
            SessionManager.getInstance().setCurrentAccounts(accounts);

            if (welcomeText != null) {
                welcomeText.setText("Login successful for: " + email);
            }

            // Navigate to home page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/css123group/corr4_app/HomePage.fxml"));
            Parent homePageRoot = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(homePageRoot));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            // Use the new utility class for error handling
            Alerts.showError("Navigation Error", "Failed to load the main application Home Page.");
        }
    }

    @FXML
    protected void handleRegister(ActionEvent event) {
        System.out.println("Register button clicked");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/css123group/corr4_app/Register.fxml"));
            Parent registerRoot = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(registerRoot));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alerts.showError("Navigation Error", "Failed to load the registration page.");
        }
    }
}