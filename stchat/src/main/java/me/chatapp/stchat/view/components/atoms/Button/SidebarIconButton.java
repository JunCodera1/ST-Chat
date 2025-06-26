package me.chatapp.stchat.view.components.atoms.Button;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * Atom component: Single icon button for sidebar navigation
 */
public class SidebarIconButton {

    private final StackPane container;
    private final Button button;
    private final FontIcon icon;
    private boolean isActive = false;
    private Runnable onAction;

    public SidebarIconButton(String iconCode, String tooltip) {
        this.container = new StackPane();
        this.button = new Button();
        this.icon = new FontIcon(iconCode);

        initializeButton(tooltip);
        setupStyling();
        setupEventHandlers();

        container.getChildren().add(button);
        container.setAlignment(Pos.CENTER);
    }

    private void initializeButton(String tooltip) {
        button.setGraphic(icon);
        button.setPrefSize(48, 48);
        button.setMinSize(48, 48);
        button.setMaxSize(48, 48);

        if (tooltip != null && !tooltip.isEmpty()) {
            button.setTooltip(new Tooltip(tooltip));
        }
    }

    private void setupStyling() {
        // Icon styling
        icon.setIconSize(20);
        icon.setIconColor(javafx.scene.paint.Color.web("#72767d"));

        // Button styling
        updateButtonStyle();

        // Container styling
        container.getStyleClass().add("sidebar-icon-container");
        container.setStyle(
                "-fx-background-radius: 12px; " +
                        "-fx-padding: 4px;"
        );
    }

    private void updateButtonStyle() {
        if (isActive) {
            button.setStyle(
                    "-fx-background-color: #5865f2; " +
                            "-fx-background-radius: 16px; " +
                            "-fx-border-radius: 16px; " +
                            "-fx-cursor: hand;"
            );
            icon.setIconColor(javafx.scene.paint.Color.WHITE);
        } else {
            button.setStyle(
                    "-fx-background-color: #36393f; " +
                            "-fx-background-radius: 24px; " +
                            "-fx-border-radius: 24px; " +
                            "-fx-cursor: hand;"
            );
            icon.setIconColor(javafx.scene.paint.Color.web("#72767d"));
        }
    }

    private void setupEventHandlers() {
        button.setOnAction(e -> {
            if (onAction != null) {
                onAction.run();
            }
        });

        // Hover effects
        button.setOnMouseEntered(e -> {
            if (!isActive) {
                button.setStyle(
                        "-fx-background-color: #5865f2; " +
                                "-fx-background-radius: 16px; " +
                                "-fx-border-radius: 16px; " +
                                "-fx-cursor: hand;"
                );
                icon.setIconColor(javafx.scene.paint.Color.WHITE);
            }
        });

        button.setOnMouseExited(e -> {
            if (!isActive) {
                updateButtonStyle();
            }
        });
    }

    public void setActive(boolean active) {
        this.isActive = active;
        updateButtonStyle();
    }

    public boolean isActive() {
        return isActive;
    }

    public void setOnAction(Runnable action) {
        this.onAction = action;
    }

    public StackPane getComponent() {
        return container;
    }

    public void setIconColor(javafx.scene.paint.Color color) {
        icon.setIconColor(color);
    }

    public void setIconSize(int size) {
        icon.setIconSize(size);
    }
}