package com.css123group.corr4_app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import com.css123group.corr4_be.Customer;
import java.io.IOException;

public class HomeController {

    @FXML
    private BorderPane rootPane; // fx:id from Homepage.fxml

    @FXML
    private Label customerNameLabel; // Add this if your FXML has it

    @FXML
    private void initialize() {
        // Display customer information
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
}

