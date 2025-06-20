package me.chatapp.stchat.view.components.atoms;

import javafx.scene.control.Button;

public class SecondaryButton extends Button {
    public SecondaryButton(String text) {
        super(text);
        getStyleClass().add("secondary-button");
        setPrefWidth(120);
    }
}
