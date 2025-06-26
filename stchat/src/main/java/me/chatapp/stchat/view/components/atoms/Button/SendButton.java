package me.chatapp.stchat.view.components.atoms.Button;

import javafx.scene.control.Button;
import org.kordamp.ikonli.javafx.FontIcon;

public class SendButton extends Button {
    public SendButton() {
        FontIcon icon = new FontIcon("fas-paper-plane"); // 📤 icon
        icon.setIconSize(14);
        setGraphic(icon);
        setText("");
        initializeStyle();
    }

    private void initializeStyle() {
        getStyleClass().add("send-button");
        setStyle("-fx-background-color: #007bff; -fx-text-fill: white; " +
                "-fx-border-radius: 20px; -fx-background-radius: 20px; " +
                "-fx-font-size: 14px; -fx-font-weight: bold; " +
                "-fx-min-width: 40px; -fx-min-height: 40px; " +
                "-fx-max-width: 40px; -fx-max-height: 40px;");

        setOnMouseEntered(e -> setStyle(getStyle() + "-fx-background-color: #0056b3;"));
        setOnMouseExited(e -> setStyle(getStyle().replace("-fx-background-color: #0056b3;", "")));
    }
}
