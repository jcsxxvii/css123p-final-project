package com.bankapp.c0rr4;


import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class HelloController {


    @FXML
    private Label balanceLabel;


    @FXML
    public void initialize() {
        final double balance = 25000.15;


        // Right-click event on balance label
        balanceLabel.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.SECONDARY) {
                    showBalanceAndHistory(balance);
                }
            }
        });
    }


    private void showBalanceAndHistory(double balance) {
        // Create a new stage (window)
        Stage popupStage = new Stage();
        popupStage.setTitle("Balance Summary");


        Label balanceInfo = new Label("Available Balance: ₱" + balance);
        balanceInfo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #007bff;");


        Label header = new Label("Recent Transactions");
        header.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");


        // Sample transactions
        String[] transactions = {
                "🛒 Grocery - ₱1,200",
                "💼 Salary + ₱5,000",
                "💡 Bills - ₱500",
                "🚌 Transport - ₱300",
                "💸 Transfer + ₱2,000"
        };


        VBox list = new VBox(8);
        for (String t : transactions) {
            Label item = new Label(t);
            item.setStyle("-fx-font-size: 13px; -fx-text-fill: #444; "
                    + "-fx-background-color: #ffffff; -fx-padding: 6 10; "
                    + "-fx-background-radius: 6; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
            list.getChildren().add(item);
        }


        VBox layout = new VBox(12, balanceInfo, header, list);
        layout.setStyle("-fx-padding: 20; -fx-background-color: #f0f2f5;");


        Scene scene = new Scene(layout, 300, 300);
        popupStage.setScene(scene);
        popupStage.setResizable(true); // make the window resizable
        popupStage.show();
    }
}
