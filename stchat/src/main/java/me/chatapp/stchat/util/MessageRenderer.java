package me.chatapp.stchat.util;

import javafx.application.HostServices;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import me.chatapp.stchat.model.Message;
import me.chatapp.stchat.model.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.chatapp.stchat.util.CSSUtil.*;

public class MessageRenderer {
    private final User currentUser;
    private final HostServices hostServices;

    public MessageRenderer(User currentUser, HostServices hostServices) {
        this.currentUser = currentUser;
        this.hostServices = hostServices;
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

        boolean isCurrentUser = message.getSenderId() == currentUser.getId();

        // Header: tên + thời gian (ẩn avatar nếu là current user)
        HBox headerBox = new HBox();
        headerBox.setSpacing(8);

        Label senderLabel = new Label(message.getSender());
        senderLabel.setStyle(STYLE_SENDER_LABEL);

        Label timeLabel = new Label(
                message.getCreatedAt() != null
                        ? message.getCreatedAtFormatted()
                        : "Unknown time"
        );
        timeLabel.setStyle(STYLE_TIME_LABEL);

        VBox senderTimeBox = new VBox(senderLabel, timeLabel);
        senderTimeBox.setSpacing(2);

        if (isCurrentUser) {
            headerBox.setAlignment(Pos.CENTER_RIGHT);
            headerBox.getChildren().add(senderTimeBox); // Chỉ hiện tên và giờ
        } else {
            headerBox.setAlignment(Pos.CENTER_LEFT);
            headerBox.getChildren().addAll(senderTimeBox);
        }

        bubble.getChildren().add(headerBox);

        // Nội dung tin nhắn hoặc file
        if (message.hasAttachment()) {
            VBox attachmentBox = new AttachmentRenderer().createAttachmentBox(message.getAttachment(), messageActions);
            bubble.getChildren().add(attachmentBox);
        } else {
            TextFlow contentFlow = parseMessageToTextFlow(processMessageContent(message));
            contentFlow.setMaxWidth(450);
            contentFlow.setStyle(STYLE_CONTENT_LABEL);
            bubble.getChildren().add(contentFlow);
        }

        // Style bong bóng
        styleMessageBubble(bubble, senderLabel, message);

        // Action bar (sửa, trả lời, xóa)
        HBox actionBar = messageActions.createActionBar(message);
        bubble.getChildren().add(actionBar);

        // Đặt vị trí tổng thể bên trái/phải
        if (isCurrentUser) {
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

    public void styleMessageBubble(VBox bubble, Label senderLabel, Message message) {
        String baseStyle = "-fx-background-radius: 12px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 1);";

        boolean isCurrentUser = message.getSenderId() == currentUser.getId();

        if (isCurrentUser) {
            bubble.setStyle(baseStyle + "-fx-background-color: #2196F3;"); // Màu xanh
            senderLabel.setStyle(STYLE_SENDER_LABEL + "-fx-text-fill: white;");
        } else if (message.getType() == Message.MessageType.USER) {
            bubble.setStyle(baseStyle + "-fx-background-color: #f1f1f1;");
            senderLabel.setStyle(STYLE_SENDER_LABEL + "-fx-text-fill: #333;");
        } else if (message.getType() == Message.MessageType.BOT) {
            bubble.setStyle(baseStyle + "-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 1px; -fx-border-radius: 12px;");
            senderLabel.setStyle(STYLE_SENDER_LABEL + "-fx-text-fill: #495057;");
        } else if (message.getType() == Message.MessageType.SYSTEM) {
            bubble.setStyle(baseStyle + "-fx-background-color: #fff3cd; -fx-border-color: #ffeaa7; -fx-border-width: 1px; -fx-border-radius: 12px;");
            senderLabel.setStyle(STYLE_SENDER_LABEL + "-fx-text-fill: #856404;");
        } else {
            bubble.setStyle(baseStyle + "-fx-background-color: #e9ecef;");
            senderLabel.setStyle(STYLE_SENDER_LABEL + "-fx-text-fill: #495057;");
        }
    }


    public TextFlow parseMessageToTextFlow(String content) {
        TextFlow textFlow = new TextFlow();
        textFlow.setLineSpacing(2);
        textFlow.setStyle("-fx-font-size: 14px; -fx-font-family: 'Segoe UI', Arial, sans-serif;");
        textFlow.setPrefWidth(400); // Tùy chỉnh theo layout bạn cần
        textFlow.setMaxWidth(400);
        textFlow.setTextAlignment(TextAlignment.LEFT);

        String regex = "(https?://[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=%]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);

        int lastIndex = 0;
        while (matcher.find()) {
            if (matcher.start() > lastIndex) {
                String before = content.substring(lastIndex, matcher.start());
                Text text = new Text(before);
                text.setFill(Color.BLACK);
                textFlow.getChildren().add(text);
            }

            String url = matcher.group();
            Hyperlink link = new Hyperlink(url);
            link.setStyle("-fx-text-fill: #1a73e8; -fx-underline: true;");
            link.setOnAction(e -> hostServices.showDocument(url));
            textFlow.getChildren().add(link);

            lastIndex = matcher.end();
        }

        if (lastIndex < content.length()) {
            String remaining = content.substring(lastIndex);
            Text text = new Text(remaining);
            text.setFill(Color.BLACK);
            textFlow.getChildren().add(text);
        }

        return textFlow;
    }



    public HostServices getHostServices() {
        return hostServices;
    }


}