package me.chatapp.stchat.view.components.atoms;

import javafx.scene.control.PasswordField;

public class STPasswordField extends PasswordField {
    public STPasswordField(String promptText) {
        super();
        setPromptText(promptText);
        setPrefHeight(45);
        setStyle(getDefaultStyle());
        setupFocusEffects();
    }

    private String getDefaultStyle() {
        return "-fx-background-color: #f7fafc;" +
                "-fx-border-color: #e2e8f0;" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 12;" +
                "-fx-background-radius: 12;" +
                "-fx-padding: 0 15;" +
                "-fx-font-size: 14;";
    }

    private String getFocusStyle() {
        return "-fx-background-color: #ffffff;" +
                "-fx-border-color: #667eea;" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 12;" +
                "-fx-background-radius: 12;" +
                "-fx-padding: 0 15;" +
                "-fx-font-size: 14;" +
                "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.3), 10, 0, 0, 2);";
    }

    private void setupFocusEffects() {
        focusedProperty().addListener((obs, oldVal, newVal) ->
                setStyle(newVal ? getFocusStyle() : getDefaultStyle()));
    }
}
