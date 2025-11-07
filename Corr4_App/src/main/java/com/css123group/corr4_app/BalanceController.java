package com.css123group.corr4_app;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class BalanceController {

    @FXML
    private Label amountLabel;

    @FXML
    private void initialize() {
        // amountLabel can be updated programmatically when data is available
        // amountLabel.setText("Amount: PHP 35,000,000.00");
    }

    // optional method to update amount from backend later
    public void setAmount(String amountText) {
        amountLabel.setText(amountText);
    }
}
