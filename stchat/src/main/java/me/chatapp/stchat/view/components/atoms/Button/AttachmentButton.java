package me.chatapp.stchat.view.components.atoms.Button;

import javafx.scene.control.Button;
import org.kordamp.ikonli.javafx.FontIcon;

public class AttachmentButton extends Button {
    public AttachmentButton() {
        FontIcon icon = new FontIcon("fas-paperclip"); // 📎 icon
        icon.setIconSize(16);
        setGraphic(icon);
        setText("");
        initializeStyle();
    }

    private void initializeStyle() {
        getStyleClass().add("attachment-button");
        setStyle("-fx-font-size: 16px; -fx-background-color: transparent; " +
                "-fx-border-radius: 50%; -fx-background-radius: 50%; " +
                "-fx-min-width: 32px; -fx-min-height: 32px; " +
                "-fx-max-width: 32px; -fx-max-height: 32px;");

        setOnMouseEntered(e -> setStyle(getStyle() + "-fx-background-color: #f0f0f0;"));
        setOnMouseExited(e -> setStyle(getStyle().replace("-fx-background-color: #f0f0f0;", "")));
    }
}
