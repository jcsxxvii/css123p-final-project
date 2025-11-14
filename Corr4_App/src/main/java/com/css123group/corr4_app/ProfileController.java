package com.css123group.corr4_app;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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
        setupInlineEditing();
        setupKeyboardNavigation();
    }

    private void setupInlineEditing() {
        // Make labels editable on double-click
        makeLabelEditable(emailLabel, "Email");
        makeLabelEditable(phoneLabel, "Phone");
        makeLabelEditable(accountTypeLabel, "Account Type");
        makeLabelEditable(addressLabel, "Address");
    }

    private void makeLabelEditable(Label label, String fieldType) {
        label.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double-click
                startEditing(label, fieldType);
            }
        });
    }

    private void startEditing(Label label, String fieldType) {
        // Remove any existing editing
        cancelEditing();

        // Create text field for editing
        TextField textField = new TextField(label.getText().replace(fieldType + ": ", ""));
        textField.setStyle("-fx-font-size: 14px; -fx-pref-width: 300px;");

        // Replace label with text field in parent container
        HBox parent = (HBox) label.getParent();
        int index = parent.getChildren().indexOf(label);
        parent.getChildren().set(index, textField);

        // Focus and select all text
        textField.requestFocus();
        textField.selectAll();

        // Set up keyboard events for the text field
        textField.setOnKeyPressed(event -> handleEditKeyPress(event, textField, label, fieldType));

        // Handle focus loss
        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                finishEditing(textField, label, fieldType);
            }
        });

        activeTextField = textField;
        activeLabel = label;
    }

    private void handleEditKeyPress(KeyEvent event, TextField textField, Label label, String fieldType) {
        switch (event.getCode()) {
            case ENTER:
                finishEditing(textField, label, fieldType);
                break;
            case ESCAPE:
                cancelEditing();
                break;
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

