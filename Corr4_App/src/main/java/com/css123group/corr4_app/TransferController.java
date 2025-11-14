package com.css123group.corr4_app;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import java.text.DecimalFormat;

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
        String amountStr = amountField.getText().trim();
        String from = fromAccountField.getText().trim();
        String to = toAccountField.getText().trim();

        // Validation
        if (amountStr.isEmpty() || from.isEmpty() || to.isEmpty()) {
            showAlert(AlertType.WARNING, "Please fill all fields.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                showAlert(AlertType.WARNING, "Amount must be positive.");
                return;
            }

            // Success - show confirmation
            String formattedAmount = df.format(amount);
            showAlert(AlertType.INFORMATION,
                    "Transfer Request Submitted:\n" +
                            "From: " + from + "\n" +
                            "To: " + to + "\n" +
                            "Amount: PHP " + formattedAmount);

            // Clear fields after successful submission
            handleClear();

        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Invalid amount format. Please enter a valid number.");
        }
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

