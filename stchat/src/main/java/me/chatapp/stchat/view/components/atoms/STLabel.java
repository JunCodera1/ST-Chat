package me.chatapp.stchat.view.components.atoms;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class STLabel extends Label {
    public STLabel(String text) {
        super(text);
        setFont(Font.font("System", FontWeight.MEDIUM, 14));
        setTextFill(Color.web("#4a5568"));
    }
}