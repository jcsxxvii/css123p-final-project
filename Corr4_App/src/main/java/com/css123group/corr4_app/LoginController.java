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

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label welcomeText; // Optional if you want to show messages

    // Modified handleLogin to navigate to HomePage
    @FXML
    protected void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        // Temporary message
        System.out.println("Login clicked: " + email + " / " + password);
        if (welcomeText != null) {
            welcomeText.setText("Attempting login for: " + email);
        }

        try {
            // Load HomePage FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("HomePage.fxml"));
            Parent homePageRoot = loader.load();

            // Get current stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set new scene
            Scene scene = new Scene(homePageRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            if (welcomeText != null) {
                welcomeText.setText("Failed to load HomePage.");
            }
        }
    }

    @FXML
    protected void handleRegister() {
        System.out.println("Register button clicked");
        if (welcomeText != null) {
            welcomeText.setText("Redirecting to registration...");
        }
        // You can later navigate to a Registration page here
    }
}

