package me.chatapp.stchat.view.components.atoms;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AvatarCircle extends StackPane {
    public AvatarCircle(String name) {
        Circle circle = new Circle(24, Color.web("#1877f2"));
        Label initials = new Label(getInitials(name));
        initials.setTextFill(Color.WHITE);
        initials.setFont(Font.font("System", FontWeight.BOLD, 14));

        getChildren().addAll(circle, initials);
    }

    private String getInitials(String name) {
        String[] parts = name.split(" ");
        if (parts.length >= 2) return ("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase();
        return name.substring(0, 1).toUpperCase();
    }
}
