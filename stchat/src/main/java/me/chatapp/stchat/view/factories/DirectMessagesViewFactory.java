package me.chatapp.stchat.view.factories;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;

public class DirectMessagesViewFactory {

    public static VBox create(Runnable onBackClicked) {
        VBox content = new VBox();
        content.setSpacing(10);
        content.setPadding(new Insets(15));
        content.setStyle("-fx-background-color: #1a1d21;");

        // Header
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Button backButton = new Button("←");
        backButton.setStyle(
                "-fx-background-color: #2f3136;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-radius: 50%;" +
                        "-fx-background-radius: 50%;"
        );
        backButton.setOnAction(e -> onBackClicked.run());

        Label titleLabel = new Label("Direct Messages");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        header.getChildren().addAll(backButton, titleLabel);

        // Message list
        VBox messagesList = new VBox(5);
        messagesList.setPadding(new Insets(10, 0, 0, 0));

        Label msg1 = new Label("📨 Recent conversations");
        msg1.setStyle("-fx-text-fill: #72767d; -fx-font-size: 12px;");

        Label msg2 = new Label("💬 user3 - Hello there!");
        msg2.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        messagesList.getChildren().addAll(msg1, msg2);

        content.getChildren().addAll(header, createSeparator(), messagesList);
        return content;
    }

    private static Region createSeparator() {
        Region separator = new Region();
        separator.setPrefHeight(1);
        separator.setStyle("-fx-background-color: #40444b;");
        VBox.setMargin(separator, new Insets(10, 0, 10, 0));
        return separator;
    }
}
