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

import java.time.format.DateTimeFormatter;

public class ChatPanel {
    private final VBox chatContainer;
    private final TextArea chatArea;
    private final ListView<Message> messageListView;

    public ChatPanel() {
        chatContainer = new VBox();
        chatContainer.getStyleClass().add("chat-area");
        chatContainer.setSpacing(10);

        // Chat header
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPadding(new Insets(0, 0, 10, 0));

        Label chatTitle = new Label("Messages");
        chatTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #495057;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label messageCount = new Label("0 messages");
        messageCount.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c757d;");

        headerBox.getChildren().addAll(chatTitle, spacer, messageCount);

        // Chat area (using TextArea for compatibility)
        chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setWrapText(true);
        chatArea.setStyle("""
            -fx-background-color: #fafafa;
            -fx-border-color: #e0e0e0;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
            -fx-font-family: 'Segoe UI', 'Arial', sans-serif;
            -fx-font-size: 13px;
            -fx-padding: 15;
            """);

        // Message ListView (for advanced message display)
        messageListView = new ListView<>();
        messageListView.setCellFactory(listView -> new EnhancedMessageCell());
        messageListView.setStyle("""
            -fx-background-color: #fafafa;
            -fx-border-color: #e0e0e0;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
            -fx-padding: 10;
            """);

        // Use TextArea by default, can switch to ListView when needed
        VBox.setVgrow(chatArea, Priority.ALWAYS);

        chatContainer.getChildren().addAll(headerBox, chatArea);
    }

    public VBox getComponent() {
        return chatContainer;
    }

    public void addMessage(String message) {
        Platform.runLater(() -> {
            chatArea.appendText(message + "\n");
            chatArea.setScrollTop(Double.MAX_VALUE);
            updateMessageCount();
        });
    }

    public void addMessage(Message message) {
        Platform.runLater(() -> {
            if (messageListView.getParent() == null) {
                // Switch to ListView mode
                switchToListView();
            }
            messageListView.getItems().add(message);
            scrollToBottom();
            updateMessageCount();
        });
    }

    private void switchToListView() {
        chatContainer.getChildren().remove(chatArea);
        chatContainer.getChildren().add(messageListView);
        VBox.setVgrow(messageListView, Priority.ALWAYS);
    }

    private void scrollToBottom() {
        Platform.runLater(() -> {
            if (messageListView.getItems().size() > 0) {
                messageListView.scrollTo(messageListView.getItems().size() - 1);
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
        HBox headerBox = (HBox) chatContainer.getChildren().get(0);
        Label countLabel = (Label) headerBox.getChildren().get(2);

        int count = messageListView.getItems().size();
        if (count == 0 && !chatArea.getText().trim().isEmpty()) {
            // Count lines in text area
            count = chatArea.getText().split("\n").length;
        }

        countLabel.setText(count + " message" + (count != 1 ? "s" : ""));
    }

    // Getters
    public TextArea getChatArea() { return chatArea; }
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

                // Remove default cell background
                setStyle("-fx-background-color: transparent; -fx-padding: 5 10;");
            }
        }

        private VBox createMessageBox(Message message) {
            VBox messageBox = new VBox(8);
            messageBox.setPadding(new Insets(12, 15, 12, 15));

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
            contentLabel.setMaxWidth(400);

            messageBox.getChildren().addAll(headerBox, contentLabel);

            // Style based on message type
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