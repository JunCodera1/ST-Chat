package me.chatapp.stchat.view.components.molecules.Item;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ContentDisplay;


import java.util.Objects;
import java.util.function.Consumer;

public class DirectMessageItem {

    private final HBox root;
    private final StackPane avatarContainer;
    private final ImageView avatarImageView;
    private final Circle statusIndicator;
    private final Label nameLabel;
    private final Label unreadBadge;
    private boolean isOnline;
    private String unreadCount;
    private Runnable onAction;

    private Consumer<String> onRemoveClicked;
    private Consumer<String> onViewProfileClicked;

    private final MenuButton optionsButton;


    public DirectMessageItem(String name, String avatarUrl, boolean isOnline, String unreadCount) {
        this.isOnline = isOnline;
        this.unreadCount = unreadCount;

        this.root = new HBox();
        this.avatarContainer = new StackPane();
        this.avatarImageView = createRoundedAvatarImageView(avatarUrl);
        this.statusIndicator = new Circle(4);
        this.nameLabel = new Label(name);
        this.unreadBadge = new Label();

        this.optionsButton = new MenuButton("...");
        initializeOptionsMenu(name);


        initializeComponent();
        updateAppearance();
    }

    private void initializeOptionsMenu(String name) {
        MenuItem viewProfileItem = new MenuItem("View Profile");
        MenuItem removeItem = new MenuItem("Remove from favorites");

        viewProfileItem.setOnAction(e -> {
            if (onViewProfileClicked != null) {
                onViewProfileClicked.accept(name);
            }
        });

        removeItem.setOnAction(e -> {
            if (onRemoveClicked != null) {
                onRemoveClicked.accept(name);
            }
        });

        optionsButton.getItems().addAll(viewProfileItem, removeItem);
        optionsButton.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-text-fill: #b9bbbe; " +
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 0;"
        );
        optionsButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }

    private void initializeComponent() {
        root.setAlignment(Pos.CENTER_LEFT);
        root.setSpacing(10);
        root.setPadding(new Insets(6, 12, 6, 12));
        root.setMinHeight(32);

        statusIndicator.setFill(isOnline ? Color.web("#43b581") : Color.web("#747f8d"));
        statusIndicator.setStroke(Color.web("#1a1d21"));
        statusIndicator.setStrokeWidth(2);

        // Position status indicator
        avatarContainer.getChildren().addAll(avatarImageView, statusIndicator);
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
            root.getChildren().addAll(avatarContainer, nameLabel, spacer, unreadBadge, optionsButton);
        } else {
            root.getChildren().addAll(avatarContainer, nameLabel, spacer, optionsButton);
        }

        // Click com.stchat.server.handler
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
    private ImageView createRoundedAvatarImageView(String avatarUrl) {
        Image image;

        try {
            if (avatarUrl == null || avatarUrl.isBlank()) {
                throw new IllegalArgumentException("Avatar URL is null or blank");
            }

            image = new Image(avatarUrl, 32, 32, true, true);
            if (image.isError()) {
                throw new RuntimeException("Failed to load image from URL: " + avatarUrl);
            }

        } catch (Exception e) {
            image = new Image(Objects.requireNonNull(getClass().getResource("/image/default_avatar.png")).toExternalForm());
        }

        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(32);
        imageView.setFitHeight(32);

        Circle clip = new Circle(16, 16, 16);
        imageView.setClip(clip);

        return imageView;
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
    public void setOnRemoveClicked(Consumer<String> onRemoveClicked) {
        this.onRemoveClicked = onRemoveClicked;
    }

    public void setOnViewProfileClicked(Consumer<String> onViewProfileClicked) {
        this.onViewProfileClicked = onViewProfileClicked;
    }
    public void setOnAction(Runnable action) {
        this.onAction = action;
    }

    public HBox getComponent() {
        return root;
    }
}