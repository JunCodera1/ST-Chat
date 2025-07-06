package me.chatapp.stchat.view.handlers;

import me.chatapp.stchat.controller.MessageController;
import me.chatapp.stchat.model.User;
import me.chatapp.stchat.view.components.organisms.Panel.ChatPanel;

import java.util.function.Consumer;

public class MessageSender {

    public static void sendMessage(
            User currentUser,
            String content,
            int conversationId,
            String contactName,
            MessageController messageController,
            ChatPanel chatPanel,
            Consumer<String> errorHandler
    ) {
        if (currentUser == null) {
            errorHandler.accept("Please login first");
            return;
        }

        if (conversationId == -1 || contactName == null) {
            errorHandler.accept("Please select a conversation first");
            return;
        }

        if (content.trim().isEmpty()) {
            return;
        }

        System.out.println("Sending message: " + content);
        System.out.println("To conversation: " + conversationId);
        System.out.println("Contact: " + contactName);

        if (messageController != null) {
            messageController.sendMessage(currentUser, content, conversationId, chatPanel);
        }
    }
}
