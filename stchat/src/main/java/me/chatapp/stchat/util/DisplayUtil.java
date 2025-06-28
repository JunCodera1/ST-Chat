package me.chatapp.stchat.util;

import javafx.animation.ScaleTransition;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static me.chatapp.stchat.util.CSSUtil.GRADIENT_BACKGROUND;
import static me.chatapp.stchat.util.CSSUtil.getFieldStyle;

public class DisplayUtil {
    public static void updatePasswordStrengthDisplay(String password, Text strengthText) {
        int strength = ValidateUtil.calculatePasswordStrength(password);

        String[] messages = {"", "Password strength: Weak", "Password strength: Fair",
                "Password strength: Good", "Password strength: Strong"};
        Color[] colors = {Color.TRANSPARENT, Color.web("#e53e3e"), Color.web("#dd6b20"),
                Color.web("#3182ce"), Color.web("#38a169")};

        if (strength < messages.length) {
            strengthText.setText(messages[strength]);
            strengthText.setFill(colors[strength]);
        }
    }

    public static void createBackgroundCircles(StackPane root) {
        Circle[] circles = {
                createBackgroundCircle(90, -160, -250, 0.1),
                createBackgroundCircle(70, 190, -180, 0.1),
                createBackgroundCircle(50, -190, 280, 0.1),
                createBackgroundCircle(110, 160, 250, 0.05)
        };

        root.getChildren().addAll(circles);
    }

    public static Circle createBackgroundCircle(double radius, double translateX, double translateY, double opacity) {
        Circle circle = new Circle(radius);
        circle.setFill(Color.web("#ffffff", opacity));
        circle.setTranslateX(translateX);
        circle.setTranslateY(translateY);
        return circle;
    }

    public static VBox createPasswordContainer(String labelText, String promptText) {
        VBox container = new VBox(8);

        Label label = new Label(labelText);
        label.setFont(Font.font("System", FontWeight.MEDIUM, 14));
        label.setTextFill(Color.web("#4a5568"));

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(promptText);
        passwordField.setPrefHeight(45);
        passwordField.setStyle(getFieldStyle());

        container.getChildren().addAll(label, passwordField);
        return container;
    }

    public static void addFieldFocusEffects(TextField... fields) {
        String focusStyle =
                "-fx-background-color: #ffffff;" +
                        "-fx-border-color: #667eea;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 12;" +
                        "-fx-background-radius: 12;" +
                        "-fx-padding: 0 15;" +
                        "-fx-font-size: 14;" +
                        "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.3), 10, 0, 0, 2);";

        for (TextField field : fields) {
            field.focusedProperty().addListener((obs, oldVal, newVal) -> field.setStyle(newVal ? focusStyle : getFieldStyle()));
        }
    }


    public static void createBackgroundCirclesRegister(StackPane root) {
        Circle circle1 = new Circle(90);
        circle1.setFill(Color.web("#ffffff", 0.1));
        circle1.setTranslateX(-160);
        circle1.setTranslateY(-250);

        Circle circle2 = new Circle(70);
        circle2.setFill(Color.web("#ffffff", 0.1));
        circle2.setTranslateX(190);
        circle2.setTranslateY(-180);

        Circle circle3 = new Circle(50);
        circle3.setFill(Color.web("#ffffff", 0.1));
        circle3.setTranslateX(-190);
        circle3.setTranslateY(300);

        Circle circle4 = new Circle(110);
        circle4.setFill(Color.web("#ffffff", 0.05));
        circle4.setTranslateX(160);
        circle4.setTranslateY(280);

        root.getChildren().addAll(circle1, circle2, circle3, circle4);
    }

    public static VBox createFieldContainer(String labelText, String promptText) {
        VBox container = new VBox(8);

        Label label = new Label(labelText);
        label.setFont(Font.font("System", FontWeight.MEDIUM, 14));
        label.setTextFill(Color.web("#4a5568"));

        TextField textField = new TextField();
        textField.setPromptText(promptText);
        textField.setPrefHeight(45);
        textField.setStyle(getFieldStyle());

        container.getChildren().addAll(label, textField);
        return container;
    }

    public static Button createBackButton() {
        Button button = new Button("← Back to Profile");
        button.setPrefWidth(320);
        button.setPrefHeight(45);
        button.setFont(Font.font("System", FontWeight.MEDIUM, 14));
        button.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: #667eea;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 22;" +
                        "-fx-text-fill: #667eea;" +
                        "-fx-cursor: hand;"
        );
        return button;
    }

    public static Button createChangePasswordButton() {
        Button button = new Button("Change Password");
        button.setPrefWidth(320);
        button.setPrefHeight(50);
        button.setFont(Font.font("System", FontWeight.BOLD, 16));
        button.setStyle(
                GRADIENT_BACKGROUND +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 25;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.4), 15, 0, 0, 5);"
        );
        return button;
    }
}
