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

    private ChatPanel activeChatPanel;
    private int activeConversationId = -1;
    private static MessageController instance;

    public static MessageController getInstance() {
        if (instance == null) {
            instance = new MessageController();
        }
        return instance;
    }

    public MessageController() {
        this.messageApiClient = new MessageApiClient();
    }

    public void receiveMessage(Message message) {
        if (activeChatPanel != null && message.getConversationId() == activeConversationId) {
            Platform.runLater(() -> {
                activeChatPanel.addMessage(message);
            });
        } else {
            System.out.println("Received message for other conversation: " + message.getConversationId());
        }
    }

    public void handleIncomingMessage(Message message) {
        int conversationId = message.getConversationId();

        System.out.println("[Socket] Received message for conversation: " + conversationId);

        if (activeChatPanel != null && activeConversationId == conversationId) {
            Platform.runLater(() -> {
                System.out.println("[Socket] Appending message to UI");
                activeChatPanel.addMessage(message);
            });
        } else {
            System.out.println("[Socket] Message not for current conversation. Ignored.");
        }
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
                        Platform.runLater(() -> {
                            if (chatPanel != null) {
                                chatPanel.addMessage(message); // Luôn add vào UI ngay khi gửi thành công API
                            }
                            System.out.println("Message sent successfully via API");
                        });
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

    public void sendDirectMessage(User sender, User receiver, String content, int conversationId) {
        System.out.println("Sending message: " + content + " to conversation: " + conversationId);

        Message message = new Message();
        message.setSenderId(sender.getId());
        message.setReceiverId(receiver.getId());
        message.setConversationId(conversationId);
        message.setContent(content);
        message.setType(Message.MessageType.TEXT);
        message.setCreatedAt(LocalDateTime.now());

        if (activeChatPanel != null && conversationId == activeConversationId) {
            System.out.println("Adding message to active chat panel immediately");
            activeChatPanel.addMessage(message);
        }

        messageApiClient.sendMessage(message)
                .thenAccept(success -> {
                    if (success) {
                        System.out.println("Message sent successfully via API");
                    } else {
                        Platform.runLater(() -> {
                            System.err.println("Failed to send message via API");
                        });
                    }
                })
                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        System.err.println("Error sending message: " + throwable.getMessage());
                        throwable.printStackTrace();
                    });
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

    public void setActiveChatPanel(ChatPanel chatPanel, int conversationId) {
        this.activeChatPanel = chatPanel;
        this.activeConversationId = conversationId;
    }

    public ChatPanel getActiveChatPanel() {
        return activeChatPanel;
    }
}