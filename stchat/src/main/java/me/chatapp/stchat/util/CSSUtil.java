package me.chatapp.stchat.util;

import me.chatapp.stchat.view.components.pages.ChangePassword;

import java.util.logging.Logger;

public class CSSUtil {
    public static final String GRADIENT_BACKGROUND = "-fx-background-color: linear-gradient(135deg, #667eea 0%, #764ba2 100%);";
    public static final String STYLE_CHAT_CONTAINER = "-fx-background-color: #ffffff;";
    public static final String STYLE_MESSAGE_CONTAINER = "-fx-background-color: #ffffff;";
    public static final String STYLE_EMPTY_STATE_LABEL = "-fx-text-fill: #9e9e9e; -fx-font-size: 14px;";
    public static final String STYLE_SCROLL_PANE = "-fx-background: #ffffff; -fx-background-color: transparent;";
    public static final String STYLE_CHAT_TITLE = "-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;";
    public static final String STYLE_MESSAGE_COUNT = "-fx-text-fill: #7f8c8d; -fx-font-size: 12px; -fx-font-weight: 500;";
    public static final String STYLE_SEPARATOR = "-fx-background-color: #ecf0f1;";
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

}
