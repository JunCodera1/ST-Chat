package me.chatapp.stchat.view.components.atoms.Field;

import javafx.scene.control.PasswordField;

public class StyledPasswordField extends PasswordField {
    private static final String FOCUS_STYLE =
            "-fx-background-color: #ffffff;" +
                    "-fx-border-color: #667eea;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 12;" +
                    "-fx-background-radius: 12;" +
                    "-fx-padding: 0 15;" +
                    "-fx-font-size: 14;" +
                    "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.3), 10, 0, 0, 2);";

    private static final String NORMAL_STYLE =
            "-fx-background-color: #f7fafc;" +
                    "-fx-border-color: #e2e8f0;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 12;" +
                    "-fx-background-radius: 12;" +
                    "-fx-padding: 0 15;" +
                    "-fx-font-size: 14;";

    public StyledPasswordField() {
        super();
        initializeStyles();
    }

    public StyledPasswordField(String promptText) {
        super();
        setPromptText(promptText);
        initializeStyles();
    }

    private void initializeStyles() {
        setPrefHeight(45);
        setStyle(NORMAL_STYLE);

        focusedProperty().addListener((obs, oldVal, newVal) -> {
            setStyle(newVal ? FOCUS_STYLE : NORMAL_STYLE);
        });
    }
}