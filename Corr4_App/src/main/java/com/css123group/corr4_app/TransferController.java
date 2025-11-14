package com.css123group.corr4_app;

import java.math.BigDecimal;

import com.css123group.corr4_be.BankingService; 

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class TransferController {

    @FXML private TextField amountField;
    @FXML private TextField fromAccountField;
    @FXML private TextField toAccountField;

    private static final DecimalFormat df = new DecimalFormat("#,##0.00");

    @FXML
    private void initialize() {
        setupKeyboardSupport();
    }

    private void setupKeyboardSupport() {
        // Add Enter key support for form submission
        amountField.setOnKeyPressed(this::handleFieldKeyPress);
        fromAccountField.setOnKeyPressed(this::handleFieldKeyPress);
        toAccountField.setOnKeyPressed(this::handleFieldKeyPress);
    }

    private void handleFieldKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (event.getSource() == toAccountField) {
                // Last field - submit the form
                handleConfirm();
            } else {
                // Move to next field
                if (event.getSource() == amountField) {
                    fromAccountField.requestFocus();
                } else if (event.getSource() == fromAccountField) {
                    toAccountField.requestFocus();
                }
            }
            event.consume();
        } else if (event.getCode() == KeyCode.ESCAPE) {
            handleClear();
            event.consume();
        }
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
        amountField.requestFocus(); // Focus back to first field
    }

    private void showAlert(AlertType type, String message) {
        Alert alert = new Alert(type, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
