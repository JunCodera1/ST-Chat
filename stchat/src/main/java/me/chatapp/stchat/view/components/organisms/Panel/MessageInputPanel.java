package me.chatapp.stchat.view.components.organisms.Panel;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import me.chatapp.stchat.util.VoiceRecorder;
import me.chatapp.stchat.view.components.atoms.Button.AttachmentButton;
import me.chatapp.stchat.view.components.atoms.Button.MicrophoneButton;
import me.chatapp.stchat.view.components.atoms.Button.SendButton;
import me.chatapp.stchat.view.components.atoms.TextField.MessageTextField;
import me.chatapp.stchat.view.components.molecules.Picker.EmojiPicker;

import java.io.File;
import java.util.function.Consumer;
import java.util.List;

import static me.chatapp.stchat.util.FileUtil.*;

public class MessageInputPanel {
    private final VBox container;
    private final HBox inputRow;
    private final MessageTextField messageField;
    private final EmojiPicker emojiPicker;
    private final AttachmentButton attachmentButton;
    private final SendButton sendButton;
    private final MicrophoneButton microphoneButton;

    // Callback để gửi tin nhắn
    private Consumer<String> sendMessageCallback;
    // Callback để gửi file/attachment
    private Consumer<File> sendFileCallback;
    // Callback để gửi voice message
    private Consumer<String> sendVoiceCallback;

    // Voice recording state
    private boolean isRecording = false;
    private final VoiceRecorder voiceRecorder;

    public MessageInputPanel(Consumer<String> sendMessageCallback) {
        this.sendMessageCallback = sendMessageCallback;
        this.voiceRecorder = new VoiceRecorder();

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
        messageField.appendText(" :" + iconCode + ": ");
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
            HBox rightButtons = (HBox) inputRow.getChildren().get(2);
            if (hasText) {
                rightButtons.getChildren().remove(microphoneButton);
            } else {
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

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            // Gọi callback để gửi tin nhắn
            if (sendMessageCallback != null) {
                sendMessageCallback.accept(message);
            }

            // Clear input và focus
            messageField.clear();
            messageField.requestFocus();
        }
    }

    private void handleAttachment() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File to Send");

        // Set up file filters
        FileChooser.ExtensionFilter allFiles = new FileChooser.ExtensionFilter("All Files", "*.*");
        FileChooser.ExtensionFilter imageFiles = new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp");
        FileChooser.ExtensionFilter documentFiles = new FileChooser.ExtensionFilter("Documents", "*.pdf", "*.doc", "*.docx", "*.txt", "*.rtf");
        FileChooser.ExtensionFilter videoFiles = new FileChooser.ExtensionFilter("Videos", "*.mp4", "*.avi", "*.mkv", "*.mov", "*.wmv");
        FileChooser.ExtensionFilter audioFiles = new FileChooser.ExtensionFilter("Audio", "*.mp3", "*.wav", "*.ogg", "*.m4a");

        fileChooser.getExtensionFilters().addAll(imageFiles, documentFiles, videoFiles, audioFiles, allFiles);

        // Try to get the current stage from the scene
        Stage stage = (Stage) container.getScene().getWindow();
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(stage);

        if (selectedFiles != null && !selectedFiles.isEmpty()) {
            for (File file : selectedFiles) {
                handleSelectedFile(file);
            }
        }
    }

    private void handleSelectedFile(File file) {
        if (file == null || !file.exists()) {
            showAlert("Error", "File not found or cannot be accessed.");
            return;
        }

        // Check file size (limit to 10MB)
        long maxSize = 10 * 1024 * 1024; // 10MB in bytes
        if (file.length() > maxSize) {
            showAlert("File Too Large", "File size exceeds 10MB limit. Please select a smaller file.");
            return;
        }

        try {
            // Create a message with file attachment
            String fileName = file.getName();
            String fileExtension = getFileExtension(fileName);
            String fileType = getFileType(fileExtension);

            // Format the message to include file info
            String attachmentMessage = String.format("📎 %s [%s - %s]",
                    fileName,
                    fileType,
                    formatFileSize(file.length())
            );

            // Send the attachment message
            if (sendMessageCallback != null) {
                sendMessageCallback.accept(attachmentMessage);
            }

            // If there's a file callback, also send the file
            if (sendFileCallback != null) {
                sendFileCallback.accept(file);
            }

            // Show success message
            showInfoAlert("File Sent", "File \"" + fileName + "\" has been sent successfully.");

        } catch (Exception e) {
            showAlert("Error", "Failed to send file: " + e.getMessage());
        }
    }



    private void handleVoiceInput() {
        if (!isRecording) {
            startVoiceRecording();
        } else {
            stopVoiceRecording();
        }
    }

    private void startVoiceRecording() {
        try {
            voiceRecorder.startRecording();
            isRecording = true;

            // Update UI to show recording state
            microphoneButton.setText("🔴 Stop");
            microphoneButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");

            // Disable other buttons during recording
            attachmentButton.setDisable(true);
            sendButton.setDisable(true);
            emojiPicker.getEmojiButton().setDisable(true);

            showInfoAlert("Recording", "Voice recording started. Click the microphone button again to stop.");

        } catch (Exception e) {
            showAlert("Recording Error", "Failed to start voice recording: " + e.getMessage());
        }
    }

    private void stopVoiceRecording() {
        try {
            String recordingPath = voiceRecorder.stopRecording();
            isRecording = false;

            // Reset UI
            microphoneButton.setText("🎤");
            microphoneButton.setStyle("");

            // Re-enable buttons
            attachmentButton.setDisable(false);
            sendButton.setDisable(false);
            emojiPicker.getEmojiButton().setDisable(false);

            if (recordingPath != null) {
                // Send voice message
                String voiceMessage = "🎤 Voice Message [" + formatDuration(voiceRecorder.getRecordingDuration()) + "]";

                if (sendMessageCallback != null) {
                    sendMessageCallback.accept(voiceMessage);
                }

                if (sendVoiceCallback != null) {
                    sendVoiceCallback.accept(recordingPath);
                }

                showInfoAlert("Voice Message", "Voice message sent successfully.");
            }

        } catch (Exception e) {
            showAlert("Recording Error", "Failed to stop voice recording: " + e.getMessage());
        }
    }

    private String formatDuration(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;

        if (minutes > 0) {
            return String.format("%d:%02d", minutes, seconds);
        } else {
            return seconds + "s";
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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

    public void setSendMessageCallback(Consumer<String> callback) {
        this.sendMessageCallback = callback;
    }

    public void setSendFileCallback(Consumer<File> callback) {
        this.sendFileCallback = callback;
    }

    public void setSendVoiceCallback(Consumer<String> callback) {
        this.sendVoiceCallback = callback;
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