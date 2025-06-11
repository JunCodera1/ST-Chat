package me.chatapp.stchat.view.components;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import me.chatapp.stchat.model.Message;
import me.chatapp.stchat.model.MessageType;

import java.time.LocalDateTime;

public class ChatPanel {
    private final VBox chatContainer;
    private final TextArea chatArea;
    private final ListView<Message> messageListView;
    private final Label messageCountLabel;

    public ChatPanel() {
        chatContainer = new VBox();
        chatContainer.setSpacing(10);

        // Chat header
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPadding(new Insets(0, 0, 10, 0));

        Label chatTitle = new Label("Messages");
        chatTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #495057;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        messageCountLabel = new Label("0 messages");
        messageCountLabel.getStylesheets().add("message-count");

        headerBox.getChildren().addAll(chatTitle, spacer, messageCountLabel);

        // Chat area (using TextArea for compatibility)
        chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setWrapText(true);
        chatArea.getStyleClass().add("chat-area");

        // Message ListView (for advanced message display)
        messageListView = new ListView<>();
        messageListView.setCellFactory(listView -> new EnhancedMessageCell());
        messageListView.getStylesheets().add("message-list-view");

        // Use TextArea by default
        VBox.setVgrow(chatArea, Priority.ALWAYS);
        chatContainer.getChildren().addAll(headerBox, chatArea);

        // Ensure the chat area takes up available space
        chatArea.setMaxHeight(Double.MAX_VALUE);
        chatArea.setPrefHeight(Region.USE_COMPUTED_SIZE);
    }

    public VBox getComponent() {
        return chatContainer;
    }

    public void addMessage(String message) {
        Platform.runLater(() -> {
            // Chuyển String thành Message object
            Message msgObj = new Message("System", message, MessageType.SYSTEM, LocalDateTime.now());
            messageListView.getItems().add(msgObj);
            scrollToBottom();
            updateMessageCount();
        });
    }

    public void addMessage(Message message) {
        Platform.runLater(() -> {
            // Append to TextArea
            chatArea.appendText("[" + message.getFormattedTime() + "] "
                    + message.getSender() + ": "
                    + message.getContent() + "\n");

            // Optional: Nếu bạn vẫn muốn dùng ListView
            messageListView.getItems().add(message);
            scrollToBottom();
            updateMessageCount();
        });
    }


    private void scrollToBottom() {
        Platform.runLater(() -> {
            if (!messageListView.getItems().isEmpty()) {
                messageListView.scrollTo(messageListView.getItems().size() - 1);
                messageListView.getSelectionModel().clearSelection();
            }
        });
    }

    public void clearMessages() {
        Platform.runLater(() -> {
            chatArea.clear();
            messageListView.getItems().clear();
            updateMessageCount();
        });
    }

    private void updateMessageCount() {
        int count = messageListView.getItems().size();
        messageCountLabel.setText(count + " message" + (count != 1 ? "s" : ""));
    }


    public ListView<Message> getMessageListView() { return messageListView; }

    // Enhanced message cell for ListView
    private static class EnhancedMessageCell extends ListCell<Message> {
        @Override
        protected void updateItem(Message message, boolean empty) {
            super.updateItem(message, empty);

            if (empty || message == null) {
                setGraphic(null);
                setText(null);
                setStyle("");
            } else {
                VBox messageBox = createMessageBox(message);
                setGraphic(messageBox);
                setText(null);

                // Remove default cell background and padding
                setStyle("-fx-background-color: transparent; -fx-padding: 5 10; -fx-border-color: transparent;");
            }
        }

        private VBox createMessageBox(Message message) {
            VBox messageBox = new VBox(8);
            messageBox.setPadding(new Insets(12, 15, 12, 15));
            messageBox.setMaxWidth(600); // Limit message width

            // Header with sender and time
            HBox headerBox = new HBox(10);
            headerBox.setAlignment(Pos.CENTER_LEFT);

            Label senderLabel = new Label(message.getSender());
            senderLabel.setFont(Font.font("System", FontWeight.BOLD, 13));

            Label timeLabel = new Label(message.getFormattedTime());
            timeLabel.setFont(Font.font("System", 11));
            timeLabel.setTextFill(Color.GRAY);

            headerBox.getChildren().addAll(senderLabel, timeLabel);

            // Message content
            Label contentLabel = new Label(processMessageContent(message));
            contentLabel.setWrapText(true);
            contentLabel.setFont(Font.font("System", 13));
            contentLabel.setMaxWidth(550);

            messageBox.getChildren().addAll(headerBox, contentLabel);

            switch (message.getType()) {
                case USER:
                    messageBox.getStyleClass().add("user-message");
                    senderLabel.setTextFill(Color.web("#2196F3"));
                    break;
                case BOT:
                    if (message.getContent().startsWith("(Private)")) {
                        messageBox.getStyleClass().add("private-message");
                        senderLabel.setTextFill(Color.web("#E91E63"));
                    } else {
                        messageBox.getStyleClass().add("bot-message");
                        senderLabel.setTextFill(Color.web("#4CAF50"));
                    }
                    break;
                case SYSTEM:
                    messageBox.getStyleClass().add("system-message");
                    senderLabel.setTextFill(Color.web("#FF9800"));
                    break;
            }

            return messageBox;
        }

        private String processMessageContent(Message message) {
            String content = message.getContent();

            // Handle private messages
            if (content.startsWith("(Private)") && message.getType() == MessageType.BOT) {
                return "🔒 " + content;
            }

            // Handle system messages
            if (message.getType() == MessageType.SYSTEM) {
                return "ℹ️ " + content;
            }

            return content;
        }
    }
}