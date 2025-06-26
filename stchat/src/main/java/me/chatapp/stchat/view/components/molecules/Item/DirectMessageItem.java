package me.chatapp.stchat.view.components.molecules.Item;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class DirectMessageItem {

    private final HBox root;
    private final StackPane avatarContainer;
    private final Circle avatar;
    private final Circle statusIndicator;
    private final Label nameLabel;
    private final Label unreadBadge;
    private boolean isOnline;
    private String unreadCount;
    private Runnable onAction;

    public DirectMessageItem(String name, String icon, boolean isOnline, String unreadCount) {
        this.isOnline = isOnline;
        this.unreadCount = unreadCount;

        this.root = new HBox();
        this.avatarContainer = new StackPane();
        this.avatar = new Circle(12);
        this.statusIndicator = new Circle(4);
        this.nameLabel = new Label(name);
        this.unreadBadge = new Label();

        initializeComponent();
        updateAppearance();
    }

    private void initializeComponent() {
        root.setAlignment(Pos.CENTER_LEFT);
        root.setSpacing(10);
        root.setPadding(new Insets(6, 12, 6, 12));
        root.setMinHeight(32);

        // Avatar setup
        avatar.setFill(Color.web("#7289da"));

        // Status indicator
        statusIndicator.setFill(isOnline ? Color.web("#43b581") : Color.web("#747f8d"));
        statusIndicator.setStroke(Color.web("#1a1d21"));
        statusIndicator.setStrokeWidth(2);

        // Position status indicator
        avatarContainer.getChildren().addAll(avatar, statusIndicator);
        StackPane.setAlignment(statusIndicator, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(statusIndicator, new Insets(0, -2, -2, 0));

        // Name styling
        nameLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 500;");

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Unread badge
        if (unreadCount != null && !unreadCount.isEmpty()) {
            unreadBadge.setText(unreadCount);
            unreadBadge.setStyle(
                    "-fx-background-color: #f23f42; " +
                            "-fx-text-fill: white; " +
                            "-fx-font-size: 11px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-background-radius: 10px; " +
                            "-fx-padding: 2px 6px; " +
                            "-fx-min-width: 18px; " +
                            "-fx-alignment: center;"
            );
            root.getChildren().addAll(avatarContainer, nameLabel, spacer, unreadBadge);
        } else {
            root.getChildren().addAll(avatarContainer, nameLabel, spacer);
        }

        // Click handler
        root.setOnMouseClicked(e -> {
            if (onAction != null) {
                onAction.run();
            }
        });

        // Hover effects
        root.setOnMouseEntered(e -> {
            root.setStyle(
                    "-fx-background-color: #36393f; " +
                            "-fx-background-radius: 4px; " +
                            "-fx-cursor: hand;"
            );
        });

        root.setOnMouseExited(e -> {
            root.setStyle(
                    "-fx-background-color: transparent; " +
                            "-fx-background-radius: 4px;"
            );
        });
    }

    private void updateAppearance() {
        root.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-background-radius: 4px;"
        );
        nameLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 500; -fx-text-fill: #b9bbbe;");

        // Update status indicator color
        statusIndicator.setFill(isOnline ? Color.web("#43b581") : Color.web("#747f8d"));
    }

    public void setOnline(boolean online) {
        this.isOnline = online;
        updateAppearance();
    }

    public void setUnreadCount(String count) {
        this.unreadCount = count;
        if (count != null && !count.isEmpty()) {
            unreadBadge.setText(count);
            unreadBadge.setVisible(true);
        } else {
            unreadBadge.setVisible(false);
        }
    }

    public void setOnAction(Runnable action) {
        this.onAction = action;
    }

    public HBox getComponent() {
        return root;
    }
}