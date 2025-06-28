package me.chatapp.stchat.util;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import static me.chatapp.stchat.util.ValidateUtil.calculatePasswordStrength;

public class AnimationUtil {
    public static void addEntranceAnimation(@NotNull VBox card) {
        card.setOpacity(0);
        card.setScaleY(0.8);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(600), card);
        fadeIn.setToValue(1.0);

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(600), card);
        scaleIn.setToY(1.0);

        fadeIn.play();
        scaleIn.play();
    }

    public static void animateButtonScale(Button button, double scale) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), button);
        scaleTransition.setToX(scale);
        scaleTransition.setToY(scale);
        scaleTransition.play();
    }

    public static void addButtonHoverEffects(@NotNull Button... buttons) {
        for (Button button : buttons) {
            button.setOnMouseEntered(e -> AnimationUtil.animateButtonScale(button, 1.05));
            button.setOnMouseExited(e -> AnimationUtil.animateButtonScale(button, 1.0));
        }
    }

    public static void addPasswordStrengthIndicator(@NotNull PasswordField passwordField, Text strengthText) {
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            DisplayUtil.updatePasswordStrengthDisplay(newVal, strengthText);
        });
    }

    public static void addButtonSignUpHoverEffects(@NotNull Button... buttons) {
        for (Button button : buttons) {
            button.setOnMouseEntered(e -> {
                ScaleTransition scale = new ScaleTransition(Duration.millis(100), button);
                scale.setToX(1.05);
                scale.setToY(1.05);
                scale.play();
            });

            button.setOnMouseExited(e -> {
                ScaleTransition scale = new ScaleTransition(Duration.millis(100), button);
                scale.setToX(1.0);
                scale.setToY(1.0);
                scale.play();
            });
        }
    }

    public static void showRegisterMessage(Text statusMessage, String message, Color color) {
        statusMessage.setText(message);
        statusMessage.setFill(color);

        // Message animation
        ScaleTransition messageScale = new ScaleTransition(Duration.millis(100), statusMessage);
        messageScale.setToX(1.1);
        messageScale.setAutoReverse(true);
        messageScale.setCycleCount(2);
        messageScale.play();
    }
}
