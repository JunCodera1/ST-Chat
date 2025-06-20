package me.chatapp.stchat.view.components.atoms;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class STText extends Text {
    public enum TextType {
        TITLE, SUBTITLE, BODY, ERROR, SUCCESS
    }

    public STText(String text, TextType type) {
        super(text);
        applyStyle(type);
    }

    private void applyStyle(TextType type) {
        switch (type) {
            case TITLE:
                setFont(Font.font("System", FontWeight.BOLD, 26));
                setFill(Color.web("#2d3748"));
                break;
            case SUBTITLE:
                setFont(Font.font("System", FontWeight.NORMAL, 14));
                setFill(Color.web("#718096"));
                break;
            case ERROR:
                setFont(Font.font("System", FontWeight.MEDIUM, 12));
                setFill(Color.web("#e53e3e"));
                break;
        }
    }
}
