package me.chatapp.stchat.util;

import javafx.scene.layout.VBox;
import me.chatapp.stchat.view.components.pages.ChangePassword;

import java.util.logging.Logger;

public class CSSUtil {
    public static final String GRADIENT_BACKGROUND = "-fx-background-color: linear-gradient(135deg, #667eea 0%, #764ba2 100%);";
    public static final String STYLE_CHAT_CONTAINER = "-fx-background-color: #ffffff;";
    public static final String STYLE_MESSAGE_CONTAINER = "-fx-background-color: #ffffff;";
    public static final String STYLE_EMPTY_STATE_LABEL = "-fx-text-fill: #9e9e9e; -fx-font-size: 14px;";
    public static final String STYLE_SCROLL_PANE = "-fx-background-color: transparent; -fx-background-insets: 0;";
    public static final String STYLE_CHAT_TITLE = "-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;";
    public static final String STYLE_MESSAGE_COUNT = "-fx-text-fill: #7f8c8d; -fx-font-size: 12px; -fx-font-weight: 500;";
    public static final String STYLE_SEPARATOR = "-fx-background-color: #ecf0f1;";
    public static final String BACKGROUND_COLOR = "#ffffff";
    public static final String PRIMARY_COLOR = "#007bff";
    public static final String SECONDARY_COLOR = "#6c757d";
    public static final String SUCCESS_COLOR = "#28a745";
    public static final String BORDER_COLOR = "#e9ecef";
    public static final String HOVER_COLOR = "#f8f9fa";
    public static final String TEXT_PRIMARY = "#212529";
    public static final String TEXT_SECONDARY = "#6c757d";

    public static final String STYLE_SENDER_LABEL = "-fx-font-weight: bold; -fx-font-size: 13px;";
    public static final String STYLE_TIME_LABEL = "-fx-text-fill: #000000; -fx-font-size: 11px;";
    public static final String STYLE_CONTENT_LABEL = "-fx-font-size: 14px; -fx-line-spacing: 2px;";
    public static final String STYLE_TYPING_LABEL = "-fx-text-fill: #7f8c8d; -fx-font-style: italic; -fx-font-size: 12px;";
    public static final String STYLE_TYPING_BOX = "-fx-background-color: #f8f9fa; -fx-background-radius: 18; -fx-opacity: 0.8;";
    public static final String FIELD_STYLE = "-fx-background-color: #f7fafc;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 12;" +
            "-fx-background-radius: 12;" +
            "-fx-padding: 0 15;" +
            "-fx-font-size: 14;";
    public static final String FOCUS_STYLE = "-fx-background-color: #ffffff;" +
            "-fx-border-color: #667eea;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 12;" +
            "-fx-background-radius: 12;" +
            "-fx-padding: 0 15;" +
            "-fx-font-size: 14;" +
            "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.3), 10, 0, 0, 2);";

    public static String getFieldStyle() {
        return "-fx-background-color: #f7fafc;" +
                "-fx-border-color: #e2e8f0;" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 12;" +
                "-fx-background-radius: 12;" +
                "-fx-padding: 0 15;" +
                "-fx-font-size: 14;";
    }

    public static String focusStyle =
            "-fx-background-color: #ffffff;" +
                    "-fx-border-color: #667eea;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 12;" +
                    "-fx-background-radius: 12;" +
                    "-fx-padding: 0 15;" +
                    "-fx-font-size: 14;" +
                    "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.3), 10, 0, 0, 2);";

    public static String normalStyle =
            "-fx-background-color: #f7fafc;" +
                    "-fx-border-color: #e2e8f0;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 12;" +
                    "-fx-background-radius: 12;" +
                    "-fx-padding: 0 15;" +
                    "-fx-font-size: 14;";

    public static final String REGISTER_BACKGROUND = "-fx-background-color: linear-gradient(135deg, #667eea 0%, #764ba2 100%);";
    public static final String LOGIN_BUTTON = "-fx-background-color: transparent;" +
            "-fx-text-fill: #667eea;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-font-size: 14;";
    public static String baseButtonStyle() {
        return "-fx-background-color: transparent;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px;" +
                "-fx-padding: 6 10;" +
                "-fx-cursor: hand;" +
                "-fx-background-radius: 4;";
    }

    public static String hoverButtonStyle() {
        return "-fx-background-color: #40444b;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px;" +
                "-fx-padding: 6 10;" +
                "-fx-cursor: hand;" +
                "-fx-background-radius: 4;";
    }

    public static void addHoverEffect(VBox attachmentBox) {
        attachmentBox.setOnMouseEntered(e -> {
            attachmentBox.setStyle(String.format(
                    "-fx-background-color: %s; " +
                            "-fx-background-radius: 12px; " +
                            "-fx-border-color: %s; " +
                            "-fx-border-radius: 12px; " +
                            "-fx-border-width: 1px; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 12, 0, 0, 4);",
                    BACKGROUND_COLOR, PRIMARY_COLOR
            ));
        });

        attachmentBox.setOnMouseExited(e -> {
            attachmentBox.setStyle(String.format(
                    "-fx-background-color: %s; " +
                            "-fx-background-radius: 12px; " +
                            "-fx-border-color: %s; " +
                            "-fx-border-radius: 12px; " +
                            "-fx-border-width: 1px; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);",
                    BACKGROUND_COLOR, BORDER_COLOR
            ));
        });
    }

}
