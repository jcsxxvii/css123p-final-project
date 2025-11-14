package com.css123group.corr4_app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
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

    @FXML
    private void initialize() {
        // Initialize the auth helper
        this.auth = new Auth();
    }

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

    @FXML
    protected void handleRegister(ActionEvent event) {
        System.out.println("REGISTER button clicked."); // Debug

        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String depositText = initialDepositField.getText().trim();

        // Validation
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() ||
                confirmPassword.isEmpty() || depositText.isEmpty()) {
            statusLabel.setText("Please fill in all fields.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            statusLabel.setText("Passwords do not match.");
            return;
        }

        // Parse full name into first and last name
        String[] nameParts = fullName.split(" ", 2);
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[1] : "";

        // Parse initial deposit
        BigDecimal initialDeposit;
        try {
            initialDeposit = new BigDecimal(depositText);
            if (initialDeposit.compareTo(BigDecimal.ZERO) < 0) {
                statusLabel.setText("Initial deposit must be non-negative.");
                return;
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid deposit amount. Please enter a valid number.");
            return;
        }

            // Create Customer object and register using Auth (stores credentials)
            try {
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
