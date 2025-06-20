package me.chatapp.stchat.view.components.organisms;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.effect.DropShadow;
import me.chatapp.stchat.model.Message;
import me.chatapp.stchat.model.MessageType;

import java.time.LocalDateTime;

public class ChatPanel {
    private final VBox chatContainer;
    private final ScrollPane scrollPane;
    private final VBox messageContainer;
    private Label messageCountLabel;

    public ChatPanel() {
        chatContainer = new VBox();
        chatContainer.setSpacing(0);
        chatContainer.setStyle("-fx-background-color: #ffffff;");

        // Chat header with improved styling
        HBox headerBox = createChatHeader();

        // Create scrollable message container
        messageContainer = new VBox();
        messageContainer.setSpacing(2);
        messageContainer.setPadding(new Insets(10, 15, 10, 15));
        messageContainer.setStyle("-fx-background-color: #ffffff;");

        // Empty state
        Label emptyStateLabel = new Label("No messages yet");
        emptyStateLabel.setStyle("-fx-text-fill: #9e9e9e; -fx-font-size: 14px;");
        emptyStateLabel.setAlignment(Pos.CENTER);
        VBox emptyStateContainer = new VBox(emptyStateLabel);
        emptyStateContainer.setAlignment(Pos.CENTER);
        emptyStateContainer.setPadding(new Insets(50));

        messageContainer.getChildren().add(emptyStateContainer);

        // Scroll pane for messages
        scrollPane = new ScrollPane(messageContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: #ffffff; -fx-background-color: transparent;");

        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        chatContainer.getChildren().addAll(headerBox, createSeparator(), scrollPane);
    }

    private HBox createChatHeader() {
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPadding(new Insets(15, 20, 15, 20));
        headerBox.setStyle("-fx-background-color: #ffffff;");

        Label chatTitle = new Label("Messages");
        chatTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        messageCountLabel = new Label("0 messages");
        messageCountLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px; -fx-font-weight: 500;");

        headerBox.getChildren().addAll(chatTitle, spacer, messageCountLabel);
        return headerBox;
    }

    private Region createSeparator() {
        Region separator = new Region();
        separator.setPrefHeight(1);
        separator.setStyle("-fx-background-color: #ecf0f1;");
        return separator;
    }

    public VBox getComponent() {
        return chatContainer;
    }

    public void addMessage(String message) {
        Platform.runLater(() -> {
            Message msgObj = new Message("System", message, MessageType.SYSTEM, LocalDateTime.now());
            addMessageToContainer(msgObj);
        });
    }

    public void addMessage(Message message) {
        Platform.runLater(() -> {
            addMessageToContainer(message);
        });
    }

    private void addMessageToContainer(Message message) {
        // Remove empty state if this is the first message
        if (messageContainer.getChildren().size() == 1 &&
                messageContainer.getChildren().get(0) instanceof VBox) {
            messageContainer.getChildren().clear();
        }

        VBox messageBox = createEnhancedMessageBox(message);
        messageContainer.getChildren().add(messageBox);

        // Add spacing between messages
        if (messageContainer.getChildren().size() > 1) {
            VBox.setMargin(messageBox, new Insets(8, 0, 0, 0));
        }

        scrollToBottom();
        updateMessageCount();
    }

    private VBox createEnhancedMessageBox(Message message) {
        VBox messageBox = new VBox();
        messageBox.setSpacing(6);

        // Create message bubble
        HBox bubbleContainer = new HBox();
        VBox bubble = new VBox();
        bubble.setSpacing(4);
        bubble.setPadding(new Insets(12, 16, 12, 16));
        bubble.setMaxWidth(450);

        // Message header with avatar, sender, and time
        HBox headerBox = new HBox();
        headerBox.setSpacing(8);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        // Avatar
        Circle avatar = createAvatar(message);

        // Sender and time container
        VBox senderTimeBox = new VBox();
        senderTimeBox.setSpacing(2);

        Label senderLabel = new Label(message.getSender());
        senderLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        Label timeLabel = new Label(message.getFormattedTime());
        timeLabel.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 11px;");

        senderTimeBox.getChildren().addAll(senderLabel, timeLabel);
        headerBox.getChildren().addAll(avatar, senderTimeBox);

        // Message content
        Label contentLabel = new Label(processMessageContent(message));
        contentLabel.setWrapText(true);
        contentLabel.setStyle("-fx-font-size: 14px; -fx-line-spacing: 2px;");

        bubble.getChildren().addAll(headerBox, contentLabel);

        // Style message bubble based on type
        styleMessageBubble(bubble, senderLabel, contentLabel, message);

        // Align message based on type
        if (message.getType() == MessageType.USER) {
            bubbleContainer.setAlignment(Pos.CENTER_RIGHT);
            HBox.setMargin(bubble, new Insets(0, 0, 0, 50));
        } else {
            bubbleContainer.setAlignment(Pos.CENTER_LEFT);
            HBox.setMargin(bubble, new Insets(0, 50, 0, 0));
        }

        bubbleContainer.getChildren().add(bubble);
        messageBox.getChildren().add(bubbleContainer);

        return messageBox;
    }

    private Circle createAvatar(Message message) {
        Circle avatar = new Circle(16);

        switch (message.getType()) {
            case USER:
                avatar.setFill(Color.web("#3498db"));
                break;
            case BOT:
                if (message.getContent().startsWith("(Private)")) {
                    avatar.setFill(Color.web("#e74c3c"));
                } else {
                    avatar.setFill(Color.web("#27ae60"));
                }
                break;
            case SYSTEM:
                avatar.setFill(Color.web("#f39c12"));
                break;
        }

        // Add subtle shadow
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.1));
        shadow.setRadius(2);
        shadow.setOffsetX(0);
        shadow.setOffsetY(1);
        avatar.setEffect(shadow);

        return avatar;
    }

    private void styleMessageBubble(VBox bubble, Label senderLabel, Label contentLabel, Message message) {
        switch (message.getType()) {
            case USER:
                bubble.setStyle("-fx-background-color: #3498db; -fx-background-radius: 18 18 4 18;");
                senderLabel.setTextFill(Color.WHITE);
                contentLabel.setTextFill(Color.WHITE);
                break;
            case BOT:
                if (message.getContent().startsWith("(Private)")) {
                    bubble.setStyle("-fx-background-color: #fdf2f2; -fx-background-radius: 18 18 18 4; -fx-border-color: #e74c3c; -fx-border-width: 1; -fx-border-radius: 18 18 18 4;");
                    senderLabel.setTextFill(Color.web("#c0392b"));
                    contentLabel.setTextFill(Color.web("#2c3e50"));
                } else {
                    bubble.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 18 18 18 4; -fx-border-color: #e9ecef; -fx-border-width: 1; -fx-border-radius: 18 18 18 4;");
                    senderLabel.setTextFill(Color.web("#27ae60"));
                    contentLabel.setTextFill(Color.web("#2c3e50"));
                }
                break;
            case SYSTEM:
                bubble.setStyle("-fx-background-color: #fff3cd; -fx-background-radius: 12; -fx-border-color: #ffeaa7; -fx-border-width: 1; -fx-border-radius: 12;");
                senderLabel.setTextFill(Color.web("#856404"));
                contentLabel.setTextFill(Color.web("#856404"));
                break;
        }

        // Add subtle shadow to all bubbles
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.08));
        shadow.setRadius(4);
        shadow.setOffsetX(0);
        shadow.setOffsetY(2);
        bubble.setEffect(shadow);
    }

    private String processMessageContent(Message message) {
        String content = message.getContent();

        // Handle private messages
        if (content.startsWith("(Private)") && message.getType() == MessageType.BOT) {
            return "🔒 " + content.substring(9).trim();
        }

        // Handle system messages
        if (message.getType() == MessageType.SYSTEM) {
            return "ℹ️ " + content;
        }

        return content;
    }

    private void scrollToBottom() {
        Platform.runLater(() -> {
            scrollPane.setVvalue(1.0);
        });
    }

    public void clearMessages() {
        Platform.runLater(() -> {
            messageContainer.getChildren().clear();

            // Show empty state
            Label emptyStateLabel = new Label("No messages yet");
            emptyStateLabel.setStyle("-fx-text-fill: #9e9e9e; -fx-font-size: 14px;");
            emptyStateLabel.setAlignment(Pos.CENTER);
            VBox emptyStateContainer = new VBox(emptyStateLabel);
            emptyStateContainer.setAlignment(Pos.CENTER);
            emptyStateContainer.setPadding(new Insets(50));

            messageContainer.getChildren().add(emptyStateContainer);
            updateMessageCount();
        });
    }

    private void updateMessageCount() {
        int count = messageContainer.getChildren().size();
        // Subtract 1 if empty state is showing
        if (count == 1 && messageContainer.getChildren().get(0) instanceof VBox) {
            VBox child = (VBox) messageContainer.getChildren().get(0);
            if (child.getChildren().size() == 1 && child.getChildren().get(0) instanceof Label) {
                count = 0;
            }
        }

        messageCountLabel.setText(count + " message" + (count != 1 ? "s" : ""));
    }

    // Getter methods for backward compatibility
    public ScrollPane getScrollPane() {
        return scrollPane;
    }

    public VBox getMessageContainer() {
        return messageContainer;
    }

    // Method to add typing indicator
    public void showTypingIndicator(String sender) {
        Platform.runLater(() -> {
            HBox typingBox = new HBox();
            typingBox.setSpacing(8);
            typingBox.setAlignment(Pos.CENTER_LEFT);
            typingBox.setPadding(new Insets(8, 16, 8, 16));
            typingBox.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 18; -fx-opacity: 0.8;");

            Circle avatar = new Circle(12);
            avatar.setFill(Color.web("#95a5a6"));

            Label typingLabel = new Label(sender + " is typing...");
            typingLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic; -fx-font-size: 12px;");

            typingBox.getChildren().addAll(avatar, typingLabel);

            HBox container = new HBox();
            container.setAlignment(Pos.CENTER_LEFT);
            container.getChildren().add(typingBox);
            HBox.setMargin(typingBox, new Insets(0, 50, 0, 0));

            messageContainer.getChildren().add(container);
            scrollToBottom();
        });
    }

    public void hideTypingIndicator() {
        Platform.runLater(() -> {
            // Remove the last typing indicator (if exists)
            if (!messageContainer.getChildren().isEmpty()) {
                int lastIndex = messageContainer.getChildren().size() - 1;
                if (messageContainer.getChildren().get(lastIndex) instanceof HBox) {
                    HBox lastItem = (HBox) messageContainer.getChildren().get(lastIndex);
                    if (!lastItem.getChildren().isEmpty() && lastItem.getChildren().get(0) instanceof HBox) {
                        HBox typingBox = (HBox) lastItem.getChildren().get(0);
                        if (typingBox.getChildren().size() > 1 && typingBox.getChildren().get(1) instanceof Label) {
                            Label label = (Label) typingBox.getChildren().get(1);
                            if (label.getText().contains(" is typing...")) {
                                messageContainer.getChildren().remove(lastIndex);
                            }
                        }
                    }
                }
            }
        });
    }
}