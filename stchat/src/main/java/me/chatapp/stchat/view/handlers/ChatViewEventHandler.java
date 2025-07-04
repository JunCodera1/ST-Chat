package me.chatapp.stchat.view.handlers;

import me.chatapp.stchat.model.Message;
import me.chatapp.stchat.api.SocketClient;
import me.chatapp.stchat.view.components.organisms.Panel.ChatPanel;
import me.chatapp.stchat.view.components.organisms.Panel.ConnectionPanel;
import me.chatapp.stchat.view.components.organisms.Panel.MessageInputPanel;
import me.chatapp.stchat.view.state.ChatViewStateManager;
import java.time.LocalDateTime;

public class ChatViewEventHandler {

    private final int port;
    private final ConnectionPanel connectionPanel;
    private final ChatPanel chatPanel;
    private final MessageInputPanel messageInputPanel;
    private final ChatViewStateManager stateManager;
    private SocketClient socketClient;


    // Actions
    private ChatEventActions.ConnectAction onConnectAction;
    private ChatEventActions.DisconnectAction onDisconnectAction;
    private ChatEventActions.SendMessageAction onSendMessageAction;

    public ChatViewEventHandler(int port,
                                ConnectionPanel connectionPanel,
                                ChatPanel chatPanel,
                                MessageInputPanel messageInputPanel,
                                ChatViewStateManager stateManager) {
        this.port = port;
        this.connectionPanel = connectionPanel;
        this.chatPanel = chatPanel;
        this.messageInputPanel = messageInputPanel;
        this.stateManager = stateManager;

        setupEventHandlers();
    }

    private void setupEventHandlers() {
        // Connection events
        connectionPanel.getConnectButton().setOnAction(e -> handleConnect());
        connectionPanel.getDisconnectButton().setOnAction(e -> handleDisconnect());

        // Message events
        messageInputPanel.getSendButton().setOnAction(e -> handleSendMessage());
        messageInputPanel.getMessageField().setOnAction(e -> handleSendMessage());

        // Enter key support cho username field
        connectionPanel.getUsernameField().setOnAction(e ->
                connectionPanel.getConnectButton().fire());
    }

    private void handleConnect() {
        if (onConnectAction != null) {
            String host = connectionPanel.getHostField().getText().trim();
            String username = connectionPanel.getUsernameField().getText().trim();

            // Validate input
            if (host.isEmpty()) {
                stateManager.showError("Host cannot be empty");
                return;
            }

            if (username.isEmpty()) {
                stateManager.showError("Username cannot be empty");
                return;
            }

            onConnectAction.execute(host, port, username);
        }
    }

    private void handleDisconnect() {
        if (onDisconnectAction != null) {
            onDisconnectAction.execute();
        }
    }

    private void handleSendMessage() {
        String messageText = stateManager.getCurrentMessage();
        if (!messageText.isEmpty()) {
            // Tạo message object với thông tin người gửi
            String senderName = getSenderName();
            Message message = new Message(
                    senderName,
                    messageText,
                    Message.MessageType.USER,
                    LocalDateTime.now()
            );

            // Thêm message vào chat panel
            stateManager.addMessage(message);

            // Gọi action nếu có (để gửi message đến server)
            if (onSendMessageAction != null) {
                onSendMessageAction.execute(messageText);
            }

            // Clear input field
            stateManager.clearMessageInput();
        }
    }

    /**
     * Lấy tên người gửi từ username field hoặc từ current user
     */
    private String getSenderName() {
        String username = connectionPanel.getUsernameField().getText().trim();
        return username.isEmpty() ? "You" : username;
    }

    private void handleClearMessages() {
        chatPanel.clearMessages();
    }

    public void setSocketClient(SocketClient socketClient) {
        this.socketClient = socketClient;
    }

    public SocketClient getSocketClient() {
        if (this.socketClient == null) {
            throw new IllegalStateException("SocketClient has not been set in ChatViewEventHandler.");
        }
        return this.socketClient;
    }

    // Setters cho actions
    public void setOnConnectAction(ChatEventActions.ConnectAction action) {
        this.onConnectAction = action;
    }

    public void setOnDisconnectAction(ChatEventActions.DisconnectAction action) {
        this.onDisconnectAction = action;
    }

    public void setOnSendMessageAction(ChatEventActions.SendMessageAction action) {
        this.onSendMessageAction = action;
    }

    public ChatEventActions.DisconnectAction getDisconnectAction() {
        return this.onDisconnectAction;
    }
}