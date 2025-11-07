package com.css123group.corr4_app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import java.io.IOException;

public class HomeController {

    @FXML
    private BorderPane rootPane; // fx:id from Homepage.fxml

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
    private void handleLogout() throws IOException {
        // Replace scene root with login page
        Parent loginRoot = FXMLLoader.load(getClass().getResource("Login.fxml"));
        rootPane.getScene().setRoot(loginRoot);
    }

    // --- Utility method to load center content ---
    private void loadCenterContent(String fxmlFile) throws IOException {
        Parent content = FXMLLoader.load(getClass().getResource(fxmlFile));
        rootPane.setCenter(content);
    }
}

