package me.chatapp.stchat.view.factories;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import me.chatapp.stchat.model.User;

public class ProfileViewFactory {
    public static VBox create(User currentUser, Runnable onBackClicked) {
        VBox content = new VBox();
        content.setSpacing(10);
        content.setPadding(new Insets(15));
        content.setStyle("-fx-background-color: #1a1d21;");

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Button backButton = new Button("←");
        backButton.setStyle("-fx-background-color: #2f3136; -fx-text-fill: white;");
        backButton.setOnAction(e -> onBackClicked.run());

        Label title = new Label("Profile");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        header.getChildren().addAll(backButton, title);

        VBox info = new VBox(10);
        if (currentUser != null) {
            Label name = new Label("👤 " + currentUser.getUsername());
            name.setStyle("-fx-text-fill: white;");
            Label status = new Label("🟢 Online");
            status.setStyle("-fx-text-fill: #43b581;");
            info.getChildren().addAll(name, status);
        }

        content.getChildren().addAll(header, info);
        return content;
    }
}

