package me.chatapp.stchat.view.components.atoms;

import javafx.scene.control.TextField;

public class PrimaryTextField extends TextField {
    public PrimaryTextField(String placeholder) {
        setPromptText(placeholder);
        setPrefHeight(45);
        setStyle("-fx-background-color: #f7fafc; -fx-border-color: #e2e8f0;" +
                "-fx-border-radius: 12; -fx-background-radius: 12;" +
                "-fx-padding: 0 15; -fx-font-size: 14;");
    }
}
