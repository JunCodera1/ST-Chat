package me.chatapp.stchat.view.components.molecules;

import javafx.animation.ScaleTransition;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class StatusMessage extends Text {
    public StatusMessage() {
        setFont(Font.font("System", FontWeight.MEDIUM, 12));
    }

    public void showMessage(String message, boolean isError) {
        setText(message);
        setFill(isError ? Color.web("#e53e3e") : Color.web("#38a169"));

        // Animation
        ScaleTransition messageScale = new ScaleTransition(Duration.millis(100), this);
        messageScale.setToX(1.1);
        messageScale.setAutoReverse(true);
        messageScale.setCycleCount(2);
        messageScale.play();
    }
}
