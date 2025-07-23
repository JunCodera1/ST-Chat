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
import me.chatapp.stchat.api.MessageApiClient;
import me.chatapp.stchat.api.SocketClient;
import me.chatapp.stchat.funtional.TriConsumer;
import me.chatapp.stchat.model.AttachmentMessage;
import me.chatapp.stchat.model.User;
import me.chatapp.stchat.util.VoiceRecorder;
import me.chatapp.stchat.view.components.atoms.Button.AttachmentButton;
import me.chatapp.stchat.view.components.atoms.Button.MicrophoneButton;
import me.chatapp.stchat.view.components.atoms.Button.SendButton;
import me.chatapp.stchat.view.components.atoms.TextField.MessageTextField;
import me.chatapp.stchat.view.components.molecules.Picker.EmojiPicker;

import java.io.File;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import static me.chatapp.stchat.util.FileUtil.*;
import static me.chatapp.stchat.util.FileUtil.setupFileFilters;


public class MessageInputPanel {
    private static final Logger LOGGER = Logger.getLogger(MessageInputPanel.class.getName());
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB
    private static final int MAX_MESSAGE_LENGTH = 4000;
    private static final String RECORDING_ANIMATION = "🔴 Recording";

    // UI Components
    private final VBox container;
    private final HBox inputRow;
    private final MessageTextField messageField;
    private TriConsumer<User, User, AttachmentMessage> sendAttachmentCallback;
    private final EmojiPicker emojiPicker;
    private final AttachmentButton attachmentButton;
    private final SendButton sendButton;
    private final MicrophoneButton microphoneButton;
    private final Label statusLabel;
    private final ProgressBar uploadProgress;
    private SocketClient socketClient;
    private Integer currentUserId;
    private Integer receiverId;


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

    public MessageInputPanel(
            TriConsumer<User, User, AttachmentMessage> sendAttachmentCallback,
            TriConsumer<User, User, String> sendMessageCallback
    ) {
        this.sendAttachmentCallback = sendAttachmentCallback;
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

    public void setSocketClient(SocketClient socketClient, Integer currentUserId, Integer receiverId) {
        this.socketClient = socketClient;
        this.currentUserId = currentUserId;
        this.receiverId = receiverId;
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
                // Gửi trạng thái typing
                if (socketClient != null && sender != null && receiver != null) {
                    socketClient.sendTypingStatus(sender.getId(), receiver.getId(), true);
                }
            }
            // Reset typing timer
            if (typingTimer != null) {
                typingTimer.stop();
            }
            typingTimer = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
                isTyping.set(false);
                typingCallback.accept("stopped_typing");
                // Gửi trạng thái dừng typing
                if (socketClient != null && sender != null && receiver != null) {
                    socketClient.sendTypingStatus(sender.getId(), receiver.getId(), false);
                }
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

        // 1. Gọi uploadFile
        MessageApiClient apiClient = new MessageApiClient(); // hoặc inject từ ngoài nếu đã có sẵn

        apiClient.uploadFile(file).thenAccept(fileUrl -> {
            String fileName = file.getName();
            String fileExtension = getFileExtension(fileName);
            String fileType = getFileType(fileExtension);

            AttachmentMessage attachment = new AttachmentMessage(
                    fileName,
                    fileType,
                    file.length(),
                    fileUrl,
                    fileExtension
            );

            Platform.runLater(() -> {
                if (sendAttachmentCallback != null && sender != null && receiver != null) {
                    sendAttachmentCallback.accept(sender, receiver, attachment);
                }

                showInfoAlert("File Sent", String.format("File \"%s\" has been sent successfully.", fileName));
            });

        }).exceptionally(ex -> {
            ex.printStackTrace();
            Platform.runLater(() -> showErrorAlert("Upload Failed", "Could not upload file: " + ex.getMessage()));
            return null;
        });
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
                File audioFile = new File(recordingPath);

                if (audioFile.exists() && audioFile.length() > 0) {
                    // Gửi file audio như một attachment thay vì chỉ text
                    processAudioFileAsync(audioFile);

                    LOGGER.info("Voice recording completed and queued for upload: " + recordingPath);
                } else {
                    showErrorAlert("Recording Error", "No audio data recorded or file is empty.");
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to stop voice recording", e);
            showErrorAlert("Recording Error", "Failed to stop voice recording: " + e.getMessage());
        }
    }

    private void processAudioFileAsync(File audioFile) {
        CompletableFuture.runAsync(() -> {
            Platform.runLater(() -> {
                isUploading.set(true);
                statusLabel.setText("Uploading voice message...");
                statusLabel.setVisible(true);
                uploadProgress.setVisible(true);
                uploadProgress.setProgress(0.0);
            });

            try {
                // Upload file audio lên server
                MessageApiClient apiClient = new MessageApiClient();

                apiClient.uploadFile(audioFile).thenAccept(fileUrl -> {
                    String fileName = audioFile.getName();
                    long duration = voiceRecorder.getRecordingDuration();

                    // Tạo attachment cho file audio
                    AttachmentMessage audioAttachment = new AttachmentMessage(
                            fileName,
                            "audio", // Đặt type là "audio"
                            audioFile.length(),
                            fileUrl,
                            getAudioFileExtension(fileName) // wav, mp3, etc.
                    );

                    Platform.runLater(() -> {
                        // Gửi attachment thay vì text message
                        if (sendAttachmentCallback != null && sender != null && receiver != null) {
                            sendAttachmentCallback.accept(sender, receiver, audioAttachment);
                        }

                        // Gửi mô tả voice message (optional)
                        if (sendMessageCallback != null && sender != null && receiver != null) {
                            String voiceDescription = String.format("🎤 Voice Message (%s)",
                                    formatDuration(duration));
                            sendMessageCallback.accept(sender, receiver, voiceDescription);
                        }

                        // Reset UI
                        isUploading.set(false);
                        statusLabel.setVisible(false);
                        uploadProgress.setVisible(false);

                        showInfoAlert("Voice Message", "Voice message sent successfully.");
                        LOGGER.info("Voice message sent as file attachment");
                    });

                }).exceptionally(ex -> {
                    ex.printStackTrace();
                    Platform.runLater(() -> {
                        isUploading.set(false);
                        statusLabel.setVisible(false);
                        uploadProgress.setVisible(false);
                        showErrorAlert("Upload Failed", "Could not upload voice message: " + ex.getMessage());
                    });
                    return null;
                });

            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to upload voice message", e);
                Platform.runLater(() -> {
                    isUploading.set(false);
                    statusLabel.setVisible(false);
                    uploadProgress.setVisible(false);
                    showErrorAlert("Upload Error", "Failed to upload voice message: " + e.getMessage());
                });
            }
        }, executorService);
    }

    private String getAudioFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0 && lastDot < fileName.length() - 1) {
            return fileName.substring(lastDot + 1).toLowerCase();
        }
        return "wav";
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

    public VBox getComponent() {
        return container;
    }

    public MessageTextField getMessageField() {
        return messageField;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

}