package com.css123group.corr4_app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.util.List;

import com.css123group.corr4_be.BankingService;
import com.css123group.corr4_be.Auth;
import com.css123group.corr4_be.Customer;
import com.css123group.corr4_be.Account;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label welcomeText;

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

        // Validate input
        if (email.isEmpty() || password.isEmpty()) {
            if (welcomeText != null) {
                welcomeText.setText("Please enter email and password.");
            }
            return;
        }

        System.out.println("Login clicked: " + email);

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
            if (welcomeText != null) {
                welcomeText.setText("Failed to load HomePage.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (welcomeText != null) {
                welcomeText.setText("Error during login: " + e.getMessage());
            }
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
            if (welcomeText != null) {
                welcomeText.setText("Failed to load registration page.");
            }
        }
    }
}
