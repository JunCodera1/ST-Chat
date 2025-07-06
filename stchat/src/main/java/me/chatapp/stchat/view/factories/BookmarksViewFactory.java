package me.chatapp.stchat.view.factories;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class BookmarksViewFactory {

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

        Label titleLabel = new Label("Bookmarks");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        header.getChildren().addAll(backButton, titleLabel);

        // Bookmarks list
        VBox bookmarksList = new VBox(5);
        bookmarksList.setPadding(new Insets(10, 0, 0, 0));

        Label bookmarkInfo = new Label("🔖 Your bookmarks will appear here");
        bookmarkInfo.setStyle("-fx-text-fill: #72767d; -fx-font-size: 12px;");

        bookmarksList.getChildren().add(bookmarkInfo);

        content.getChildren().addAll(header, createSeparator(), bookmarksList);
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
