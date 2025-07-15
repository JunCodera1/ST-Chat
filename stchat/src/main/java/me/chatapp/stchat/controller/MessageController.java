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
                .thenApply(messageApiClient::enrichMessagesWithAttachment) // enrich here
                .thenAccept(messages -> {
                    System.out.println("Loaded " + messages.size() + " enriched messages");

                    if (chatPanel != null) {
                        if (isJavaFXApplicationActive()) {
                            Platform.runLater(() -> messages.forEach(chatPanel::addMessage));
                        } else {
                            messages.forEach(chatPanel::addMessage);
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
        message.setSenderId(sender.getId());

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

    public void sendDirectMessage(User sender,
                                  User receiver,
                                  String content,
                                  int conversationId,
                                  ChatPanel chatPanel) {
        if (content == null || content.isBlank()) return;

        System.out.println("Sending DM from " + sender + " to " + receiver);

        messageApiClient.sendDirectMessage(sender.getId(), receiver.getId(), content)
                .thenAccept(ignored -> {
                    Message msg = new Message(sender.getUsername(), content, Message.MessageType.USER);
                    msg.setSenderId(sender.getId());
                    msg.setReceiverId(receiver.getId());
                    msg.setConversationId(conversationId);
                    msg.setCreatedAt(LocalDateTime.now());

                    // Cập nhật UI
                    Platform.runLater(() -> chatPanel.addMessage(msg));

                    // Gửi socket nếu cần
                    if (socketClient != null && socketClient.isConnected()) {
                        socketClient.sendMessage(msg);
                    }
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
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

    public void sendAttachmentMessage(Message message, ChatPanel chatPanel) {
        System.out.println("Sending attachment message: " + message);

        if (message.getType() == Message.MessageType.TEXT) {
            System.err.println("MessageType TEXT is not considered an attachment.");
            return;
        }

        message.setCreatedAt(LocalDateTime.now());

        messageApiClient.sendMessage(message)
                .thenAccept(success -> {
                    if (success) {
                        System.out.println("Attachment message sent successfully via API");
                        handleSuccessfulSend(message, chatPanel);
                    } else {
                        System.err.println("Failed to send attachment message via API");
                    }
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    System.err.println("Error sending attachment message: " + ex.getMessage());
                    return null;
                });
    }

    private void handleSuccessfulSend(Message message, ChatPanel chatPanel) {
        Platform.runLater(() -> {
            if (chatPanel != null) {
                chatPanel.addMessage(message);
            }
        });

        if (socketClient != null && socketClient.isConnected()) {
            socketClient.sendMessage(message);
            System.out.println("Attachment message sent via socket");
        } else {
            System.err.println("Socket client not connected");
        }
    }


}