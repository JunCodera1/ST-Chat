package me.chatapp.stchat.view.components.organisms.Panel;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import me.chatapp.stchat.funtional.TriConsumer;
import me.chatapp.stchat.model.User;
import me.chatapp.stchat.util.VoiceRecorder;
import me.chatapp.stchat.view.components.atoms.Button.AttachmentButton;
import me.chatapp.stchat.view.components.atoms.Button.MicrophoneButton;
import me.chatapp.stchat.view.components.atoms.Button.SendButton;
import me.chatapp.stchat.view.components.atoms.TextField.MessageTextField;
import me.chatapp.stchat.view.components.molecules.Picker.EmojiPicker;

import java.io.File;
import java.util.function.Consumer;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import static me.chatapp.stchat.util.FileUtil.*;

public class MessageInputPanel {
    private static final Logger LOGGER = Logger.getLogger(MessageInputPanel.class.getName());
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB
    private static final int MAX_MESSAGE_LENGTH = 4000;
    private static final String RECORDING_ANIMATION = "🔴 Recording";

    // UI Components
    private final VBox container;
    private final HBox inputRow;
    private final MessageTextField messageField;
    private final EmojiPicker emojiPicker;
    private final AttachmentButton attachmentButton;
    private final SendButton sendButton;
    private final MicrophoneButton microphoneButton;
    private final Label statusLabel;
    private final ProgressBar uploadProgress;

    // Data
    private User sender;
    private User receiver;

    // Callbacks
    private TriConsumer<User, User, String> sendMessageCallback;
    private Consumer<File> sendFileCallback;
    private Consumer<String> sendVoiceCallback;
    private Consumer<String> typingCallback;

    // State management
    private final BooleanProperty isRecording = new SimpleBooleanProperty(false);
    private final BooleanProperty isUploading = new SimpleBooleanProperty(false);
    private final BooleanProperty isTyping = new SimpleBooleanProperty(false);
    private final BooleanProperty conversationReady = new SimpleBooleanProperty(false);
    private final BooleanProperty busy = new SimpleBooleanProperty(false);

    private final VoiceRecorder voiceRecorder;
    private final ExecutorService executorService;

    // Animations and timers
    private Timeline recordingAnimation;
    private Timeline typingTimer;
    private int recordingDots = 0;

    public MessageInputPanel(TriConsumer<User, User, String> sendMessageCallback) {
        this.sendMessageCallback = sendMessageCallback;
        this.voiceRecorder = new VoiceRecorder();
        this.executorService = Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            t.setName("MessageInputPanel-" + t.getId());
            return t;
        });

        // Initialize UI components
        container = new VBox();
        inputRow = new HBox();
        messageField = new MessageTextField();
        emojiPicker = new EmojiPicker(this::appendToInput);
        attachmentButton = new AttachmentButton();
        sendButton = new SendButton();
        microphoneButton = new MicrophoneButton();
        statusLabel = new Label();
        uploadProgress = new ProgressBar();

        initializeUI();
        setupEventHandlers();
        setupPropertyBindings();
    }

    private void initializeUI() {
        setupContainer();
        setupInputRow();
        setupStatusArea();

        container.getChildren().addAll(inputRow);
    }
    public void setConversationReady(boolean ready) {
        conversationReady.set(ready);
    }
    private void setupContainer() {
        container.setPadding(new Insets(15, 20, 15, 20));
        container.setStyle(
                "-fx-background-color: white; " +
                        "-fx-border-color: #e9ecef; " +
                        "-fx-border-width: 1px 0 0 0; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, -1);"
        );
    }

    private void setupInputRow() {
        inputRow.setAlignment(Pos.CENTER);
        inputRow.setSpacing(10);

        // Left side buttons
        HBox leftButtons = new HBox(5);
        leftButtons.setAlignment(Pos.CENTER);
        leftButtons.getChildren().addAll(attachmentButton, emojiPicker.getEmojiButton());

        // Right side buttons
        HBox rightButtons = new HBox(5);
        rightButtons.setAlignment(Pos.CENTER);
        rightButtons.getChildren().addAll(microphoneButton, sendButton);

        // Enhanced message field
        messageField.setPromptText("Type a message...");
        messageField.setStyle(
                "-fx-background-radius: 20; " +
                        "-fx-padding: 10 15; " +
                        "-fx-border-radius: 20; " +
                        "-fx-border-color: #d1d5db; " +
                        "-fx-border-width: 1;"
        );

        HBox.setHgrow(messageField, Priority.ALWAYS);
        inputRow.getChildren().addAll(leftButtons, messageField, rightButtons);
    }

    private void setupStatusArea() {
        statusLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 12px;");
        statusLabel.setVisible(false);

        uploadProgress.setVisible(false);
        uploadProgress.setPrefWidth(200);
        uploadProgress.setStyle("-fx-accent: #3b82f6;");

        HBox statusBox = new HBox(10);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        statusBox.getChildren().addAll(statusLabel, uploadProgress);

        container.getChildren().add(statusBox);
    }

    private void setupEventHandlers() {
        System.out.println("TRACE: setupEventHandlers()");
        // Message field events
        messageField.setOnAction(e -> sendMessage());

        // Text change listener for typing indicator
        messageField.textProperty().addListener((obs, oldText, newText) -> {
            handleTextChange(newText);
        });
        sendButton.disableProperty().bind(
                busy.or(messageField.textProperty().isEmpty()).or(conversationReady.not())
        );
        microphoneButton.disableProperty().bind(
                busy.or(conversationReady.not())
        );
        // Button events
        sendButton.setOnAction(e -> sendMessage());
        attachmentButton.setOnAction(e -> handleAttachmentAsync());
        microphoneButton.setOnAction(e -> handleVoiceInput());

        // Character limit enforcement
        messageField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.length() > MAX_MESSAGE_LENGTH) {
                messageField.setText(oldText);
                showWarningAlert("Character Limit",
                        "Message cannot exceed " + MAX_MESSAGE_LENGTH + " characters.");
            }
        });
    }

    private void setupPropertyBindings() {
        // Disable controls during recording or uploading
        BooleanProperty busy = new SimpleBooleanProperty();
        busy.bind(isRecording.or(isUploading));

        attachmentButton.disableProperty().bind(busy);
        sendButton.disableProperty().bind(busy.or(messageField.textProperty().isEmpty()));
        emojiPicker.getEmojiButton().disableProperty().bind(busy);

        // Recording state binding
        isRecording.addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                startRecordingAnimation();
            } else {
                stopRecordingAnimation();
            }
        });
    }

    private void handleTextChange(String newText) {
        if (typingCallback != null && !newText.trim().isEmpty()) {
            if (!isTyping.get()) {
                isTyping.set(true);
                typingCallback.accept("typing");
            }

            // Reset typing timer
            if (typingTimer != null) {
                typingTimer.stop();
            }

            typingTimer = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
                isTyping.set(false);
                typingCallback.accept("stopped_typing");
            }));
            typingTimer.play();
        }
    }

    private void appendToInput(String iconCode) {
        String currentText = messageField.getText();
        String newText = currentText + " :" + iconCode + ": ";

        if (newText.length() <= MAX_MESSAGE_LENGTH) {
            messageField.setText(newText);
            messageField.positionCaret(newText.length());
        }
        messageField.requestFocus();
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        System.out.println("DEBUG: sendMessage() fired — sender=" + sender + ", receiver=" + receiver + ", text=\"" + message + "\"");
        System.out.println("TRACE: sendMessage() — text=\"" + messageField.getText() + "\"");
        try {
            sendMessageCallback.accept(sender, receiver, message);
            messageField.clear();
            messageField.requestFocus();

            // Stop typing indicator
            if (isTyping.get()) {
                isTyping.set(false);
                if (typingCallback != null) {
                    typingCallback.accept("stopped_typing");
                }
            }

            LOGGER.info("Message sent successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to send message", e);
            showErrorAlert("Send Error", "Failed to send message: " + e.getMessage());
        }
    }

    private void handleAttachmentAsync() {
        CompletableFuture.runAsync(() -> {
            Platform.runLater(() -> {
                isUploading.set(true);
                statusLabel.setText("Selecting file...");
                statusLabel.setVisible(true);
            });

            try {
                handleAttachment();
            } finally {
                Platform.runLater(() -> {
                    isUploading.set(false);
                    statusLabel.setVisible(false);
                    uploadProgress.setVisible(false);
                });
            }
        }, executorService);
    }

    private void handleAttachment() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File to Send");
        setupFileFilters(fileChooser);

        Platform.runLater(() -> {
            Stage stage = (Stage) container.getScene().getWindow();
            List<File> selectedFiles = fileChooser.showOpenMultipleDialog(stage);

            if (selectedFiles != null && !selectedFiles.isEmpty()) {
                processSelectedFiles(selectedFiles);
            }
        });
    }

    private void setupFileFilters(FileChooser fileChooser) {
        FileChooser.ExtensionFilter allFiles = new FileChooser.ExtensionFilter("All Files", "*.*");
        FileChooser.ExtensionFilter imageFiles = new FileChooser.ExtensionFilter("Images",
                "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp", "*.svg", "*.webp");
        FileChooser.ExtensionFilter documentFiles = new FileChooser.ExtensionFilter("Documents",
                "*.pdf", "*.doc", "*.docx", "*.txt", "*.rtf", "*.odt", "*.xls", "*.xlsx");
        FileChooser.ExtensionFilter videoFiles = new FileChooser.ExtensionFilter("Videos",
                "*.mp4", "*.avi", "*.mkv", "*.mov", "*.wmv", "*.flv", "*.webm");
        FileChooser.ExtensionFilter audioFiles = new FileChooser.ExtensionFilter("Audio",
                "*.mp3", "*.wav", "*.ogg", "*.m4a", "*.flac", "*.aac");

        fileChooser.getExtensionFilters().addAll(imageFiles, documentFiles, videoFiles, audioFiles, allFiles);
    }

    private void processSelectedFiles(List<File> selectedFiles) {
        if (selectedFiles.size() > 10) {
            showWarningAlert("Too Many Files", "You can only select up to 10 files at once.");
            return;
        }

        for (File file : selectedFiles) {
            processFileAsync(file);
        }
    }

    private void processFileAsync(File file) {
        CompletableFuture.runAsync(() -> {
            Platform.runLater(() -> {
                statusLabel.setText("Processing " + file.getName() + "...");
                uploadProgress.setVisible(true);
                uploadProgress.setProgress(0.0);
            });

            try {
                handleSelectedFile(file);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to process file: " + file.getName(), e);
                Platform.runLater(() ->
                        showErrorAlert("File Error", "Failed to process file: " + e.getMessage()));
            }
        }, executorService);
    }

    private void handleSelectedFile(File file) {
        if (file == null || !file.exists()) {
            showErrorAlert("Error", "File not found or cannot be accessed.");
            return;
        }

        if (file.length() > MAX_FILE_SIZE) {
            showErrorAlert("File Too Large",
                    String.format("File size exceeds %dMB limit. Please select a smaller file.",
                            MAX_FILE_SIZE / (1024 * 1024)));
            return;
        }

        try {
            // Simulate upload progress
            for (int i = 0; i <= 100; i += 10) {
                final int progress = i;
                Platform.runLater(() -> uploadProgress.setProgress(progress / 100.0));
                Thread.sleep(50); // Simulate processing time
            }

            String fileName = file.getName();
            String fileExtension = getFileExtension(fileName);
            String fileType = getFileType(fileExtension);

            // Enhanced message format with file info
            String attachmentMessage = String.format("📎 %s\n📄 Type: %s\n📊 Size: %s",
                    fileName, fileType, formatFileSize(file.length()));

            Platform.runLater(() -> {
                if (sendMessageCallback != null && sender != null && receiver != null) {
                    sendMessageCallback.accept(sender, receiver, attachmentMessage);
                }

                if (sendFileCallback != null) {
                    sendFileCallback.accept(file);
                }

                showInfoAlert("File Sent",
                        String.format("File \"%s\" has been sent successfully.", fileName));
            });

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to send file", e);
            Platform.runLater(() ->
                    showErrorAlert("Error", "Failed to send file: " + e.getMessage()));
        }
    }

    private void handleVoiceInput() {
        if (!isRecording.get()) {
            startVoiceRecording();
        } else {
            stopVoiceRecording();
        }
    }

    private void startVoiceRecording() {
        try {
            voiceRecorder.startRecording();
            isRecording.set(true);

            statusLabel.setText("Recording voice message...");
            statusLabel.setVisible(true);

            LOGGER.info("Voice recording started");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to start voice recording", e);
            showErrorAlert("Recording Error", "Failed to start voice recording: " + e.getMessage());
        }
    }

    private void stopVoiceRecording() {
        try {
            String recordingPath = voiceRecorder.stopRecording();
            isRecording.set(false);

            statusLabel.setVisible(false);

            if (recordingPath != null) {
                long duration = voiceRecorder.getRecordingDuration();
                String voiceMessage = String.format("🎤 Voice Message (%s)", formatDuration(duration));

                if (sendMessageCallback != null && sender != null && receiver != null) {
                    sendMessageCallback.accept(sender, receiver, voiceMessage);
                }

                if (sendVoiceCallback != null) {
                    sendVoiceCallback.accept(recordingPath);
                }

                showInfoAlert("Voice Message", "Voice message sent successfully.");
                LOGGER.info("Voice message sent successfully");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to stop voice recording", e);
            showErrorAlert("Recording Error", "Failed to stop voice recording: " + e.getMessage());
        }
    }

    private void startRecordingAnimation() {
        recordingAnimation = new Timeline(new KeyFrame(Duration.millis(500), e -> {
            recordingDots = (recordingDots + 1) % 4;
            String dots = ".".repeat(recordingDots);
            microphoneButton.setText(RECORDING_ANIMATION + dots);
        }));
        recordingAnimation.setCycleCount(Timeline.INDEFINITE);
        recordingAnimation.play();

        microphoneButton.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 20;");
    }

    private void stopRecordingAnimation() {
        if (recordingAnimation != null) {
            recordingAnimation.stop();
        }
        microphoneButton.setText("🎤");
        microphoneButton.setStyle("");
    }

    private String formatDuration(long milliseconds) {
        long totalSeconds = milliseconds / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;

        if (minutes > 0) {
            return String.format("%d:%02d", minutes, seconds);
        } else {
            return seconds + "s";
        }
    }

    private void showErrorAlert(String title, String message) {
        showAlert(Alert.AlertType.ERROR, title, message);
    }

    private void showWarningAlert(String title, String message) {
        showAlert(Alert.AlertType.WARNING, title, message);
    }

    private void showInfoAlert(String title, String message) {
        showAlert(Alert.AlertType.INFORMATION, title, message);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
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

    public void setSender(User sender) {
        this.sender = sender;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public User getSender() {
       return sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setSendMessageCallback(TriConsumer<User, User, String> callback) {
        this.sendMessageCallback = callback;
    }
}