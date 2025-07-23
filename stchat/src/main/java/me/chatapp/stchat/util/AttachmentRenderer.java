package me.chatapp.stchat.util;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import me.chatapp.stchat.model.AttachmentMessage;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;

import static me.chatapp.stchat.util.CSSUtil.*;
import static me.chatapp.stchat.util.DisplayUtil.*;
import static me.chatapp.stchat.util.FileUtil.*;

public class AttachmentRenderer {

    public VBox createAttachmentBox(AttachmentMessage attachment, MessageActions messageActions) {
        System.out.println("DEBUG: File path = " + attachment.getFilePath());
        System.out.println("DEBUG: Absolute file path = " + new File(attachment.getFilePath()).getAbsolutePath());

        VBox attachmentBox = new VBox();
        attachmentBox.setSpacing(12);
        attachmentBox.setPadding(new Insets(16));

        // Modern card-style background with shadow
        attachmentBox.setStyle(String.format(
                "-fx-background-color: %s; " +
                        "-fx-background-radius: 12px; " +
                        "-fx-border-color: %s; " +
                        "-fx-border-radius: 12px; " +
                        "-fx-border-width: 1px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);",
                BACKGROUND_COLOR, BORDER_COLOR
        ));

        // Header with file info
        HBox headerBox = createHeaderBox(attachment);
        attachmentBox.getChildren().add(headerBox);

        // Content preview based on file type
        Node contentPreview = createContentPreview(attachment, messageActions);
        if (contentPreview != null) {
            attachmentBox.getChildren().add(contentPreview);
        }

        // Action buttons
        HBox actionBox = createActionButtons(attachment, messageActions);
        attachmentBox.getChildren().add(actionBox);

        // Add hover effect
        addHoverEffect(attachmentBox);

        return attachmentBox;
    }

    private HBox createHeaderBox(AttachmentMessage attachment) {
        HBox headerBox = new HBox();
        headerBox.setSpacing(12);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label fileIcon = new Label(attachment.getFileIcon());
        fileIcon.setStyle(String.format(
                "-fx-font-size: 32px; " +
                        "-fx-text-fill: %s; " +
                        "-fx-background-color: %s; " +
                        "-fx-background-radius: 8px; " +
                        "-fx-padding: 8px; " +
                        "-fx-min-width: 48px; " +
                        "-fx-min-height: 48px; " +
                        "-fx-alignment: center;",
                PRIMARY_COLOR, HOVER_COLOR
        ));

        // File details
        VBox fileDetails = new VBox();
        fileDetails.setSpacing(4);
        HBox.setHgrow(fileDetails, Priority.ALWAYS);

        Label fileName = new Label(attachment.getFileName());
        fileName.setStyle(String.format(
                "-fx-font-weight: 600; " +
                        "-fx-font-size: 16px; " +
                        "-fx-text-fill: %s;",
                TEXT_PRIMARY
        ));
        fileName.setMaxWidth(Double.MAX_VALUE);

        Label fileInfo = new Label(attachment.getFileType() + " • " + attachment.getFormattedSize());
        fileInfo.setStyle(String.format(
                "-fx-text-fill: %s; " +
                        "-fx-font-size: 13px;",
                TEXT_SECONDARY
        ));

        // File status indicator
        Label statusLabel = new Label("Ready");
        statusLabel.setStyle(String.format(
                "-fx-text-fill: %s; " +
                        "-fx-font-size: 11px; " +
                        "-fx-background-color: %s; " +
                        "-fx-background-radius: 10px; " +
                        "-fx-padding: 2px 8px;",
                SUCCESS_COLOR, "#d4edda"
        ));

        fileDetails.getChildren().addAll(fileName, fileInfo, statusLabel);
        headerBox.getChildren().addAll(fileIcon, fileDetails);

        return headerBox;
    }

    private VBox createVideoPlayer(AttachmentMessage attachment) {
        File videoFile = new File(attachment.getFilePath());
        if (!videoFile.exists()) {
            System.err.println("Video file not found: " + videoFile.getAbsolutePath());
            return createErrorMessage();
        }

        VBox videoContainer = new VBox();
        videoContainer.setSpacing(12);
        videoContainer.setPadding(new Insets(16));
        videoContainer.setStyle(String.format(
                "-fx-background-color: %s; " +
                        "-fx-background-radius: 8px; " +
                        "-fx-border-color: %s; " +
                        "-fx-border-radius: 8px; " +
                        "-fx-border-width: 1px;",
                HOVER_COLOR, BORDER_COLOR
        ));

        try {
            // Create VLC video player
            final VideoPlayer player = new VideoPlayer();

            // Get video display container
            StackPane videoDisplay = player.getVideoContainer();
            videoDisplay.setPrefSize(400, 300);
            videoDisplay.setStyle(
                    "-fx-background-color: black; " +
                            "-fx-background-radius: 6px;"
            );

            // Create control panel
            VBox controlsPanel = createVideoControls(player, attachment);

            // Setup video player callbacks
            setupVideoPlayerCallbacks(player, controlsPanel);

            // Set video size
            player.setSize(400, 300);

            videoContainer.getChildren().addAll(videoDisplay, controlsPanel);

            // Store player reference for cleanup
            videoContainer.setUserData(player);

            return videoContainer;

        } catch (Exception e) {
            System.err.println("Failed to create VLC video player: " + e.getMessage());
            e.printStackTrace();
            return createVideoPlayerFallback(attachment);
        }
    }

    private void setupVideoPlayerCallbacks(VideoPlayer player, VBox controlsPanel) {
        // Get controls from the panel
        HBox mainControls = (HBox) controlsPanel.getChildren().get(0);
        Button playPauseBtn = (Button) mainControls.getChildren().get(0);
        Label currentTime = (Label) mainControls.getChildren().get(2);
        ProgressBar progressBar = (ProgressBar) mainControls.getChildren().get(3);
        Label totalTime = (Label) mainControls.getChildren().get(4);

        // Setup callbacks
        player.setOnProgressUpdate(progress -> {
            Platform.runLater(() -> progressBar.setProgress(progress));
        });

        player.setOnTimeUpdate(time -> {
            Platform.runLater(() -> currentTime.setText(time));
        });

        player.setOnTotalTimeUpdate(time -> {
            Platform.runLater(() -> totalTime.setText(time));
        });

        player.setOnPlaybackFinished(() -> {
            Platform.runLater(() -> {
                playPauseBtn.setGraphic(new FontIcon(FontAwesome.PLAY));
                progressBar.setProgress(0.0);
                currentTime.setText("0:00");
            });
        });
    }

    private VBox createVideoControls(VideoPlayer player, AttachmentMessage attachment) {
        VBox controlsPanel = new VBox();
        controlsPanel.setSpacing(8);

        // Main controls row
        HBox mainControls = new HBox();
        mainControls.setSpacing(12);
        mainControls.setAlignment(Pos.CENTER_LEFT);

        // Play/Pause button
        Button playPauseBtn = createModernButton("", FontAwesome.PLAY);
        playPauseBtn.setStyle(String.format(
                "-fx-background-color: %s; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 50%%; " +
                        "-fx-min-width: 48px; " +
                        "-fx-min-height: 48px; " +
                        "-fx-font-size: 16px;",
                PRIMARY_COLOR
        ));

        // Stop button
        Button stopBtn = createModernButton("", FontAwesome.STOP);
        stopBtn.setStyle(String.format(
                "-fx-background-color: %s; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 6px; " +
                        "-fx-min-width: 36px; " +
                        "-fx-min-height: 36px; " +
                        "-fx-font-size: 14px;",
                SECONDARY_COLOR
        ));

        // Progress bar
        ProgressBar progressBar = new ProgressBar();
        progressBar.setProgress(0.0);
        progressBar.setPrefWidth(200);
        progressBar.setStyle(String.format("-fx-accent: %s;", PRIMARY_COLOR));
        HBox.setHgrow(progressBar, Priority.ALWAYS);

        // Time labels
        Label currentTime = new Label("0:00");
        Label totalTime = new Label("0:00");
        currentTime.setStyle(String.format("-fx-text-fill: %s; -fx-font-size: 12px;", TEXT_SECONDARY));
        totalTime.setStyle(String.format("-fx-text-fill: %s; -fx-font-size: 12px;", TEXT_SECONDARY));

        mainControls.getChildren().addAll(playPauseBtn, stopBtn, currentTime, progressBar, totalTime);

        // Volume controls row
        HBox volumeControls = new HBox();
        volumeControls.setSpacing(8);
        volumeControls.setAlignment(Pos.CENTER_LEFT);

        FontIcon volumeIcon = new FontIcon(FontAwesome.VOLUME_UP);
        volumeIcon.setIconColor(Color.web(TEXT_SECONDARY));

        Slider volumeSlider = new Slider(0, 100, 50);
        volumeSlider.setPrefWidth(120);
        volumeSlider.setStyle(String.format("-fx-accent: %s;", PRIMARY_COLOR));

        volumeControls.getChildren().addAll(volumeIcon, volumeSlider);

        controlsPanel.getChildren().addAll(mainControls, volumeControls);

        // Setup button actions
        playPauseBtn.setOnAction(e -> {
            FontIcon currentIcon = (FontIcon) playPauseBtn.getGraphic();
            if (currentIcon.getIconLiteral().equals(FontAwesome.PLAY.getDescription())) {
                player.play(attachment.getFilePath());
                playPauseBtn.setGraphic(new FontIcon(FontAwesome.PAUSE));
            } else {
                if (player.isPlaying()) {
                    player.pause();
                    playPauseBtn.setGraphic(new FontIcon(FontAwesome.PLAY));
                } else if (player.isPaused()) {
                    player.resume();
                    playPauseBtn.setGraphic(new FontIcon(FontAwesome.PAUSE));
                }
            }
        });

        stopBtn.setOnAction(e -> {
            player.stop();
            playPauseBtn.setGraphic(new FontIcon(FontAwesome.PLAY));
        });

        // Progress bar click for seeking
        progressBar.setOnMouseClicked(e -> {
            if (progressBar.getWidth() > 0) {
                double clickPosition = e.getX() / progressBar.getWidth();
                player.seek(clickPosition);
            }
        });

        // Volume control
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            float volume = newVal.floatValue() / 100.0f;
            player.setVolume(volume);

            // Update volume icon
            if (volume == 0) {
                volumeIcon.setIconLiteral(FontAwesome.VOLUME_OFF.getDescription());
            } else if (volume < 0.5) {
                volumeIcon.setIconLiteral(FontAwesome.VOLUME_DOWN.getDescription());
            } else {
                volumeIcon.setIconLiteral(FontAwesome.VOLUME_UP.getDescription());
            }
        });

        return controlsPanel;
    }


    private Node createContentPreview(AttachmentMessage attachment, MessageActions messageActions) {
        if (attachment.isImage()) {
            return createImagePreview(attachment, messageActions);
        } else if ("Audio".equalsIgnoreCase(attachment.getFileType())) {
            return createModernAudioPlayer(attachment);
        }
        else if ("Video".equalsIgnoreCase(attachment.getFileType())) {
            return createVideoPlayer(attachment);
        }

        return null;
    }

    public void cleanup(VBox attachmentBox) {
        if (attachmentBox != null && attachmentBox.getUserData() instanceof AudioPlayer player) {
            player.cleanup();
        }
    }
}