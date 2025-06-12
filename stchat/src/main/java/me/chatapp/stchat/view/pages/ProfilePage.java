package me.chatapp.stchat.view.pages;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import me.chatapp.stchat.view.core.SceneManager;

public class ProfilePage {

    public VBox getPage() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #2c3e50, #34495e);");

        // Avatar
        ImageView avatar = new ImageView(new Image("https://www.gravatar.com/avatar/?d=mp&s=120"));
        avatar.setFitWidth(120);
        avatar.setFitHeight(120);
        avatar.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 5, 0.2, 0, 2);");

        // Name
        Label nameLabel = new Label("Minh Tiến");
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        nameLabel.setTextFill(Color.WHITE);

        // Email
        Label emailLabel = new Label("minhtien@example.com");
        emailLabel.setFont(Font.font("System", 14));
        emailLabel.setTextFill(Color.LIGHTGRAY);

        // Buttons
        Button editButton = new Button("✏️ Chỉnh sửa thông tin");
        Button logoutButton = new Button("🚪 Đăng xuất");
        Button backButton = new Button("🔙 Quay về");

        editButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        logoutButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        backButton.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white;");

        editButton.setOnAction(e -> {
            System.out.println("Edit profile clicked");
        });

        logoutButton.setOnAction(e -> {
            System.out.println("User logged out");
        });

        backButton.setOnAction(e -> {
            ChatView chatView = new ChatView();
            SceneManager.switchScene(chatView.getScene());
        });

        VBox buttonBox = new VBox(10, editButton, logoutButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);

        root.getChildren().addAll(avatar, nameLabel, emailLabel, buttonBox);

        return root;
    }
}
