package me.chatapp.stchat.view.components.molecules;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import me.chatapp.stchat.view.components.atoms.PrimaryTextField;

public class LabeledInputField extends VBox {
    public LabeledInputField(String labelText, String placeholder) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-size: 14px; -fx-text-fill: #4a5568;");
        PrimaryTextField field = new PrimaryTextField(placeholder);
        setSpacing(8);
        getChildren().addAll(label, field);
    }
}
