package me.chatapp.stchat.view.components;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import me.chatapp.stchat.view.core.SceneManager;
import me.chatapp.stchat.view.pages.ProfilePage;

public class HeaderComponent {
    private final VBox headerContainer;
    private final Label titleLabel;
    private final Label subtitleLabel;
    private final Button profileButton;

    public HeaderComponent() {
        headerContainer = new VBox();
        headerContainer.getStyleClass().add("header-panel");
        headerContainer.setAlignment(Pos.CENTER_LEFT);
        headerContainer.setSpacing(5);

        titleLabel = new Label("ST Chat Application");
        titleLabel.getStyleClass().add("app-title");

        subtitleLabel = new Label("Modern Chat Interface");
        subtitleLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.8); -fx-font-size: 14px;");

        // Online indicator (future feature)
        HBox statusBox = new HBox(10);
        statusBox.setAlignment(Pos.CENTER_LEFT);

        Label onlineIndicator = new Label("●");
        onlineIndicator.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 12px;");

        Label onlineLabel = new Label("Ready to connect");
        onlineLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.7); -fx-font-size: 12px;");

        statusBox.getChildren().addAll(onlineIndicator, onlineLabel);

        // Profile button
        profileButton = new Button("Profile");
        profileButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 12px;");
        profileButton.setOnAction(event -> {
            VBox profilePage = new ProfilePage().getPage(); // Tạo layout trang Profile
            SceneManager.switchScene(profilePage);
            System.out.println("Navigating to Profile Page...");
        });

        // Container cho status và nút profile
        HBox bottomBox = new HBox(20, statusBox, profileButton);
        bottomBox.setAlignment(Pos.CENTER_LEFT);

        headerContainer.getChildren().addAll(titleLabel, subtitleLabel, bottomBox);
    }

    public VBox getComponent() {
        return headerContainer;
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    public void setSubtitle(String subtitle) {
        subtitleLabel.setText(subtitle);
    }

    public void updateOnlineStatus(boolean online, String status) {
        // Find and update online indicator
        HBox statusBox = (HBox) headerContainer.getChildren().get(2);
        Label indicator = (Label) statusBox.getChildren().get(0);
        Label label = (Label) statusBox.getChildren().get(1);

        if (online) {
            indicator.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 12px;");
            label.setText(status);
        } else {
            indicator.setStyle("-fx-text-fill: #FF5722; -fx-font-size: 12px;");
            label.setText(status);
        }
    }
}