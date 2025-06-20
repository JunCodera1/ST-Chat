package me.chatapp.stchat.view.components.atoms.Circle;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AvatarCircleHeader extends StackPane {
    private final Circle circle;
    private final Label initials;

    public AvatarCircleHeader(String name, Color color) {
        circle = new Circle(20, color);
        initials = new Label(getInitials(name));
        initials.setTextFill(Color.WHITE);
        initials.setFont(Font.font("System", FontWeight.BOLD, 12));
        getChildren().addAll(circle, initials);
    }

    private String getInitials(String name) {
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        return ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
    }

    public void setColor(Color color) {
        circle.setFill(color);
    }

    public void setInitials(String name) {
        initials.setText(getInitials(name));
    }
}
