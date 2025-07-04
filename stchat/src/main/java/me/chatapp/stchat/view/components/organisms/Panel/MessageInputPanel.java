package me.chatapp.stchat.view.components.organisms.Panel;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import me.chatapp.stchat.view.components.atoms.Button.AttachmentButton;
import me.chatapp.stchat.view.components.atoms.Button.MicrophoneButton;
import me.chatapp.stchat.view.components.atoms.Button.SendButton;
import me.chatapp.stchat.view.components.atoms.TextField.MessageTextField;
import me.chatapp.stchat.view.components.molecules.Picker.EmojiPicker;

public class MessageInputPanel {
    private final VBox container;
    private final HBox inputRow;
    private final MessageTextField messageField;
    private final EmojiPicker emojiPicker;
    private final AttachmentButton attachmentButton;
    private final SendButton sendButton;
    private final MicrophoneButton microphoneButton;

    public MessageInputPanel() {
        container = new VBox();
        setupContainer();

        inputRow = new HBox();
        messageField = new MessageTextField();
        emojiPicker = new EmojiPicker(this::appendToInput);
        attachmentButton = new AttachmentButton();
        sendButton = new SendButton();
        microphoneButton = new MicrophoneButton();

        setupInputRow();
        setupEventHandlers();

        container.getChildren().add(inputRow);
    }

    private void appendToInput(String iconCode) {
        messageField.appendText(" :" + iconCode + ": "); // ví dụ chèn :fas-smile:
        messageField.requestFocus();
    }


    private void setupContainer() {
        container.setPadding(new Insets(15, 20, 15, 20));
        container.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #e9ecef; " +
                "-fx-border-width: 1px 0 0 0;");
    }

    private void setupInputRow() {
        inputRow.setAlignment(Pos.CENTER);

        // Left side buttons
        HBox leftButtons = new HBox(5);
        leftButtons.setAlignment(Pos.CENTER);
        leftButtons.getChildren().addAll(attachmentButton, emojiPicker.getEmojiButton());

        // Right side buttons
        HBox rightButtons = new HBox(5);
        rightButtons.setAlignment(Pos.CENTER);
        rightButtons.getChildren().addAll(microphoneButton, sendButton);

        // Set message field to grow
        HBox.setHgrow(messageField, Priority.ALWAYS);

        inputRow.getChildren().addAll(leftButtons, messageField, rightButtons);
    }

    private void setupEventHandlers() {
        // Enable/disable send button based on text input
        messageField.textProperty().addListener((obs, oldText, newText) -> {
            boolean hasText = !newText.trim().isEmpty();
            sendButton.setDisable(!hasText);

            // Show/hide microphone button based on text
            if (hasText) {
                if (inputRow.getChildren().contains(microphoneButton)) {
                    HBox rightButtons = (HBox) inputRow.getChildren().get(2);
                    rightButtons.getChildren().remove(microphoneButton);
                }
            } else {
                HBox rightButtons = (HBox) inputRow.getChildren().get(2);
                if (!rightButtons.getChildren().contains(microphoneButton)) {
                    rightButtons.getChildren().add(0, microphoneButton);
                }
            }
        });

        // Send on Enter key
        messageField.setOnAction(e -> sendMessage());
        sendButton.setOnAction(e -> sendMessage());

        // Attachment button action
        attachmentButton.setOnAction(e -> handleAttachment());

        // Microphone button action
        microphoneButton.setOnAction(e -> handleVoiceInput());

        // Initial state
        sendButton.setDisable(true);
    }

    private void onEmojiSelected() {
        messageField.requestFocus();
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            // Handle sending message
            System.out.println("Sending message: " + message);
            messageField.clear();
            messageField.requestFocus();
        }
    }

    private void handleAttachment() {
        // Handle file attachment
        System.out.println("Opening file picker...");
    }

    private void handleVoiceInput() {
        // Handle voice input
        System.out.println("Starting voice input...");
    }

    // Public API
    public VBox getComponent() {
        return container;
    }

    public MessageTextField getMessageField() {
        return messageField;
    }

    public SendButton getSendButton() {
        return sendButton;
    }

    public void focusInput() {
        messageField.requestFocus();
    }

    public void clearInput() {
        messageField.clear();
        messageField.requestFocus();
    }

    public void setInputEnabled(boolean enabled) {
        messageField.setDisable(!enabled);
        sendButton.setDisable(!enabled);
        attachmentButton.setDisable(!enabled);
        emojiPicker.getEmojiButton().setDisable(!enabled);
        microphoneButton.setDisable(!enabled);
    }
}