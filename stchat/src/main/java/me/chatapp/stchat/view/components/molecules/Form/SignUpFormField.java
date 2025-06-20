package me.chatapp.stchat.view.components.molecules.Form;

import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import me.chatapp.stchat.view.components.atoms.Label.STLabel;
import me.chatapp.stchat.view.components.atoms.Field.STPasswordField;
import me.chatapp.stchat.view.components.atoms.Field.STTextField;

public class SignUpFormField extends VBox {
    private STLabel label;
    private TextField inputField;

    public SignUpFormField(String labelText, String promptText, boolean isPassword) {
        super(8);

        this.label = new STLabel(labelText);
        this.inputField = isPassword ?
                new STPasswordField(promptText) :
                new STTextField(promptText);

        getChildren().addAll(label, inputField);
    }

    public TextField getInputField() {
        return inputField;
    }

    public String getValue() {
        return inputField.getText().trim();
    }

    public void clear() {
        inputField.clear();
    }
}