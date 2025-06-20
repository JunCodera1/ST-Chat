package me.chatapp.stchat.view.components.atoms;

import javafx.animation.ScaleTransition;
import javafx.scene.control.Button;
import javafx.util.Duration;

public class LinkButton extends Button {
    public LinkButton() {
        super();
        initializeStyles();
    }

    public LinkButton(String text) {
        super(text);
        initializeStyles();
    }

    private void initializeStyles() {
        setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #667eea;" +
                        "-fx-cursor: hand;" +
                        "-fx-font-size: 13;"
        );
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

    public void applyUnderline(boolean underline) {
        if (underline) {
            setStyle(getStyle() + "-fx-underline: true;");
        } else {
            setStyle(getStyle().replace("-fx-underline: true;", ""));
        }
    }
}
