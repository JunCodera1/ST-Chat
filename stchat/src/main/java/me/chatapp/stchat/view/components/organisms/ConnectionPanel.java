package me.chatapp.stchat.view.components.organisms;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import me.chatapp.stchat.view.components.atoms.PrimaryButton;
import me.chatapp.stchat.view.components.atoms.SecondaryButton;
import me.chatapp.stchat.view.components.atoms.TitledLabel;
import me.chatapp.stchat.view.components.molecules.InputFieldGroup;

public class ConnectionPanel {
    private final VBox container;
    private final InputFieldGroup serverGroup;
    private final InputFieldGroup usernameGroup;
    private final PrimaryButton connectButton;
    private final SecondaryButton disconnectButton;

    public ConnectionPanel() {
        container = new VBox(15);
        container.getStyleClass().add("connection-panel");

        TitledLabel title = new TitledLabel("Connection Settings");

        serverGroup = new InputFieldGroup("Server Address", "localhost", "localhost");
        usernameGroup = new InputFieldGroup("Username", "Enter your name", null);

        connectButton = new PrimaryButton("Connect");
        disconnectButton = new SecondaryButton("Disconnect");
        disconnectButton.setDisable(true);

        HBox controls = new HBox(15, connectButton, disconnectButton);
        controls.setAlignment(Pos.CENTER);

        container.getChildren().addAll(title, serverGroup, usernameGroup, controls);
    }

    public VBox getComponent() {
        return container;
    }

    public TextField getHostField() {
        return serverGroup.getInputField();
    }

    public TextField getUsernameField() {
        return usernameGroup.getInputField();
    }

    public Button getConnectButton() {
        return connectButton;
    }

    public Button getDisconnectButton() {
        return disconnectButton;
    }

    public void setConnectionState(boolean connected) {
        connectButton.setDisable(connected);
        disconnectButton.setDisable(!connected);
        getHostField().setDisable(connected);
        getUsernameField().setDisable(connected);
    }

    public void showValidationError() {
        if (getHostField().getText().trim().isEmpty()) serverGroup.showError();
        if (getUsernameField().getText().trim().isEmpty()) usernameGroup.showError();
    }

    public void clearValidationError() {
        serverGroup.clearError();
        usernameGroup.clearError();
    }
}
