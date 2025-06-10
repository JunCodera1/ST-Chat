package me.chatapp.stchat.view.handlers;

import me.chatapp.stchat.view.ChatView;

/**
 * Connection Handler - Manages connection-related UI logic
 */
public class ConnectionHandler {
    public final ChatView chatView;

    public ConnectionHandler(ChatView chatView) {
        this.chatView = chatView;
    }

    public void handleConnectionAttempt(String host, String port, String username) {
        // Validate input
        if (!validateConnectionInput(host, port, username)) {
            chatView.getConnectionPanel().showValidationError();
            chatView.showError("Please fill in all connection fields");
            return;
        }

        // Clear any previous validation errors
        chatView.getConnectionPanel().clearValidationError();

        // Show connecting status
        chatView.getStatusBar().showConnecting();

        // Update header
        chatView.getHeaderComponent().updateOnlineStatus(false, "Connecting...");
    }

    public void handleConnectionSuccess(String serverInfo) {
        chatView.getStatusBar().showConnected(serverInfo);
        chatView.getHeaderComponent().updateOnlineStatus(true, "Connected to " + serverInfo);
        chatView.showInfo("Connection Successful", "Connected to server successfully!");
    }

    public void handleConnectionFailure(String error) {
        chatView.getStatusBar().showError(error);
        chatView.getHeaderComponent().updateOnlineStatus(false, "Connection failed");
    }

    public void handleDisconnection() {
        chatView.getStatusBar().showDisconnected();
        chatView.getHeaderComponent().updateOnlineStatus(false, "Disconnected");
    }

    private boolean validateConnectionInput(String host, String port, String username) {
        return host != null && !host.trim().isEmpty() &&
                port != null && !port.trim().isEmpty() &&
                username != null && !username.trim().isEmpty();
    }
}