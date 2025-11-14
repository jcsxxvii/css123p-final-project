package com.css123group.corr4_app;

import java.util.List;

import com.css123group.corr4_be.Account;
import com.css123group.corr4_be.Customer;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox; // Import for Customer object
import javafx.scene.layout.VBox; // Import for Account object

public class ProfileController {

    @FXML private VBox profileContainer;
    @FXML private Label emailLabel;
    @FXML private Label phoneLabel;
    @FXML private Label accountTypeLabel;
    @FXML private Label addressLabel;

    private TextField activeTextField;
    private Label activeLabel;

    @FXML
    private void initialize() {
        loadProfileData(); // <-- NEW: Load data when initialized
        setupInlineEditing();
        setupKeyboardNavigation();
    }

    /**
     * Loads the current customer's profile data from the SessionManager and populates the labels.
     */
    private void loadProfileData() {
        SessionManager sessionManager = SessionManager.getInstance();
        Customer customer = sessionManager.getCurrentCustomer();
        List<Account> accounts = sessionManager.getCurrentAccounts();

        if (customer == null) {
            emailLabel.setText("Email: Not Logged In");
            return;
        }

        // Populate Customer Details
        emailLabel.setText("Email: " + customer.getEmail());
        // Use a ternary operator to handle potential nulls
        phoneLabel.setText("Phone: " + (customer.getPhone() != null ? customer.getPhone() : "N/A"));
        addressLabel.setText("Mailing Address: " + (customer.getAddress() != null ? customer.getAddress() : "N/A"));
        
        // Populate Account Type (using the first account)
        String accountType = "N/A";
        if (accounts != null && !accounts.isEmpty()) {
            accountType = accounts.get(0).getAccountType();
        }
        accountTypeLabel.setText("Account Type: " + accountType);
    }

    private void setupInlineEditing() {
        // Make labels editable on double-click
        makeLabelEditable(emailLabel, "Email");
        makeLabelEditable(phoneLabel, "Phone");
        makeLabelEditable(addressLabel, "Mailing Address"); // Removed Account Type as it's not a direct customer field to edit
    }

    private void makeLabelEditable(Label label, String fieldType) {
        label.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double-click
                startEditing(label, fieldType);
            }
        });
    }

    private void startEditing(Label label, String fieldType) {
        // Only allow one field to be edited at a time
        if (activeTextField != null) {
            cancelEditing();
        }

        activeLabel = label;
        activeTextField = new TextField();
        
        // Extract the current value by removing the fieldType prefix
        String currentValue = label.getText().replace(fieldType + ": ", "").trim();
        activeTextField.setText(currentValue);

        // Handle Enter key to finish editing
        activeTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                finishEditing(activeTextField, activeLabel, fieldType);
            }
        });
        
        // Replace the label with the text field in its parent container
        if (label.getParent() instanceof HBox) {
            HBox parent = (HBox) label.getParent();
            int index = parent.getChildren().indexOf(label);
            
            // Add style class to match other inputs (optional, adjust if needed)
            activeTextField.getStyleClass().add("text-field");
            activeTextField.setPrefWidth(250); // Set a reasonable width
            
            parent.getChildren().set(index, activeTextField);
            activeTextField.requestFocus();
            activeTextField.selectAll(); // Select all text for easy replacement
        }
    }

    private void finishEditing(TextField textField, Label label, String fieldType) {
        if (textField.getParent() instanceof HBox) {
            HBox parent = (HBox) textField.getParent();
            int index = parent.getChildren().indexOf(textField);

            // Update label with new value
            String newValue = textField.getText().trim();
            if (!newValue.isEmpty()) {
                label.setText(fieldType + ": " + newValue);
                // Here you would typically save to database
                System.out.println(fieldType + " updated to: " + newValue);
            }

            parent.getChildren().set(index, label);
        }
        activeTextField = null;
        activeLabel = null;
    }

    private void cancelEditing() {
        if (activeTextField != null && activeLabel != null) {
            if (activeTextField.getParent() instanceof HBox) {
                HBox parent = (HBox) activeTextField.getParent();
                int index = parent.getChildren().indexOf(activeTextField);
                parent.getChildren().set(index, activeLabel);
            }
            activeTextField = null;
            activeLabel = null;
        }
    }

    private void setupKeyboardNavigation() {
        profileContainer.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                cancelEditing();
            }
        });
    }
}