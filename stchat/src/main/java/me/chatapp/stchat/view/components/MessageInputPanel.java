package me.chatapp.stchat.view.components;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

public class MessageInputPanel {
    private final VBox inputContainer;
    private final TextField messageField;
    private final Button sendButton;
    private final Button clearButton;
    private final Button emojiButton;

    private final Popup emojiPopup = new Popup();

    public MessageInputPanel() {
        inputContainer = new VBox();
        inputContainer.getStyleClass().add("message-input-area");
        inputContainer.setSpacing(10);

        // Input header
        Label inputLabel = new Label("Type your message");
        inputLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #495057; -fx-font-size: 14px;");

        // Message input row
        HBox inputRow = new HBox(10);
        inputRow.setAlignment(Pos.CENTER);

        // Message field
        messageField = new TextField();
        messageField.getStyleClass().add("modern-text-field");
        messageField.setPromptText("Enter your message...");
        messageField.setStyle(messageField.getStyle() + "; -fx-pref-height: 40px;");
        HBox.setHgrow(messageField, Priority.ALWAYS);

        // Emoji button (future feature)
        emojiButton = new Button("😊");
        emojiButton.getStylesheets().add("emoji-btn");
        emojiButton.setOnAction(e -> showEmojiPopup());

        // Send button
        sendButton = new Button("Send");
        sendButton.getStyleClass().add("primary-button");
        sendButton.setPrefWidth(80);
        sendButton.setDisable(true);

        // Clear button
        clearButton = new Button("Clear");
        clearButton.getStyleClass().add("danger-button");
        clearButton.setPrefWidth(80);

        inputRow.getChildren().addAll(messageField, emojiButton, sendButton, clearButton);

        // Additional controls row
        HBox controlsRow = new HBox(15);
        controlsRow.setAlignment(Pos.CENTER_LEFT);

        // Character counter
        Label charCounter = new Label("0/500");
        charCounter.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c757d;");

        // Message field listener for character count
        messageField.textProperty().addListener((obs, oldText, newText) -> {
            int length = newText.length();
            charCounter.setText(length + "/500");

            if (length > 500) {
                charCounter.setStyle("-fx-font-size: 12px; -fx-text-fill: #dc3545;");
                messageField.setText(oldText); // Prevent exceeding limit
            } else if (length > 400) {
                charCounter.setStyle("-fx-font-size: 12px; -fx-text-fill: #ffc107;");
            } else {
                charCounter.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c757d;");
            }

            // Enable/disable send button based on content
            sendButton.setDisable(newText.trim().isEmpty());
        });

        // Typing indicator placeholder
        Label typingIndicator = new Label();
        typingIndicator.setStyle("-fx-font-size: 12px; -fx-text-fill: #28a745; -fx-font-style: italic;");

        controlsRow.getChildren().addAll(charCounter, typingIndicator);

        inputContainer.getChildren().addAll(inputLabel, inputRow, controlsRow);
    }

    private void showEmojiPopup() {
        if (emojiPopup.isShowing()) {
            emojiPopup.hide();
            return;
        }

        // Danh sách emoji mẫu
        String[] emojis = {"😊", "😂", "😍", "🤔", "👍", "❤️", "🎉", "🔥", "😎", "😭", "😡", "😱"};

        FlowPane emojiPane = new FlowPane();
        emojiPane.setHgap(10);
        emojiPane.setVgap(10);
        emojiPane.setPrefWrapLength(200);
        emojiPane.setPadding(new javafx.geometry.Insets(10));
        emojiPane.setStyle("-fx-background-color: white; -fx-border-color: gray; -fx-border-radius: 5; -fx-background-radius: 5;");

        for (String emoji : emojis) {
            Button emojiBtn = new Button(emoji);
            emojiBtn.setStyle("-fx-font-size: 20px; -fx-background-color: transparent;");
            emojiBtn.setOnAction(e -> {
                messageField.appendText(emoji);
                emojiPopup.hide();
                messageField.requestFocus();
            });
            emojiPane.getChildren().add(emojiBtn);
        }

        emojiPopup.getContent().clear();
        emojiPopup.getContent().add(emojiPane);
        emojiPopup.setAutoHide(true);

        // Hiển thị popup ngay dưới nút emoji
        javafx.geometry.Bounds bounds = emojiButton.localToScreen(emojiButton.getBoundsInLocal());
        emojiPopup.show(emojiButton, bounds.getMinX(), bounds.getMaxY());
    }


    public VBox getComponent() {
        return inputContainer;
    }

    // Getters
    public TextField getMessageField() { return messageField; }
    public Button getSendButton() { return sendButton; }
    public Button getClearButton() { return clearButton; }
    public Button getEmojiButton() { return emojiButton; }

    // State management
    public void setInputEnabled(boolean enabled) {
        messageField.setDisable(!enabled);
        sendButton.setDisable(!enabled || messageField.getText().trim().isEmpty());
        emojiButton.setDisable(!enabled);
    }

    public void clearInput() {
        messageField.clear();
        messageField.requestFocus();
    }

    public void showTypingIndicator(String username) {
        HBox controlsRow = (HBox) inputContainer.getChildren().get(2);
        Label typingIndicator = (Label) controlsRow.getChildren().get(1);
        typingIndicator.setText(username + " is typing...");
    }

    public void hideTypingIndicator() {
        HBox controlsRow = (HBox) inputContainer.getChildren().get(2);
        Label typingIndicator = (Label) controlsRow.getChildren().get(1);
        typingIndicator.setText("");
    }

    public void focusInput() {
        messageField.requestFocus();
    }
}