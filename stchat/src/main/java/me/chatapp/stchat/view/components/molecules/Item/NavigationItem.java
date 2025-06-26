package me.chatapp.stchat.view.components.molecules.Item;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class NavigationItem {

    private final HBox root;
    private final Label iconLabel;
    private final Label textLabel;
    private boolean isActive;
    private Runnable onAction;

    public NavigationItem(String icon, String text, boolean isActive) {
        this.isActive = isActive;
        this.root = new HBox();
        this.iconLabel = new Label(icon);
        this.textLabel = new Label(text);

        initializeComponent();
        updateAppearance();
    }

    private void initializeComponent() {
        root.setAlignment(Pos.CENTER_LEFT);
        root.setSpacing(12);
        root.setPadding(new Insets(8, 12, 8, 12));
        root.setMinHeight(36);

        // Icon styling
        iconLabel.setStyle("-fx-font-size: 16px;");
        iconLabel.setMinWidth(20);

        // Text styling
        textLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: 500;");

        // Add spacing
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        root.getChildren().addAll(iconLabel, textLabel, spacer);

        // Add click com.stchat.server.handler
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
                                "-fx-background-radius: 6px; " +
                                "-fx-cursor: hand;"
                );
            }
        });

        root.setOnMouseExited(e -> {
            if (!isActive) {
                root.setStyle(
                        "-fx-background-color: transparent; " +
                                "-fx-background-radius: 6px;"
                );
            }
        });
    }

    private void updateAppearance() {
        if (isActive) {
            root.setStyle(
                    "-fx-background-color: #5865f2; " +
                            "-fx-background-radius: 6px;"
            );
            iconLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
            textLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: white;");
        } else {
            root.setStyle(
                    "-fx-background-color: transparent; " +
                            "-fx-background-radius: 6px;"
            );
            iconLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #b9bbbe;");
            textLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: 500; -fx-text-fill: #b9bbbe;");
        }
    }

    public void setActive(boolean active) {
        this.isActive = active;
        updateAppearance();
    }

    public boolean isActive() {
        return isActive;
    }

    public void setOnAction(Runnable action) {
        this.onAction = action;
    }

    public HBox getComponent() {
        return root;
    }
}