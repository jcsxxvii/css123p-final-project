package com.css123group.corr4_app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import com.css123group.corr4_be.Customer;
import java.io.IOException;

public class HomeController {

    @FXML
    private BorderPane rootPane;

    @FXML
    private Label customerNameLabel;

    @FXML
    public void initialize() {
        // Add global keyboard shortcuts and display customer information
        setupKeyboardShortcuts();
        updateCustomerInfo();
    }

    /**
     * Update customer information in the header
     */
    private void updateCustomerInfo() {
        Customer customer = SessionManager.getInstance().getCurrentCustomer();
        if (customer != null && customerNameLabel != null) {
            customerNameLabel.setText("Welcome, " + customer.getFirstName() + " " + customer.getLastName());
        }
    }

    // --- Navigation Handlers ---
    @FXML
    private void handleProfile() throws IOException {
        loadCenterContent("Profile.fxml");
    }

    @FXML
    private void handleBalance() throws IOException {
        loadCenterContent("Balance.fxml");
    }

    @FXML
    private void handleCards() throws IOException {
        loadCenterContent("Cards.fxml");
    }

    @FXML
    private void handleTransfer() throws IOException {
        loadCenterContent("Transfer.fxml");
    }

    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        // Clear session
        SessionManager.getInstance().clearSession();

        // Navigate back to login
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent loginRoot = loader.load();
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(new Scene(loginRoot));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- Utility method to load center content ---
    private void loadCenterContent(String fxmlFile) throws IOException {
        Parent content = FXMLLoader.load(getClass().getResource(fxmlFile));
        rootPane.setCenter(content);
    }

    // --- Keyboard Shortcuts Setup ---
    private void setupKeyboardShortcuts() {
        rootPane.setOnKeyPressed(this::handleKeyPress);
    }

    private void handleKeyPress(KeyEvent event) {
        if (event.isControlDown()) {
            switch (event.getCode()) {
                case H: // Ctrl+H - Home
                    try {
                        loadCenterContent("HomePage.fxml");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case T: // Ctrl+T - Transfer
                    try {
                        loadCenterContent("Transfer.fxml");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case L: // Ctrl+L - Logout
                    try {
                        logout();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case P: // Ctrl+P - Profile
                    try {
                        loadCenterContent("Profile.fxml");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case B: // Ctrl+B - Balance
                    try {
                        loadCenterContent("Balance.fxml");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case C: // Ctrl+C - Cards
                    try {
                        loadCenterContent("Cards.fxml");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    // Ignore other key combinations
                    break;
            }
        } else if (event.getCode() == KeyCode.ESCAPE) {
            // ESC key - return to home
            try {
                loadCenterContent("HomePage.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void logout() throws IOException {
        Parent loginRoot = FXMLLoader.load(getClass().getResource("Login.fxml"));
        rootPane.getScene().setRoot(loginRoot);
    }
}

