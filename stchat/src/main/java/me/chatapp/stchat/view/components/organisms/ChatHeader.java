package me.chatapp.stchat.view.components.organisms;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import me.chatapp.stchat.view.core.SceneManager;
import me.chatapp.stchat.view.components.templates.ProfilePage;

public class ChatHeader {
    private final HBox headerContainer;
    private final Label conversationName;
    private final Label onlineStatus;
    private final Button callButton;
    private final Button videoCallButton;
    private final Button infoButton;
    private final StackPane avatarPane;
    private final Label avatarInitials;
    private final Circle avatar;
    private final Button profileButton;


    private Runnable onCallAction;
    private Runnable onVideoCallAction;
    private Runnable onInfoAction;

    public ChatHeader() {
        headerContainer = new HBox();
        headerContainer.setAlignment(Pos.CENTER_LEFT);
        headerContainer.setPadding(new Insets(12, 16, 12, 16));
        headerContainer.setSpacing(12);
        headerContainer.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #e4e6ea;
            -fx-border-width: 0 0 1 0;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 3, 0, 0, 1);
            """);

        // Avatar
        avatar = new Circle(20);
        avatar.setFill(Color.web("#42b883"));
        avatarPane = new StackPane();
        avatarPane.getChildren().add(avatar);

        avatarInitials = new Label("?");
        avatarInitials.setTextFill(Color.WHITE);
        avatarInitials.setFont(Font.font("System", FontWeight.BOLD, 12));
        avatarPane.getChildren().add(avatarInitials);

        // Conversation info
        VBox conversationInfo = new VBox(2);
        conversationName = new Label("Select a conversation");
        conversationName.setFont(Font.font("System", FontWeight.BOLD, 16));
        conversationName.setTextFill(Color.web("#050505"));

        onlineStatus = new Label("Click on a conversation to start chatting");
        onlineStatus.setFont(Font.font("System", 13));
        onlineStatus.setTextFill(Color.web("#65676b"));

        conversationInfo.getChildren().addAll(conversationName, onlineStatus);
        HBox.setHgrow(conversationInfo, Priority.ALWAYS);

        // Action buttons
        HBox actionButtons = new HBox(8);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);

        // Call button
        callButton = createActionButton("📞");
        callButton.setOnAction(e -> {
            if (onCallAction != null) onCallAction.run();
        });

        // Video call button
        videoCallButton = createActionButton("📹");
        videoCallButton.setOnAction(e -> {
            if (onVideoCallAction != null) onVideoCallAction.run();
        });

        // Info button
        infoButton = createActionButton("ℹ️");
        infoButton.setOnAction(e -> {
            if (onInfoAction != null) onInfoAction.run();
        });

        actionButtons.getChildren().addAll(callButton, videoCallButton, infoButton);

        headerContainer.getChildren().addAll(avatarPane, conversationInfo, actionButtons);

        // Initially disable buttons until a conversation is selected
        setButtonsEnabled(false);
        profileButton = new Button("Profile");
        profileButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 12px;");
        profileButton.setOnAction(event -> {
            BorderPane profilePage = new ProfilePage().getPage(); // Tạo layout trang Profile
            SceneManager.switchScene(profilePage);
            System.out.println("Navigating to Profile Page...");
        });
    }

    private Button createActionButton(String text) {
        Button button = new Button(text);
        button.setPrefSize(40, 40);
        button.setMinSize(40, 40);
        button.setMaxSize(40, 40);
        button.setStyle("""
            -fx-background-color: transparent;
            -fx-background-radius: 20;
            -fx-border-radius: 20;
            -fx-font-size: 16;
            -fx-cursor: hand;
            """);

        // Hover effect
        button.setOnMouseEntered(e -> {
            if (!button.isDisabled()) {
                button.setStyle("""
                    -fx-background-color: #f0f2f5;
                    -fx-background-radius: 20;
                    -fx-border-radius: 20;
                    -fx-font-size: 16;
                    -fx-cursor: hand;
                    """);
            }
        });

        button.setOnMouseExited(e -> {
            if (!button.isDisabled()) {
                button.setStyle("""
                    -fx-background-color: transparent;
                    -fx-background-radius: 20;
                    -fx-border-radius: 20;
                    -fx-font-size: 16;
                    -fx-cursor: hand;
                    """);
            }
        });

        return button;
    }

    public void setActiveConversation(String conversationName) {
        this.conversationName.setText(conversationName);
        this.onlineStatus.setText("Active now");

        // Update avatar
        if (conversationName != null && !conversationName.isEmpty()) {
            String initials = getInitials(conversationName);
            avatarInitials.setText(initials);
            avatar.setFill(getAvatarColor(conversationName));
            setButtonsEnabled(true);
        } else {
            avatarInitials.setText("?");
            avatar.setFill(Color.web("#42b883"));
            setButtonsEnabled(false);
        }
    }

    public void setOnlineStatus(String status) {
        this.onlineStatus.setText(status);
    }

    public void setConversationName(String name) {
        this.conversationName.setText(name);
    }

    private String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "?";
        }

        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        } else {
            return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
        }
    }

    private Color getAvatarColor(String name) {
        // Generate a consistent color based on the name
        String[] colors = {
                "#42b883", "#1877f2", "#fd79a8", "#6c5ce7",
                "#a29bfe", "#fd63c3", "#ee5a24", "#00b894",
                "#0984e3", "#e17055", "#81ecec", "#fab1a0"
        };

        int hash = Math.abs(name.hashCode());
        return Color.web(colors[hash % colors.length]);
    }

    private void setButtonsEnabled(boolean enabled) {
        callButton.setDisable(!enabled);
        videoCallButton.setDisable(!enabled);
        infoButton.setDisable(!enabled);

        String disabledStyle = """
            -fx-background-color: transparent;
            -fx-background-radius: 20;
            -fx-border-radius: 20;
            -fx-font-size: 16;
            -fx-opacity: 0.5;
            """;

        String enabledStyle = """
            -fx-background-color: transparent;
            -fx-background-radius: 20;
            -fx-border-radius: 20;
            -fx-font-size: 16;
            -fx-cursor: hand;
            """;

        if (!enabled) {
            callButton.setStyle(disabledStyle);
            videoCallButton.setStyle(disabledStyle);
            infoButton.setStyle(disabledStyle);
        } else {
            callButton.setStyle(enabledStyle);
            videoCallButton.setStyle(enabledStyle);
            infoButton.setStyle(enabledStyle);
        }
    }

    // Event handlers
    public void setOnCallAction(Runnable action) {
        this.onCallAction = action;
    }

    public void setOnVideoCallAction(Runnable action) {
        this.onVideoCallAction = action;
    }

    public void setOnInfoAction(Runnable action) {
        this.onInfoAction = action;
    }

    // Getters
    public HBox getComponent() {
        return headerContainer;
    }

    public String getConversationName() {
        return conversationName.getText();
    }

    public String getOnlineStatus() {
        return onlineStatus.getText();
    }
}