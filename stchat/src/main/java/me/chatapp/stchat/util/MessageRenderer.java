package me.chatapp.stchat.util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import me.chatapp.stchat.model.Message;
import me.chatapp.stchat.model.User;

import static me.chatapp.stchat.util.CSSUtil.*;

public class MessageRenderer {
    private final User currentUser;

    public MessageRenderer(User currentUser) {
        this.currentUser = currentUser;
    }

    public void addMessage(VBox messageContainer, Message message, MessageActions messageActions) {
        removeEmptyStateIfExists(messageContainer);
        VBox messageBox = createMessageBox(message, messageActions);
        messageContainer.getChildren().add(messageBox);
        if (messageContainer.getChildren().size() > 1) {
            VBox.setMargin(messageBox, new Insets(8, 0, 0, 0));
        }
    }

    private void removeEmptyStateIfExists(VBox messageContainer) {
        if (messageContainer.getChildren().size() == 1) {
            javafx.scene.Node firstChild = messageContainer.getChildren().get(0);
            if (firstChild instanceof VBox vbox) {
                if (vbox.getChildren().size() == 1 &&
                        vbox.getChildren().get(0) instanceof Label label) {
                    if ("No messages yet".equals(label.getText())) {
                        messageContainer.getChildren().clear();
                    }
                }
            }
        }
    }

    private VBox createMessageBox(Message message, MessageActions messageActions) {
        VBox messageBox = new VBox();
        messageBox.setSpacing(6);

        HBox bubbleContainer = new HBox();
        VBox bubble = new VBox();
        bubble.setSpacing(4);
        bubble.setPadding(new Insets(12, 16, 12, 16));
        bubble.setMaxWidth(450);

        HBox headerBox = new HBox();
        headerBox.setSpacing(8);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Circle avatar = createAvatar(message);
        VBox senderTimeBox = new VBox();
        senderTimeBox.setSpacing(2);

        Label senderLabel = new Label(message.getSender());
        senderLabel.setStyle(STYLE_SENDER_LABEL);

        Label timeLabel = new Label(
                message.getCreatedAt() != null
                        ? message.getCreatedAtFormatted()
                        : "Unknown time"
        );
        timeLabel.setStyle(STYLE_TIME_LABEL);

        senderTimeBox.getChildren().addAll(senderLabel, timeLabel);
        headerBox.getChildren().addAll(avatar, senderTimeBox);
        bubble.getChildren().add(headerBox);

        if (message.hasAttachment()) {
            VBox attachmentBox = new AttachmentRenderer().createAttachmentBox(message.getAttachment(), messageActions);
            bubble.getChildren().add(attachmentBox);
        } else {
            TextFlow contentFlow = parseMessageToTextFlow(processMessageContent(message));
            contentFlow.setMaxWidth(450);
            contentFlow.setStyle(STYLE_CONTENT_LABEL);
            bubble.getChildren().add(contentFlow);
        }

        styleMessageBubble(bubble, senderLabel, message);
        HBox actionBar = messageActions.createActionBar(message);
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

    public String processMessageContent(Message message) {
        String content = message.getContent();
        if (content.startsWith("(Private)") && message.getType() == Message.MessageType.BOT) {
            return "🔒 " + content.substring(9).trim();
        }
        if (message.getType() == Message.MessageType.SYSTEM) {
            return "ℹ️ " + content;
        }
        return content;
    }

    private Circle createAvatar(Message message) {
        Circle avatar = new Circle(16);
        switch (message.getType()) {
            case USER:
                avatar.setFill(Color.web("#2196F3"));
                break;
            case BOT:
                avatar.setFill(Color.web("#4CAF50"));
                break;
            case SYSTEM:
                avatar.setFill(Color.web("#FF9800"));
                break;
            default:
                avatar.setFill(Color.web("#9E9E9E"));
        }
        return avatar;
    }

    public void styleMessageBubble(VBox bubble, Label senderLabel, Message message) {
        String baseStyle = "-fx-background-radius: 12px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 1);";
        switch (message.getType()) {
            case USER:
                bubble.setStyle(baseStyle + "-fx-background-color: #2196F3;");
                senderLabel.setStyle(STYLE_SENDER_LABEL + "-fx-text-fill: white;");
                break;
            case BOT:
                bubble.setStyle(baseStyle + "-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 1px; -fx-border-radius: 12px;");
                senderLabel.setStyle(STYLE_SENDER_LABEL + "-fx-text-fill: #495057;");
                break;
            case SYSTEM:
                bubble.setStyle(baseStyle + "-fx-background-color: #fff3cd; -fx-border-color: #ffeaa7; -fx-border-width: 1px; -fx-border-radius: 12px;");
                senderLabel.setStyle(STYLE_SENDER_LABEL + "-fx-text-fill: #856404;");
                break;
            default:
                bubble.setStyle(baseStyle + "-fx-background-color: #e9ecef;");
                senderLabel.setStyle(STYLE_SENDER_LABEL + "-fx-text-fill: #495057;");
        }
    }

    public TextFlow parseMessageToTextFlow(String content) {
        TextFlow textFlow = new TextFlow();
        Text text = new Text(content);
        text.setStyle("-fx-font-size: 14px; -fx-font-family: 'Segoe UI', Arial, sans-serif;");
        textFlow.getChildren().add(text);
        return textFlow;
    }
}