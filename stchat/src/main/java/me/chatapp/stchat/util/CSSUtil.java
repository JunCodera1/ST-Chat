package me.chatapp.stchat.util;

import me.chatapp.stchat.view.components.pages.ChangePassword;

import java.util.logging.Logger;

public class CSSUtil {
    public static final String GRADIENT_BACKGROUND = "-fx-background-color: linear-gradient(135deg, #667eea 0%, #764ba2 100%);";
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

    public static final String REGISTER_BACKGROUND = "-fx-background-color: linear-gradient(135deg, #667eea 0%, #764ba2 100%);";

}
