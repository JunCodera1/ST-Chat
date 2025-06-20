package me.chatapp.stchat.view.components.atoms;

import javafx.scene.control.TextField;

public class PrimarySearchField extends TextField {
    public PrimarySearchField(String prompt) {
        setPromptText(prompt);
        setStyle("""
            -fx-background-color: #e4e6ea;
            -fx-background-radius: 20;
            -fx-border-radius: 20;
            -fx-padding: 8 15;
            -fx-font-size: 14px;
            -fx-border-color: transparent;
            """);
        setPrefHeight(36);
    }
}

