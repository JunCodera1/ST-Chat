package me.chatapp.stchat.controller;

import javafx.application.Platform;
import me.chatapp.stchat.api.MessageApiClient;
import me.chatapp.stchat.api.SocketClient;
import me.chatapp.stchat.model.Message;
import me.chatapp.stchat.model.User;
import me.chatapp.stchat.view.components.organisms.Panel.ChatPanel;

import java.time.LocalDateTime;

public class MessageController {

    private final MessageApiClient messageApiClient;

    public MessageController() {
        this.messageApiClient = new MessageApiClient();
    }

    public void loadMessagesForConversation(int conversationId, ChatPanel chatPanel) {
        messageApiClient.getMessages(conversationId)
                .thenAccept(messages -> Platform.runLater(() -> {
                    chatPanel.clearMessages();
                    messages.forEach(chatPanel::addMessage);
                }))
                .exceptionally(throwable -> {
                    Platform.runLater(() ->
                            System.err.println("Failed to load messages: " + throwable.getMessage()));
                    return null;
                });
    }

    public void sendMessage(User sender, String content, int conversationId,
                            ChatPanel chatPanel, SocketClient socketClient) {

        Message message = new Message(sender.getUsername(), content,
                Message.MessageType.TEXT, LocalDateTime.now());

        message.setConversationId(conversationId);

        messageApiClient.sendMessage(message)
                .thenAccept(success -> {
                    if (success) {
                        Platform.runLater(() -> {
                            chatPanel.addMessage(message);
                            System.out.println("Message sent successfully");
                        });
                        socketClient.sendMessage(message);
                    } else {
                        Platform.runLater(() ->
                                System.err.println("Failed to send message"));
                    }
                })
                .exceptionally(throwable -> {
                    Platform.runLater(() ->
                            System.err.println("Error sending message: " + throwable.getMessage()));
                    return null;
                });
    }

    public void close() {
        messageApiClient.close();
    }
}
