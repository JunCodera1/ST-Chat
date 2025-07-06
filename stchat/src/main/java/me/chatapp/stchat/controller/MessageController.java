package me.chatapp.stchat.controller;

import javafx.application.Platform;
import me.chatapp.stchat.AppContext;
import me.chatapp.stchat.api.MessageApiClient;
import me.chatapp.stchat.api.SocketClient;
import me.chatapp.stchat.model.Message;
import me.chatapp.stchat.model.User;
import me.chatapp.stchat.view.components.organisms.Panel.ChatPanel;

import java.time.LocalDateTime;

public class MessageController {

    private final MessageApiClient messageApiClient;
    SocketClient socketClient = AppContext.getInstance().getSocketClient();

    public MessageController() {
        this.messageApiClient = new MessageApiClient();
    }

    public void loadMessagesForConversation(int conversationId, ChatPanel chatPanel) {
        System.out.println("Loading messages for conversation: " + conversationId);

        messageApiClient.getMessages(conversationId)
                .thenAccept(messages -> {
                    System.out.println("Loaded " + messages.size() + " messages");
                    if (chatPanel != null) {
                        // Only use Platform.runLater if we're in a JavaFX application
                        if (isJavaFXApplicationActive()) {
                            Platform.runLater(() -> {
                                chatPanel.clearMessages();
                                messages.forEach(chatPanel::addMessage);
                            });
                        } else {
                            System.out.println("Messages loaded (console mode)");
                        }
                    }
                })
                .exceptionally(throwable -> {
                    System.err.println("Failed to load messages: " + throwable.getMessage());
                    throwable.printStackTrace();
                    if (isJavaFXApplicationActive()) {
                        Platform.runLater(() ->
                                System.err.println("Failed to load messages: " + throwable.getMessage()));
                    }
                    return null;
                });
    }

    public void sendMessage(User sender, String content, int conversationId, ChatPanel chatPanel) {
        System.out.println("Attempting to send message:");
        System.out.println("  Sender: " + sender.getUsername());
        System.out.println("  Content: " + content);
        System.out.println("  ConversationId: " + conversationId);

        Message message = new Message(sender.getUsername(), content,
                Message.MessageType.TEXT);

        message.setConversationId(conversationId);
        message.setSenderId(1);
        message.setCreatedAt(LocalDateTime.now());

        System.out.println("Created message: " + message);

        messageApiClient.sendMessage(message)
                .thenAccept(success -> {
                    System.out.println("API call result: " + success);
                    if (success) {
                        if (isJavaFXApplicationActive()) {
                            Platform.runLater(() -> {
                                if (chatPanel != null) {
                                    chatPanel.addMessage(message);
                                }
                                System.out.println("Message sent successfully via API");
                            });
                        } else {
                            if (chatPanel != null) {
                                chatPanel.addMessage(message);
                            }
                            System.out.println("Message sent successfully via API");
                        }

                        // Send via socket
                        if (socketClient != null && socketClient.isConnected()) {
                            socketClient.sendMessage(message);
                            System.out.println("Message sent via socket");
                        } else {
                            System.err.println("Socket client not connected");
                        }
                    } else {
                        System.err.println("Failed to send message via API");
                        if (isJavaFXApplicationActive()) {
                            Platform.runLater(() ->
                                    System.err.println("Failed to send message"));
                        }
                    }
                })
                .exceptionally(throwable -> {
                    System.err.println("Error sending message: " + throwable.getMessage());
                    throwable.printStackTrace();
                    if (isJavaFXApplicationActive()) {
                        Platform.runLater(() ->
                                System.err.println("Error sending message: " + throwable.getMessage()));
                    }
                    return null;
                });
    }

    private boolean isJavaFXApplicationActive() {
        try {
            return Platform.isFxApplicationThread() || Platform.isImplicitExit();
        } catch (IllegalStateException e) {
            return false;
        }
    }

    public void close() {
        messageApiClient.close();
    }
}