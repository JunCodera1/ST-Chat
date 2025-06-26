package me.chatapp.stchat.view.components.atoms.Button;

import javafx.scene.control.Button;
import org.kordamp.ikonli.javafx.FontIcon;

public class IconButton extends Button {

    private final FontIcon icon;

    public IconButton(String iconCode) {
        this.icon = new FontIcon(iconCode); // Ví dụ: "fas-cog" cho FontAwesome cog icon

        setGraphic(icon);
        setPrefSize(40, 40);
        setStyle(defaultStyle());

        setOnMouseEntered(e -> setStyle(hoverStyle()));
        setOnMouseExited(e -> setStyle(defaultStyle()));
    }

    public void setEnabled(boolean enabled) {
        setDisable(!enabled);
        setStyle(enabled ? defaultStyle() : disabledStyle());
        icon.setOpacity(enabled ? 1.0 : 0.5);
    }

    private String defaultStyle() {
        return """
            -fx-background-color: transparent;
            -fx-background-radius: 20;
            -fx-border-radius: 20;
            -fx-cursor: hand;
            """;
    }

    private String hoverStyle() {
        return """
            -fx-background-color: #f0f2f5;
            -fx-background-radius: 20;
            -fx-border-radius: 20;
            -fx-cursor: hand;
            """;
    }

    private String disabledStyle() {
        return """
            -fx-background-color: transparent;
            -fx-background-radius: 20;
            -fx-border-radius: 20;
            -fx-opacity: 0.5;
            """;
    }
}
