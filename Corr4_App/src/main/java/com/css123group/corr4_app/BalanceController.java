package com.css123group.corr4_app;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.css123group.corr4_be.Account;
import com.css123group.corr4_be.Customer;
import java.util.List;

public class BalanceController {

    @FXML
    private Label amountLabel;

    @FXML
    private ListView<String> accountListView;

    @FXML
    private void initialize() {
        // Load account information when controller initializes
        loadAccountData();
    }

    /**
     * Load account data from the session and display in the view
     */
    private void loadAccountData() {
        SessionManager sessionManager = SessionManager.getInstance();
        Customer customer = sessionManager.getCurrentCustomer();

        if (customer == null) {
            amountLabel.setText("Not logged in");
            return;
        }

        List<Account> accounts = sessionManager.getCurrentAccounts();

        if (accounts.isEmpty()) {
            amountLabel.setText("No accounts found");
            accountListView.setItems(FXCollections.observableArrayList("No accounts"));
            return;
        }

        // Display first account's balance
        Account firstAccount = accounts.get(0);
        if (firstAccount.getBalance() != null) {
            amountLabel.setText("Amount: PHP " + String.format("%,.2f", firstAccount.getBalance()));
        } else {
            amountLabel.setText("Amount: PHP 0.00");
        }

        // Populate account list
        ObservableList<String> accountItems = FXCollections.observableArrayList();
        for (Account account : accounts) {
            String accountInfo = String.format("%s (%s) - PHP %,.2f",
                account.getAccountNumber(),
                account.getAccountType(),
                account.getBalance() != null ? account.getBalance() : 0.00);
            accountItems.add(accountInfo);
        }
        accountListView.setItems(accountItems);
    }

    /**
     * Optional method to update amount from backend later
     */
    public void setAmount(String amountText) {
        amountLabel.setText(amountText);
    }
}
