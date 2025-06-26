package me.chatapp.stchat.view.components.atoms.TextField;

import javafx.scene.control.TextField;

public class MessageTextField extends TextField {
    public MessageTextField() {
        super();
        initializeStyle();
    }

    private void initializeStyle() {
        getStyleClass().add("message-text-field");
        setPromptText("Type your message...");
        setStyle("-fx-background-color: #f8f9fa; " +
                "-fx-border-color: #e9ecef; " +
                "-fx-border-radius: 25px; " +
                "-fx-background-radius: 25px; " +
                "-fx-padding: 12px 16px; " +
                "-fx-font-size: 14px; " +
                "-fx-pref-height: 50px;");

        // Focus effect
        focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                setStyle(getStyle() + "-fx-border-color: #007bff; -fx-border-width: 2px;");
            } else {
                setStyle(getStyle().replace("-fx-border-color: #007bff; -fx-border-width: 2px;", ""));
            }
        });
    }
}