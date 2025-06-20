package me.chatapp.stchat.view.components.atoms.Button;

import javafx.scene.control.Button;

public class IconButton extends Button {
    public IconButton(String emoji) {
        super(emoji);
        setPrefSize(40, 40);
        setStyle(defaultStyle());

        setOnMouseEntered(e -> setStyle(hoverStyle()));
        setOnMouseExited(e -> setStyle(defaultStyle()));
    }

    public void setEnabled(boolean enabled) {
        setDisable(!enabled);
        setStyle(enabled ? defaultStyle() : disabledStyle());
    }

    private String defaultStyle() {
        return """c
            -fx-background-color: transparent;
            -fx-background-radius: 20;
            -fx-border-radius: 20;
            -fx-font-size: 16;
            -fx-cursor: hand;
            """;
    }

    private String hoverStyle() {
        return """
            -fx-background-color: #f0f2f5;
            -fx-background-radius: 20;
            -fx-border-radius: 20;
            -fx-font-size: 16;
            -fx-cursor: hand;
            """;
    }

    private String disabledStyle() {
        return """
            -fx-background-color: transparent;
            -fx-background-radius: 20;
            -fx-border-radius: 20;
            -fx-font-size: 16;
            -fx-opacity: 0.5;
            """;
    }
}
