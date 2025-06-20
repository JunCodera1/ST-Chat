package me.chatapp.stchat.view.components.organisms;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ConnectionPanel {
    private final VBox connectionContainer;
    private final TextField hostField;
    private final TextField usernameField;
    private final Button connectButton;
    private final Button disconnectButton;



    public ConnectionPanel() {
        connectionContainer = new VBox();
        connectionContainer.getStyleClass().add("connection-panel");
        connectionContainer.setSpacing(15);

        // Title
        Label titleLabel = new Label("Connection Settings");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #495057;");

        // Server input group
        VBox serverGroup = createInputGroup("Server Address", "localhost");
        hostField = (TextField) ((HBox) serverGroup.getChildren().get(1)).getChildren().get(0);

        // Username input group
        VBox usernameGroup = createInputGroup("Username", "Enter your name");
        usernameField = (TextField) ((HBox) usernameGroup.getChildren().get(1)).getChildren().get(0);

        // Connection controls
        HBox controlsBox = new HBox(15);
        controlsBox.setAlignment(Pos.CENTER);

        connectButton = new Button("Connect");
        connectButton.getStyleClass().add("primary-button");
        connectButton.setPrefWidth(120);

        disconnectButton = new Button("Disconnect");
        disconnectButton.getStyleClass().add("secondary-button");
        disconnectButton.setPrefWidth(120);
        disconnectButton.setDisable(true);

        controlsBox.getChildren().addAll(connectButton, disconnectButton);

        connectionContainer.getChildren().addAll(
                titleLabel,
                serverGroup,
                usernameGroup,
                controlsBox
        );
    }

    private VBox createInputGroup(String labelText, String promptText) {
        VBox group = new VBox(8);
        group.getStyleClass().add("input-group");

        Label label = new Label(labelText);
        label.getStyleClass().add("input-label");

        HBox inputBox = new HBox();
        TextField textField = new TextField();
        textField.getStyleClass().add("modern-text-field");
        textField.setPromptText(promptText);

        if (labelText.equals("Server Address")) {
            textField.setText("localhost");
        } else if (labelText.equals("Port")) {
            textField.setText("12345");
        }

        HBox.setHgrow(textField, Priority.ALWAYS);
        inputBox.getChildren().add(textField);

        group.getChildren().addAll(label, inputBox);
        return group;
    }

    public VBox getComponent() {
        return connectionContainer;
    }

    // Getters
    public TextField getHostField() { return hostField; }
    public TextField getUsernameField() { return usernameField; }
    public Button getConnectButton() { return connectButton; }
    public Button getDisconnectButton() { return disconnectButton; }

    // State management
    public void setConnectionState(boolean connected) {
        if (connected) {
            connectButton.setDisable(true);
            disconnectButton.setDisable(false);
            hostField.setDisable(true);
            usernameField.setDisable(true);
        } else {
            connectButton.setDisable(false);
            disconnectButton.setDisable(true);
            hostField.setDisable(false);
            usernameField.setDisable(false);
        }
    }

    public void showValidationError() {
        if (hostField.getText().trim().isEmpty()) {
            hostField.setStyle(hostField.getStyle() + "; -fx-border-color: #dc3545;");
        }
        if (usernameField.getText().trim().isEmpty()) {
            usernameField.setStyle(usernameField.getStyle() + "; -fx-border-color: #dc3545;");
        }
    }

    public void clearValidationError() {
        hostField.setStyle(hostField.getStyle().replace("; -fx-border-color: #dc3545;", ""));
        usernameField.setStyle(usernameField.getStyle().replace("; -fx-border-color: #dc3545;", ""));
    }
}