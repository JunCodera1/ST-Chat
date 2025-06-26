package me.chatapp.stchat.view.components.molecules.Item;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class ChannelItem {

    private final HBox root;
    private final Label prefixLabel;
    private final Label nameLabel;
    private final Label unreadBadge;
    private final Circle notificationDot;
    private boolean isActive;
    private boolean hasNotification;
    private String unreadCount;
    private Runnable onAction;

    public ChannelItem(String name, String prefix, boolean isActive, boolean hasNotification, String unreadCount) {
        this.isActive = isActive;
        this.hasNotification = hasNotification;
        this.unreadCount = unreadCount;

        this.root = new HBox();
        this.prefixLabel = new Label(prefix);
        this.nameLabel = new Label(name);
        this.unreadBadge = new Label();
        this.notificationDot = new Circle(4);

        initializeComponent();
        updateAppearance();
    }

    private void initializeComponent() {
        root.setAlignment(Pos.CENTER_LEFT);
        root.setSpacing(8);
        root.setPadding(new Insets(6, 12, 6, 12));
        root.setMinHeight(32);

        // Prefix styling (# symbol)
        prefixLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        prefixLabel.setMinWidth(16);

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
            root.getChildren().addAll(prefixLabel, nameLabel, spacer, unreadBadge);
        } else if (hasNotification) {
            notificationDot.setFill(Color.web("#f23f42"));
            root.getChildren().addAll(prefixLabel, nameLabel, spacer, notificationDot);
        } else {
            root.getChildren().addAll(prefixLabel, nameLabel, spacer);
        }

        // Click com.stchat.server.handler
        root.setOnMouseClicked(e -> {
            if (onAction != null) {
                onAction.run();
            }
        });

        // Hover effects
        root.setOnMouseEntered(e -> {
            if (!isActive) {
                root.setStyle(
                        "-fx-background-color: #36393f; " +
                                "-fx-background-radius: 4px; " +
                                "-fx-cursor: hand;"
                );
            }
        });

        root.setOnMouseExited(e -> {
            if (!isActive) {
                root.setStyle(
                        "-fx-background-color: transparent; " +
                                "-fx-background-radius: 4px;"
                );
            }
        });
    }

    private void updateAppearance() {
        if (isActive) {
            root.setStyle(
                    "-fx-background-color: #42464d; " +
                            "-fx-background-radius: 4px;"
            );
            prefixLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");
            nameLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 600; -fx-text-fill: white;");
        } else {
            root.setStyle(
                    "-fx-background-color: transparent; " +
                            "-fx-background-radius: 4px;"
            );
            prefixLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #8e9297;");
            nameLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 500; -fx-text-fill: #8e9297;");
        }
    }

    public void setActive(boolean active) {
        this.isActive = active;
        updateAppearance();
    }

    public void setNotification(boolean hasNotification) {
        this.hasNotification = hasNotification;
        // You might want to rebuild the component here if notification state changes
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