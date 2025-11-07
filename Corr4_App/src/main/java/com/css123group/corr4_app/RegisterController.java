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

public class RegisterController {

    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField accountNumberField;
    @FXML private TextField initialDepositField;
    @FXML private Label statusLabel;

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

        String fullName = fullNameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String accountNumber = accountNumberField.getText();
        String deposit = initialDepositField.getText();

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() ||
                confirmPassword.isEmpty() || accountNumber.isEmpty() || deposit.isEmpty()) {
            statusLabel.setText("Please fill in all fields.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            statusLabel.setText("Passwords do not match.");
            return;
        }

        statusLabel.setText("Registration successful! Redirecting to login...");
        System.out.println("New registration: " + email);

        switchScene(event, "/com/css123group/corr4_app/Login.fxml", "Failed to load login page.");
    }

    @FXML
    protected void handleBack(ActionEvent event) {
        switchScene(event, "/com/css123group/corr4_app/Login.fxml", "Error returning to login.");
    }
}
