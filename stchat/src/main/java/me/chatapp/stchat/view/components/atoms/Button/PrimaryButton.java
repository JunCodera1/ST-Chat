package me.chatapp.stchat.view.components.atoms.Button;

import javafx.animation.ScaleTransition;
import javafx.scene.control.Button;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class PrimaryButton extends Button {
    public PrimaryButton() {
        super();
        initializeStyles();
    }

    public PrimaryButton(String text) {
        super(text);
        initializeStyles();
    }

    private void initializeStyles() {
        setPrefWidth(300);
        setPrefHeight(50);
        setFont(Font.font("System", FontWeight.BOLD, 16));
        getStyleClass().add("primary-button");

        addHoverEffects();
    }

    private void addHoverEffects() {
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