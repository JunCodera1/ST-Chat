package me.chatapp.stchat.view.components.atoms;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class STCircle extends Circle {
    public STCircle(double radius, Color color, double opacity) {
        super(radius);
        setFill(Color.web(color.toString(), opacity));
    }
}
