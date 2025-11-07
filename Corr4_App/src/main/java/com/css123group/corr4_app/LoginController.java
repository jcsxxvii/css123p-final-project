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
    private Label welcomeText;

    @FXML
    protected void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        System.out.println("Login clicked: " + email + " / " + password);
        if (welcomeText != null) {
            welcomeText.setText("Attempting login for: " + email);
        }

        try {
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
