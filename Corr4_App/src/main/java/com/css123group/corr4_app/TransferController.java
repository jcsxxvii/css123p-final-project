package com.css123group.corr4_app;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class TransferController {

    @FXML
    private TextField amountField;

    @FXML
    private TextField fromAccountField;

    @FXML
    private TextField toAccountField;

    @FXML
    private void initialize() {
        // initialization if needed
    }

    @FXML
    private void handleConfirm() {
        String amount = amountField.getText();
        String from = fromAccountField.getText();
        String to = toAccountField.getText();

        // quick validation example
        if (amount == null || amount.isEmpty() || from == null || from.isEmpty() || to == null || to.isEmpty()) {
            Alert a = new Alert(AlertType.WARNING, "Please fill all fields before confirming.");
            a.showAndWait();
            return;
        }

        // placeholder action: implement actual transfer logic later
        Alert a = new Alert(AlertType.INFORMATION, "Transfer requested:\nFrom: " + from + "\nTo: " + to + "\nAmount: " + amount);
        a.showAndWait();
    }

    @FXML
    private void handleClear() {
        amountField.clear();
        fromAccountField.clear();
        toAccountField.clear();
    }
}
