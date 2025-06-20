package me.chatapp.stchat.view.handlers;

import me.chatapp.stchat.view.components.pages.ChatView;

public class MessageHandler {
    public final ChatView chatView;

    public MessageHandler(ChatView chatView) {
        this.chatView = chatView;
    }

    public void handleMessageSend(String message) {
        if (message == null || message.trim().isEmpty()) {
            return;
        }

        // Clear the input field
        chatView.getMessageInputPanel().clearInput();

        // Show message being sent (optimistic UI)
        // The actual message will be handled by the controller
    }

    public void handleMessageReceived(String sender, String content) {
        // Could add notification logic here
        // Could add sound effects
        // Could add message filtering
    }

    public void handleTypingIndicator(String username, boolean isTyping) {
        if (isTyping) {
            chatView.getMessageInputPanel().showTypingIndicator(username);
        } else {
            chatView.getMessageInputPanel().hideTypingIndicator();
        }
    }
}
