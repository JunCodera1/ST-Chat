package me.chatapp.stchat.view.components.organisms.Panel;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.TextFlow;
import me.chatapp.stchat.model.Message;
import me.chatapp.stchat.model.User;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;


import java.time.LocalDateTime;
import java.util.function.Consumer;

import static me.chatapp.stchat.util.CSSUtil.*;
import static me.chatapp.stchat.util.DisplayUtil.*;

public class ChatPanel {

    private final VBox chatContainer;
    private final ScrollPane scrollPane;
    private final VBox messageContainer;
    private final User currentUser;
    private Label messageCountLabel;
    private Label chatTitleLabel;

    public ChatPanel(User currentUser, Consumer<String> sendMessageHandler) {
        this.currentUser = currentUser;
        chatContainer = new VBox();
        chatContainer.setSpacing(0);
        chatContainer.setStyle(STYLE_CHAT_CONTAINER);

        // Chat header with improved styling
        HBox headerBox = createChatHeader();

        // Create scrollable message container
        messageContainer = new VBox();
        messageContainer.setSpacing(2);
        messageContainer.setPadding(new Insets(10, 15, 10, 15));
        messageContainer.setStyle(STYLE_MESSAGE_CONTAINER);

        // Empty state
        Label emptyStateLabel = new Label("No messages yet");
        emptyStateLabel.setStyle(STYLE_EMPTY_STATE_LABEL);
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
        scrollPane.setStyle(STYLE_SCROLL_PANE);

        chatContainer.getChildren().addAll(headerBox, createSeparator(), scrollPane);

        VBox.setVgrow(scrollPane, Priority.ALWAYS);
    }

    private HBox createChatHeader() {
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPadding(new Insets(15, 20, 15, 20));
        headerBox.setStyle("-fx-background-color: #ffffff;");

        chatTitleLabel = new Label("Messages");
        chatTitleLabel.setStyle(STYLE_CHAT_TITLE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        messageCountLabel = new Label("0 messages");
        messageCountLabel.setStyle(STYLE_MESSAGE_COUNT);

        headerBox.getChildren().addAll(chatTitleLabel, spacer, messageCountLabel);
        return headerBox;
    }

    public void setCurrentContact(String name, String type) {
        Platform.runLater(() -> {
            if (type.equalsIgnoreCase("user")) {
                chatTitleLabel.setText("Chat with " + name);
            } else if (type.equalsIgnoreCase("channel")) {
                chatTitleLabel.setText("#" + name);
            } else {
                chatTitleLabel.setText(name);
            }
        });
    }


    public VBox getComponent() {
        return chatContainer;
    }

    public void addMessage(String message) {
        Platform.runLater(() -> {
            Message msgObj = new Message("System", message, Message.MessageType.SYSTEM, LocalDateTime.now());
            addMessageToContainer(msgObj);
        });
    }

    public void addMessage(Message message) {
        Platform.runLater(() -> addMessageToContainer(message));
    }

    private void addMessageToContainer(Message message) {
        // Kiểm tra và xóa empty state nếu có
        removeEmptyStateIfExists();

        VBox messageBox = createMessageBox(message);
        messageContainer.getChildren().add(messageBox);

        // Add spacing between messages
        if (messageContainer.getChildren().size() > 1) {
            VBox.setMargin(messageBox, new Insets(8, 0, 0, 0));
        }

        scrollToBottom();
        updateMessageCount();
    }

    private void removeEmptyStateIfExists() {
        if (messageContainer.getChildren().size() == 1) {
            javafx.scene.Node firstChild = messageContainer.getChildren().get(0);
            if (firstChild instanceof VBox) {
                VBox vbox = (VBox) firstChild;
                // Kiểm tra xem có phải là empty state container không
                if (vbox.getChildren().size() == 1 &&
                        vbox.getChildren().get(0) instanceof Label) {
                    Label label = (Label) vbox.getChildren().get(0);
                    if ("No messages yet".equals(label.getText())) {
                        messageContainer.getChildren().clear();
                    }
                }
            }
        }
    }

    private VBox createMessageBox(Message message) {
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
        senderLabel.setStyle(STYLE_SENDER_LABEL);

        Label timeLabel = new Label(message.getFormattedTime());
        timeLabel.setStyle(STYLE_TIME_LABEL);

        senderTimeBox.getChildren().addAll(senderLabel, timeLabel);
        headerBox.getChildren().addAll(avatar, senderTimeBox);

        // Message content
        TextFlow contentFlow = parseMessageToTextFlow(processMessageContent(message));
        contentFlow.setMaxWidth(450);
        contentFlow.setStyle(STYLE_CONTENT_LABEL);
        bubble.getChildren().addAll(headerBox, contentFlow);

        // Style message bubble based on type
        styleMessageBubble(bubble, senderLabel, contentFlow, message);

        HBox actionBar = new HBox();
        actionBar.setSpacing(8);
        actionBar.setAlignment(Pos.CENTER_RIGHT);

        // Reply
        Button replyBtn = new Button();
        replyBtn.setGraphic(new FontIcon(FontAwesome.REPLY));
        replyBtn.setOnAction(e -> handleReply(message));

        // Edit
        Button editBtn = new Button();
        editBtn.setGraphic(new FontIcon(FontAwesome.EDIT));
        editBtn.setOnAction(e -> handleEdit(message));

        // Delete
        Button deleteBtn = new Button();
        deleteBtn.setGraphic(new FontIcon(FontAwesome.TRASH));
        deleteBtn.setOnAction(e -> handleDelete(message));

        actionBar.getChildren().addAll(replyBtn, editBtn, deleteBtn);
        bubble.getChildren().add(actionBar);

        if (message.getType() == Message.MessageType.USER) {
            bubbleContainer.setAlignment(Pos.CENTER_RIGHT);
            HBox.setMargin(bubble, new Insets(0, 0, 0, 50));
        } else {
            bubbleContainer.setAlignment(Pos.CENTER_LEFT);
            HBox.setMargin(bubble, new Insets(0, 50, 0, 0));
        }

        bubbleContainer.getChildren().add(bubble);
        messageBox.getChildren().add(bubbleContainer);

        messageBox.setUserData(message);

        return messageBox;
    }

    private void handleDelete(Message message) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Message");
        confirm.setHeaderText("Do you want to delete this message?");
        confirm.setContentText("\"" + message.getContent() + "\"");

        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                messageContainer.getChildren().removeIf(node -> {
                    if (node instanceof VBox messageBox) {
                        return messageBox.getUserData() == message;
                    }
                    return false;
                });
                updateMessageCount();
            }
        });
    }

    private void handleEdit(Message message) {
        TextInputDialog dialog = new TextInputDialog(message.getContent());
        dialog.setTitle("Edit Message");
        dialog.setHeaderText("Update your message:");
        dialog.setContentText("Message:");

        dialog.showAndWait().ifPresent(newContent -> {
            if (!newContent.trim().isEmpty()) {
                message.setContent(newContent.trim());
                refreshMessageNode(message);
            }
        });
    }


    private void handleReply(Message original) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Reply to " + original.getSender());
        dialog.setHeaderText("Enter your reply:");
        dialog.setContentText("Reply:");

        dialog.showAndWait().ifPresent(reply -> {
            if (!reply.trim().isEmpty()) {
                Message replyMsg = new Message(
                        "You",
                        "↩️ Reply to " + original.getSender() + ": " + reply.trim(),
                        Message.MessageType.USER,
                        LocalDateTime.now()
                );
                addMessage(replyMsg);
            }
        });
    }


    private String processMessageContent(Message message) {
        String content = message.getContent();

        // Handle private messages
        if (content.startsWith("(Private)") && message.getType() == Message.MessageType.BOT) {
            return "🔒 " + content.substring(9).trim();
        }

        // Handle system messages
        if (message.getType() == Message.MessageType.SYSTEM) {
            return "ℹ️ " + content;
        }

        return content;
    }


    private void scrollToBottom() {
        Platform.runLater(() -> {
            scrollPane.layout();

            // Tính toán vị trí cuộn chính xác
            double contentHeight = messageContainer.getBoundsInLocal().getHeight();
            double viewportHeight = scrollPane.getViewportBounds().getHeight();

            if (contentHeight > viewportHeight) {
                scrollPane.setVvalue(1.0);
            }
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
        int count = 0;

        for (javafx.scene.Node node : messageContainer.getChildren()) {
            if (node instanceof VBox) {
                VBox vbox = (VBox) node;
                // Kiểm tra xem có phải là message box thật không (có userData)
                if (vbox.getUserData() instanceof Message) {
                    count++;
                }
                // Hoặc kiểm tra theo structure
                else if (vbox.getChildren().size() == 1 &&
                        vbox.getChildren().get(0) instanceof HBox) {
                    HBox hbox = (HBox) vbox.getChildren().get(0);
                    if (hbox.getChildren().size() == 1 &&
                            hbox.getChildren().get(0) instanceof VBox) {
                        count++;
                    }
                }
            }
        }

        messageCountLabel.setText(count + " message" + (count != 1 ? "s" : ""));
    }

    // Method to add typing indicator
    public void showTypingIndicator(String sender) {
        Platform.runLater(() -> {
            HBox typingBox = new HBox();
            typingBox.setSpacing(8);
            typingBox.setAlignment(Pos.CENTER_LEFT);
            typingBox.setPadding(new Insets(8, 16, 8, 16));
            typingBox.setStyle(STYLE_TYPING_BOX);

            Circle avatar = new Circle(12);
            avatar.setFill(Color.web("#95a5a6"));

            Label typingLabel = new Label(sender + " is typing...");
            typingLabel.setStyle(STYLE_TYPING_LABEL);

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
            if (!messageContainer.getChildren().isEmpty()) {
                int lastIndex = messageContainer.getChildren().size() - 1;
                if (messageContainer.getChildren().get(lastIndex) instanceof HBox lastItem) {
                    if (!lastItem.getChildren().isEmpty() && lastItem.getChildren().get(0) instanceof HBox typingBox) {
                        if (typingBox.getChildren().size() > 1 && typingBox.getChildren().get(1) instanceof Label label) {
                            if (label.getText().contains(" is typing...")) {
                                messageContainer.getChildren().remove(lastIndex);
                            }
                        }
                    }
                }
            }
        });
    }

    private void refreshMessageNode(Message message) {
        for (javafx.scene.Node node : messageContainer.getChildren()) {
            if (node instanceof VBox messageBox) {
                if (messageBox.getUserData() == message) {
                    if (!messageBox.getChildren().isEmpty() && messageBox.getChildren().get(0) instanceof HBox bubbleContainer) {
                        if (!bubbleContainer.getChildren().isEmpty() && bubbleContainer.getChildren().get(0) instanceof VBox bubble) {
                            for (javafx.scene.Node child : bubble.getChildren()) {
                                if (child instanceof TextFlow) {
                                    TextFlow updatedFlow = parseMessageToTextFlow(processMessageContent(message));
                                    updatedFlow.setMaxWidth(450);
                                    updatedFlow.setStyle(STYLE_CONTENT_LABEL);
                                    int idx = bubble.getChildren().indexOf(child);
                                    bubble.getChildren().set(idx, updatedFlow);

                                    VBox senderTimeBox = (VBox) ((HBox) bubble.getChildren().get(0)).getChildren().get(1);
                                    Label senderLabel = (Label) senderTimeBox.getChildren().get(0);
                                    styleMessageBubble(bubble, senderLabel, updatedFlow, message);
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}