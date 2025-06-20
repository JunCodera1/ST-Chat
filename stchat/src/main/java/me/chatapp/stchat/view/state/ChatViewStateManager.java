package me.chatapp.stchat.view.state;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.Scene;
import me.chatapp.stchat.model.Message;
import me.chatapp.stchat.view.components.organisms.Panel.ChatPanel;
import me.chatapp.stchat.view.components.organisms.Panel.ConnectionPanel;
import me.chatapp.stchat.view.components.organisms.Panel.MessageInputPanel;
import me.chatapp.stchat.view.components.organisms.Bar.StatusBar;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ChatViewStateManager {

    private final ConnectionPanel connectionPanel;
    private final ChatPanel chatPanel;
    private final MessageInputPanel messageInputPanel;
    private final StatusBar statusBar;
    private final Scene scene;

    public ChatViewStateManager(ConnectionPanel connectionPanel,
                                ChatPanel chatPanel,
                                MessageInputPanel messageInputPanel,
                                StatusBar statusBar,
                                Scene scene) {
        this.connectionPanel = connectionPanel;
        this.chatPanel = chatPanel;
        this.messageInputPanel = messageInputPanel;
        this.statusBar = statusBar;
        this.scene = scene;
    }

    public void updateConnectionStatus(boolean connected) {
        Platform.runLater(() -> {
            connectionPanel.getConnectButton().setDisable(connected);
            connectionPanel.getDisconnectButton().setDisable(!connected);
            connectionPanel.getHostField().setDisable(connected);
            connectionPanel.getUsernameField().setDisable(connected);

            messageInputPanel.getMessageField().setDisable(!connected);
            messageInputPanel.getSendButton().setDisable(!connected);

            if (connected) {
                statusBar.setStatus("Connected", true);
                messageInputPanel.getMessageField().requestFocus();
            } else {
                statusBar.setStatus("Disconnected", false);
            }
        });
    }

    public void addMessage(String message) {
        Platform.runLater(() -> {
            String timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            chatPanel.addMessage("[" + timestamp + "] " + message);
        });
    }

    public void addMessage(Message message) {
        Platform.runLater(() -> {
            chatPanel.addMessage(message);
        });
    }

    public void showError(String error) {
        showAlert(Alert.AlertType.ERROR, "Connection Error", "Unable to connect", error);
    }

    public void showInfo(String title, String message) {
        showAlert(Alert.AlertType.INFORMATION, title, null, message);
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.getDialogPane().getStylesheets().add(scene.getStylesheets().get(0));
            alert.showAndWait();
        });
    }

    public void clearMessageInput() {
        messageInputPanel.getMessageField().clear();
    }

    public String getCurrentMessage() {
        return messageInputPanel.getMessageField().getText().trim();
    }

    public void focusMessageInput() {
        messageInputPanel.getMessageField().requestFocus();
    }
}