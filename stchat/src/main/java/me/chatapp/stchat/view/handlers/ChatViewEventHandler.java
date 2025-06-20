package me.chatapp.stchat.view.handlers;

import me.chatapp.stchat.view.components.organisms.Panel.ChatPanel;
import me.chatapp.stchat.view.components.organisms.Panel.ConnectionPanel;
import me.chatapp.stchat.view.components.organisms.Panel.MessageInputPanel;
import me.chatapp.stchat.view.state.ChatViewStateManager;

/**
 * Xử lý các sự kiện UI của ChatView
 */
public class ChatViewEventHandler {

    private final String port;
    private final ConnectionPanel connectionPanel;
    private final ChatPanel chatPanel;
    private final MessageInputPanel messageInputPanel;
    private final ChatViewStateManager stateManager;

    // Actions
    private ChatEventActions.ConnectAction onConnectAction;
    private ChatEventActions.DisconnectAction onDisconnectAction;
    private ChatEventActions.SendMessageAction onSendMessageAction;

    public ChatViewEventHandler(String port,
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

    /**
     * Thiết lập các event handler cho UI components
     */
    private void setupEventHandlers() {
        // Connection events
        connectionPanel.getConnectButton().setOnAction(e -> handleConnect());
        connectionPanel.getDisconnectButton().setOnAction(e -> handleDisconnect());

        // Message events
        messageInputPanel.getSendButton().setOnAction(e -> handleSendMessage());
        messageInputPanel.getMessageField().setOnAction(e -> handleSendMessage());
        messageInputPanel.getClearButton().setOnAction(e -> handleClearMessages());

        // Enter key support cho username field
        connectionPanel.getUsernameField().setOnAction(e ->
                connectionPanel.getConnectButton().fire());
    }

    /**
     * Xử lý sự kiện kết nối
     */
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

    /**
     * Xử lý sự kiện ngắt kết nối
     */
    private void handleDisconnect() {
        if (onDisconnectAction != null) {
            onDisconnectAction.execute();
        }
    }

    /**
     * Xử lý sự kiện gửi tin nhắn
     */
    private void handleSendMessage() {
        String message = stateManager.getCurrentMessage();
        if (!message.isEmpty() && onSendMessageAction != null) {
            onSendMessageAction.execute(message);
            stateManager.clearMessageInput();
        }
    }

    /**
     * Xử lý sự kiện xóa tin nhắn
     */
    private void handleClearMessages() {
        chatPanel.clearMessages();
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
