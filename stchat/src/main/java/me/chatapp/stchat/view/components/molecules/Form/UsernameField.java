package me.chatapp.stchat.view.components.molecules.Form;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.Node;

public class UsernameField extends VBox {
    private Label label;
    private Node inputField;

    public UsernameField(String labelText, Node inputField) {
        super(8);
        this.inputField = inputField;

        setAlignment(Pos.CENTER);
        createLabel(labelText);
        getChildren().addAll(label, inputField);
    }

    private void createLabel(String labelText) {
        label = new Label(labelText);
        label.setFont(Font.font("System", FontWeight.MEDIUM, 14));
        label.setTextFill(Color.web("#4a5568"));
    }

    public Node getInputField() {
        return inputField;
    }

    public Label getLabel() {
        return label;
    }

    public String getText() {
        if (inputField instanceof javafx.scene.control.TextInputControl) {
            return ((javafx.scene.control.TextInputControl) inputField).getText();
        }
        return "";
    }

}
