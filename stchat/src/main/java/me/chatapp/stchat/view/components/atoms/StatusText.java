package me.chatapp.stchat.view.components.atoms;

import javafx.animation.ScaleTransition;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class StatusText extends Text {
    public enum Status {
        ERROR("#e53e3e"),
        SUCCESS("#38a169"),
        INFO("#3182ce"),
        WARNING("#d69e2e");

        private final String color;

        Status(String color) {
            this.color = color;
        }

        public String getColor() {
            return color;
        }
    }

    public StatusText() {
        super();
        initializeStyles();
    }

    public StatusText(String text) {
        super(text);
        initializeStyles();
    }

    private void initializeStyles() {
        setFont(Font.font("System", FontWeight.MEDIUM, 12));
    }

    public void setStatus(String message, Status status) {
        setText(message);
        setFill(Color.web(status.getColor()));

        if (status == Status.ERROR) {
            playShakeAnimation();
        }
    }

    private void playShakeAnimation() {
        ScaleTransition shakeScale = new ScaleTransition(Duration.millis(100), this);
        shakeScale.setToX(1.1);
        shakeScale.setAutoReverse(true);
        shakeScale.setCycleCount(4);
        shakeScale.play();
    }
}