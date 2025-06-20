package me.chatapp.stchat.view.components.atoms.Label;

import javafx.scene.control.Label;

public class TitledLabel extends Label {
    public TitledLabel(String text) {
        super(text);
        setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #495057;");
    }
}
