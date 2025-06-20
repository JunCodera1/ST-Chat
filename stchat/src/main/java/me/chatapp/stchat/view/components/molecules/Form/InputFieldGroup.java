package me.chatapp.stchat.view.components.molecules.Form;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class InputFieldGroup extends VBox {
    private final TextField inputField;

    public InputFieldGroup(String labelText, String promptText, String defaultValue) {
        super(8);
        getStyleClass().add("input-group");

        Label label = new Label(labelText);
        label.getStyleClass().add("input-label");

        inputField = new TextField();
        inputField.getStyleClass().add("modern-text-field");
        inputField.setPromptText(promptText);
        if (defaultValue != null) inputField.setText(defaultValue);

        HBox inputBox = new HBox(inputField);
        HBox.setHgrow(inputField, Priority.ALWAYS);

        getChildren().addAll(label, inputBox);
    }

    public TextField getInputField() {
        return inputField;
    }

    public void showError() {
        inputField.setStyle(inputField.getStyle() + "; -fx-border-color: #dc3545;");
    }

    public void clearError() {
        inputField.setStyle(inputField.getStyle().replace("; -fx-border-color: #dc3545;", ""));
    }
}
