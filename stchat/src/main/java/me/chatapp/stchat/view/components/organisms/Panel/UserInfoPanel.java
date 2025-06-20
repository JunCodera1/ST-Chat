package me.chatapp.stchat.view.components.organisms.Panel;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import me.chatapp.stchat.model.User;

public class UserInfoPanel {
    private final VBox container;
    private final HBox userInfoContainer;
    private final StackPane avatarPane;
    private final Circle avatar;
    private final Label avatarInitials;
    private final Label usernameLabel;
    private final Label statusLabel;
    private final MenuButton settingsButton;

    private User currentUser;
    private Runnable onLogoutAction;
    private Runnable onSettingsAction;
    private Runnable onProfileAction;

    public UserInfoPanel() {
        container = new VBox();
        container.setPadding(new Insets(16));
        container.setSpacing(8);
        container.setStyle("""
            -fx-background-color: #f0f2f5;
            -fx-border-color: #e4e6ea;
            -fx-border-width: 0 0 1 0;
            """);

        userInfoContainer = new HBox();
        userInfoContainer.setAlignment(Pos.CENTER_LEFT);
        userInfoContainer.setSpacing(12);

        // Avatar
        avatar = new Circle(24);
        avatar.setFill(Color.web("#42b883"));
        avatarPane = new StackPane();
        avatarPane.getChildren().add(avatar);

        avatarInitials = new Label("?");
        avatarInitials.setTextFill(Color.WHITE);
        avatarInitials.setFont(Font.font("System", FontWeight.BOLD, 14));
        avatarPane.getChildren().add(avatarInitials);

        // User info
        VBox userDetails = new VBox(2);
        usernameLabel = new Label("Not logged in");
        usernameLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        usernameLabel.setTextFill(Color.web("#050505"));

        statusLabel = new Label("Please log in to continue");
        statusLabel.setFont(Font.font("System", 12));
        statusLabel.setTextFill(Color.web("#65676b"));

        userDetails.getChildren().addAll(usernameLabel, statusLabel);
        HBox.setHgrow(userDetails, Priority.ALWAYS);

        // Settings menu button
        settingsButton = new MenuButton("⚙️");
        settingsButton.setPrefSize(32, 32);
        settingsButton.setMinSize(32, 32);
        settingsButton.setMaxSize(32, 32);
        settingsButton.setStyle("""
            -fx-background-color: transparent;
            -fx-background-radius: 16;
            -fx-border-radius: 16;
            -fx-font-size: 14;
            -fx-cursor: hand;
            """);

        // Settings menu items
        MenuItem profileItem = new MenuItem("👤 Profile");
        profileItem.setOnAction(e -> {
            if (onProfileAction != null) onProfileAction.run();
        });

        MenuItem settingsItem = new MenuItem("⚙️ Settings");
        settingsItem.setOnAction(e -> {
            if (onSettingsAction != null) onSettingsAction.run();
        });

        MenuItem logoutItem = new MenuItem("🚪 Logout");
        logoutItem.setOnAction(e -> {
            if (onLogoutAction != null) onLogoutAction.run();
        });

        settingsButton.getItems().addAll(profileItem, settingsItem, logoutItem);

        // Hover effect for settings button
        settingsButton.setOnMouseEntered(e -> {
            if (!settingsButton.isDisabled()) {
                settingsButton.setStyle("""
                    -fx-background-color: #e4e6ea;
                    -fx-background-radius: 16;
                    -fx-border-radius: 16;
                    -fx-font-size: 14;
                    -fx-cursor: hand;
                    """);
            }
        });

        settingsButton.setOnMouseExited(e -> {
            if (!settingsButton.isDisabled()) {
                settingsButton.setStyle("""
                    -fx-background-color: transparent;
                    -fx-background-radius: 16;
                    -fx-border-radius: 16;
                    -fx-font-size: 14;
                    -fx-cursor: hand;
                    """);
            }
        });

        userInfoContainer.getChildren().addAll(avatarPane, userDetails, settingsButton);
        container.getChildren().add(userInfoContainer);

        // Initially disable settings button
        settingsButton.setDisable(true);
        settingsButton.setVisible(false);
    }

    public void setUser(User user) {
        this.currentUser = user;

        if (user != null) {
            usernameLabel.setText(user.getUsername());
            statusLabel.setText("Online");

            // Update avatar
            String initials = getInitials(user.getUsername());
            avatarInitials.setText(initials);
            avatar.setFill(getAvatarColor(user.getUsername()));

            // Enable settings button
            settingsButton.setDisable(false);
            settingsButton.setVisible(true);
        } else {
            clearUser();
        }
    }

    public void clearUser() {
        this.currentUser = null;
        usernameLabel.setText("Not logged in");
        statusLabel.setText("Please log in to continue");
        avatarInitials.setText("?");
        avatar.setFill(Color.web("#42b883"));

        // Disable settings button
        settingsButton.setDisable(true);
        settingsButton.setVisible(false);
    }

    public void setStatus(String status) {
        statusLabel.setText(status);
    }

    public void setOnlineStatus(boolean online) {
        if (currentUser != null) {
            statusLabel.setText(online ? "Online" : "Offline");
            statusLabel.setTextFill(Color.web(online ? "#42b883" : "#65676b"));
        }
    }

    private String getInitials(String username) {
        if (username == null || username.trim().isEmpty()) {
            return "?";
        }

        String[] parts = username.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        } else {
            return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
        }
    }

    private Color getAvatarColor(String username) {
        // Generate a consistent color based on the username
        String[] colors = {
                "#42b883", "#1877f2", "#fd79a8", "#6c5ce7",
                "#a29bfe", "#fd63c3", "#ee5a24", "#00b894",
                "#0984e3", "#e17055", "#81ecec", "#fab1a0"
        };

        int hash = Math.abs(username.hashCode());
        return Color.web(colors[hash % colors.length]);
    }

    // Event handlers
    public void setOnLogoutAction(Runnable action) {
        this.onLogoutAction = action;
    }

    public void setOnSettingsAction(Runnable action) {
        this.onSettingsAction = action;
    }

    public void setOnProfileAction(Runnable action) {
        this.onProfileAction = action;
    }

    // Getters
    public VBox getComponent() {
        return container;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public String getUsername() {
        return currentUser != null ? currentUser.getUsername() : null;
    }

    public boolean isUserLoggedIn() {
        return currentUser != null;
    }

    // Additional utility methods
    public void showConnectionStatus(boolean connected) {
        if (currentUser != null) {
            setStatus(connected ? "Online" : "Connecting...");
            setOnlineStatus(connected);
        }
    }

    public void addQuickAction(String text, Runnable action) {
        Button quickButton = new Button(text);
        quickButton.setStyle("""
            -fx-background-color: #1877f2;
            -fx-text-fill: white;
            -fx-background-radius: 6;
            -fx-border-radius: 6;
            -fx-font-size: 12;
            -fx-padding: 4 8 4 8;
            -fx-cursor: hand;
            """);

        quickButton.setOnAction(e -> {
            if (action != null) action.run();
        });

        // Hover effect
        quickButton.setOnMouseEntered(e -> {
            quickButton.setStyle("""
                -fx-background-color: #166fe5;
                -fx-text-fill: white;
                -fx-background-radius: 6;
                -fx-border-radius: 6;
                -fx-font-size: 12;
                -fx-padding: 4 8 4 8;
                -fx-cursor: hand;
                """);
        });

        quickButton.setOnMouseExited(e -> {
            quickButton.setStyle("""
                -fx-background-color: #1877f2;
                -fx-text-fill: white;
                -fx-background-radius: 6;
                -fx-border-radius: 6;
                -fx-font-size: 12;
                -fx-padding: 4 8 4 8;
                -fx-cursor: hand;
                """);
        });

        container.getChildren().add(quickButton);
    }
}