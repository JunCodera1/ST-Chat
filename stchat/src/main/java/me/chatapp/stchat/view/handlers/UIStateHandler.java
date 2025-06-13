package me.chatapp.stchat.view.handlers;

import me.chatapp.stchat.view.pages.ChatView;

public class UIStateHandler {
    private final ChatView chatView;
    private boolean isConnected = false;

    public UIStateHandler(ChatView chatView) {
        this.chatView = chatView;
    }

    public void updateConnectionState(boolean connected) {
        this.isConnected = connected;

        // Update connection panel
        chatView.getConnectionPanel().setConnectionState(connected);

        // Update message input
        chatView.getMessageInputPanel().setInputEnabled(connected);

        // Focus management
        if (connected) {
            chatView.getMessageInputPanel().focusInput();
        }
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void handleWindowClose() {
        // Cleanup logic before closing
        if (isConnected) {
            // Disconnect gracefully
        }
    }
}
