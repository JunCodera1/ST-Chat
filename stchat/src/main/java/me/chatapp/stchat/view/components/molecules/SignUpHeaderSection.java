package me.chatapp.stchat.view.components.molecules;

import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import me.chatapp.stchat.view.components.atoms.STText;

public class SignUpHeaderSection extends VBox {
    public SignUpHeaderSection() {
        super(10);
        setAlignment(Pos.CENTER);

        STText title = new STText("Create Account", STText.TextType.TITLE);
        STText subtitle = new STText("Join ST Chat community today", STText.TextType.SUBTITLE);

        getChildren().addAll(title, subtitle);
    }
}
