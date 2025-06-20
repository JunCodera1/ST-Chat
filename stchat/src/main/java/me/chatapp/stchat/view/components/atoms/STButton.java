package me.chatapp.stchat.view.components.atoms;

import javafx.animation.ScaleTransition;
import javafx.scene.control.Button;
import javafx.util.Duration;

public class STButton extends Button {
    public enum ButtonType {
        PRIMARY, SECONDARY, LINK
    }

    public STButton(String text, ButtonType type) {
        super(text);
        applyStyle(type);
        setupHoverEffects();
    }

    private void applyStyle(ButtonType type) {
        switch (type) {
            case PRIMARY:
                setStyle("-fx-background-color: linear-gradient(135deg, #667eea 0%, #764ba2 100%);" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 25;" +
                        "-fx-font-size: 14;" +
                        "-fx-cursor: hand;");
                break;

            case SECONDARY:
                setStyle("-fx-background-color: #e2e8f0;" +
                        "-fx-text-fill: #2d3748;" +
                        "-fx-background-radius: 25;" +
                        "-fx-font-size: 14;" +
                        "-fx-cursor: hand;");
                break;

            case LINK:
                setStyle("-fx-background-color: transparent;" +
                        "-fx-text-fill: #667eea;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 13;" +
                        "-fx-underline: true;" +
                        "-fx-cursor: hand;");
                break;
        }

        setPrefHeight(45);
    }

    private void setupHoverEffects() {
        setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(100), this);
            scale.setToX(1.05);
            scale.setToY(1.05);
            scale.play();
        });

        setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(100), this);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
        });
    }
}
