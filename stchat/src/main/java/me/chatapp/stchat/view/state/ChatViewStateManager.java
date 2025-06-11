package me.chatapp.stchat.view.state;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.Scene;
import me.chatapp.stchat.model.Message;
import me.chatapp.stchat.view.components.*;

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
            // Cập nhật trạng thái các button
            connectionPanel.getConnectButton().setDisable(connected);
            connectionPanel.getDisconnectButton().setDisable(!connected);
            connectionPanel.getHostField().setDisable(connected);
            connectionPanel.getUsernameField().setDisable(connected);

            // Cập nhật trạng thái message input
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

    /**
     * Hiển thị dialog lỗi
     */
    public void showError(String error) {
        showAlert(Alert.AlertType.ERROR, "Connection Error", "Unable to connect", error);
    }

    /**
     * Hiển thị dialog thông tin
     */
    public void showInfo(String title, String message) {
        showAlert(Alert.AlertType.INFORMATION, title, null, message);
    }

    /**
     * Hiển thị alert dialog
     */
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

    /**
     * Xóa nội dung tin nhắn đang soạn
     */
    public void clearMessageInput() {
        messageInputPanel.getMessageField().clear();
    }

    /**
     * Lấy nội dung tin nhắn hiện tại
     */
    public String getCurrentMessage() {
        return messageInputPanel.getMessageField().getText().trim();
    }

    /**
     * Focus vào ô nhập tin nhắn
     */
    public void focusMessageInput() {
        messageInputPanel.getMessageField().requestFocus();
    }
}