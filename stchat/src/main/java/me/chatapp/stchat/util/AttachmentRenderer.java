package me.chatapp.stchat.util;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import me.chatapp.stchat.model.AttachmentMessage;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;

public class AttachmentRenderer {

    // Modern color palette
    private static final String PRIMARY_COLOR = "#007bff";
    private static final String SECONDARY_COLOR = "#6c757d";
    private static final String SUCCESS_COLOR = "#28a745";
    private static final String BACKGROUND_COLOR = "#ffffff";
    private static final String BORDER_COLOR = "#e9ecef";
    private static final String HOVER_COLOR = "#f8f9fa";
    private static final String TEXT_PRIMARY = "#212529";
    private static final String TEXT_SECONDARY = "#6c757d";

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

        // File icon with modern styling
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

    private Node createContentPreview(AttachmentMessage attachment, MessageActions messageActions) {
        if (attachment.isImage()) {
            return createImagePreview(attachment, messageActions);
        } else if ("Audio".equalsIgnoreCase(attachment.getFileType())) {
            return createModernAudioPlayer(attachment);
        }
        return null;
    }

    public void cleanup(VBox attachmentBox) {
        if (attachmentBox != null && attachmentBox.getUserData() instanceof VLCJPlayer player) {
            player.cleanup();
        }
    }

    private VBox createModernAudioPlayer(AttachmentMessage attachment) {
        File audioFile = new File(attachment.getFilePath());
        if (!audioFile.exists()) {
            System.err.println("Audio file not found: " + audioFile.getAbsolutePath());
            return null;
        }

        VBox audioContainer = new VBox();
        audioContainer.setSpacing(12);
        audioContainer.setPadding(new Insets(16));
        audioContainer.setStyle(String.format(
                "-fx-background-color: %s; " +
                        "-fx-background-radius: 8px; " +
                        "-fx-border-color: %s; " +
                        "-fx-border-radius: 8px; " +
                        "-fx-border-width: 1px;",
                HOVER_COLOR, BORDER_COLOR
        ));

        // Audio controls
        HBox controlsBox = new HBox();
        controlsBox.setSpacing(12);
        controlsBox.setAlignment(Pos.CENTER_LEFT);

        // Play/Pause button with modern styling
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

        // Progress bar
        ProgressBar progressBar = new ProgressBar();
        progressBar.setProgress(0.0);
        progressBar.setPrefWidth(200);
        progressBar.setStyle(String.format(
                "-fx-accent: %s;",
                PRIMARY_COLOR
        ));
        HBox.setHgrow(progressBar, Priority.ALWAYS);

        // Initialize VLCJPlayer
        final VLCJPlayer player = new VLCJPlayer();

        // Make progress bar clickable for seeking
        progressBar.setOnMouseClicked(e -> {
            if (progressBar.getWidth() > 0) {
                double clickPosition = e.getX() / progressBar.getWidth();
                player.seek(clickPosition);
            }
        });

        // Time labels
        Label currentTime = new Label("0:00");
        Label totalTime = new Label("0:00");
        currentTime.setStyle(String.format("-fx-text-fill: %s; -fx-font-size: 12px;", TEXT_SECONDARY));
        totalTime.setStyle(String.format("-fx-text-fill: %s; -fx-font-size: 12px;", TEXT_SECONDARY));

        // Volume control
        Slider volumeSlider = new Slider(0, 100, 50);
        volumeSlider.setPrefWidth(80);
        volumeSlider.setStyle(String.format("-fx-accent: %s;", PRIMARY_COLOR));

        FontIcon volumeIcon = new FontIcon(FontAwesome.VOLUME_UP);
        volumeIcon.setIconColor(Color.web(TEXT_SECONDARY));

        // Setup callbacks for VLCJPlayer
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

        // Play/Pause button action
        playPauseBtn.setOnAction(e -> {
            FontIcon currentIcon = (FontIcon) playPauseBtn.getGraphic();
            if (currentIcon.getIconLiteral().equals(FontAwesome.PLAY.getDescription())) {
                player.play(audioFile.getAbsolutePath());
                playPauseBtn.setGraphic(new FontIcon(FontAwesome.PAUSE));
            } else {
                if (player.isPlaying()) {
                    player.pause();
                    playPauseBtn.setGraphic(new FontIcon(FontAwesome.PLAY));
                } else if (player.isPaused()) {
                    player.resume();
                    playPauseBtn.setGraphic(new FontIcon(FontAwesome.PAUSE));
                } else {
                    player.stop();
                    playPauseBtn.setGraphic(new FontIcon(FontAwesome.PLAY));
                }
            }
        });

        // Volume control
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            float volume = newVal.floatValue() / 100.0f;
            player.setVolume(volume);

            // Update volume icon based on level
            if (volume == 0) {
                volumeIcon.setIconLiteral(FontAwesome.VOLUME_OFF.getDescription());
            } else if (volume < 0.5) {
                volumeIcon.setIconLiteral(FontAwesome.VOLUME_DOWN.getDescription());
            } else {
                volumeIcon.setIconLiteral(FontAwesome.VOLUME_UP.getDescription());
            }
        });

        // Additional control buttons
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

        stopBtn.setOnAction(e -> {
            player.stop();
            playPauseBtn.setGraphic(new FontIcon(FontAwesome.PLAY));
        });

        controlsBox.getChildren().addAll(playPauseBtn, stopBtn, currentTime, progressBar, totalTime);

        HBox volumeBox = new HBox(8);
        volumeBox.setAlignment(Pos.CENTER_LEFT);
        volumeBox.getChildren().addAll(volumeIcon, volumeSlider);

        audioContainer.getChildren().addAll(controlsBox, volumeBox);

        // Store player reference for cleanup
        audioContainer.setUserData(player);

        return audioContainer;
    }

    private ImageView createImagePreview(AttachmentMessage attachment, MessageActions messageActions){
        try {
            File imageFile = new File(attachment.getFilePath());
            if (imageFile.exists()) {
                Image image = new Image("file:" + imageFile.getAbsolutePath());

                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(350);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                imageView.setStyle(
                        "-fx-background-radius: 8px; " +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 6, 0, 0, 2); " +
                                "-fx-cursor: hand;"
                );

                // Add click effect with null check
                imageView.setOnMouseClicked(e -> {
                    try {
                        if (messageActions != null) {
                            messageActions.showFullSizeImage(attachment);
                        } else {
                            new Thread(() -> {
                                try {
                                    java.awt.Desktop.getDesktop().open(imageFile);
                                } catch (Exception desktopEx) {
                                    System.err.println("Cannot open image: " + desktopEx.getMessage());
                                }
                            }).start();

                        }
                    } catch (Exception ex) {
                        System.err.println("Error showing full size image: " + ex.getMessage());
                    }
                });


                // Add hover effect for image with null check
                imageView.setOnMouseEntered(e -> {
                    try {
                        ScaleTransition scale = new ScaleTransition(Duration.millis(200), imageView);
                        scale.setToX(1.05);
                        scale.setToY(1.05);
                        scale.play();
                    } catch (Exception ex) {
                        System.err.println("Error in hover effect: " + ex.getMessage());
                    }
                });

                imageView.setOnMouseExited(e -> {
                    try {
                        ScaleTransition scale = new ScaleTransition(Duration.millis(200), imageView);
                        scale.setToX(1.0);
                        scale.setToY(1.0);
                        scale.play();
                    } catch (Exception ex) {
                        System.err.println("Error in hover effect: " + ex.getMessage());
                    }
                });

                return imageView;
            }
        } catch (Exception e) {
            System.err.println("Error creating image preview: " + e.getMessage());
        }
        return null;
    }

    private HBox createActionButtons(AttachmentMessage attachment, MessageActions messageActions) {
        HBox actionBox = new HBox();
        actionBox.setSpacing(12);
        actionBox.setAlignment(Pos.CENTER_LEFT);
        actionBox.setPadding(new Insets(8, 0, 0, 0));

        Button downloadBtn = createModernButton("Download", FontAwesome.DOWNLOAD);
        downloadBtn.setOnAction(e -> {
            try {
                if (messageActions != null) {
                    messageActions.handleDownload(attachment);
                } else {
                    System.err.println("MessageActions is null");
                }
            } catch (Exception ex) {
                System.err.println("Error downloading file: " + ex.getMessage());
            }
        });

        Button openBtn = createModernButton("Open", FontAwesome.EXTERNAL_LINK);
        openBtn.setOnAction(e -> {
            try {
                if (messageActions != null) {
                    messageActions.handleOpenFile(attachment);
                } else {
                    // Fallback: Open with system default application
                    try {
                        java.awt.Desktop.getDesktop().open(new File(attachment.getFilePath()));
                    } catch (Exception desktopEx) {
                        System.err.println("Cannot open file: " + desktopEx.getMessage());
                    }
                }
            } catch (Exception ex) {
                System.err.println("Error opening file: " + ex.getMessage());
            }
        });

        // Add share button
        Button shareBtn = createModernButton("Share", FontAwesome.SHARE);
        shareBtn.setOnAction(e -> {
            try {
                // Implement share functionality safely
                System.out.println("Share file: " + attachment.getFileName());
                // You can add actual share logic here
            } catch (Exception ex) {
                System.err.println("Error sharing file: " + ex.getMessage());
            }
        });

        actionBox.getChildren().addAll(downloadBtn, openBtn, shareBtn);
        return actionBox;
    }

    private Button createModernButton(String text, FontAwesome icon) {
        Button button = new Button(text);
        button.setGraphic(new FontIcon(icon));
        button.setStyle(String.format(
                "-fx-background-color: transparent; " +
                        "-fx-text-fill: %s; " +
                        "-fx-border-color: %s; " +
                        "-fx-border-radius: 6px; " +
                        "-fx-background-radius: 6px; " +
                        "-fx-padding: 8px 16px; " +
                        "-fx-font-size: 13px; " +
                        "-fx-font-weight: 500; " +
                        "-fx-cursor: hand;",
                PRIMARY_COLOR, BORDER_COLOR
        ));

        // Add hover effect
        button.setOnMouseEntered(e -> {
            button.setStyle(String.format(
                    "-fx-background-color: %s; " +
                            "-fx-text-fill: %s; " +
                            "-fx-border-color: %s; " +
                            "-fx-border-radius: 6px; " +
                            "-fx-background-radius: 6px; " +
                            "-fx-padding: 8px 16px; " +
                            "-fx-font-size: 13px; " +
                            "-fx-font-weight: 500; " +
                            "-fx-cursor: hand;",
                    HOVER_COLOR, PRIMARY_COLOR, PRIMARY_COLOR
            ));
        });

        button.setOnMouseExited(e -> {
            button.setStyle(String.format(
                    "-fx-background-color: transparent; " +
                            "-fx-text-fill: %s; " +
                            "-fx-border-color: %s; " +
                            "-fx-border-radius: 6px; " +
                            "-fx-background-radius: 6px; " +
                            "-fx-padding: 8px 16px; " +
                            "-fx-font-size: 13px; " +
                            "-fx-font-weight: 500; " +
                            "-fx-cursor: hand;",
                    PRIMARY_COLOR, BORDER_COLOR
            ));
        });

        // Add tooltip
        Tooltip tooltip = new Tooltip(text);
        tooltip.setStyle("-fx-font-size: 12px;");
        button.setTooltip(tooltip);

        return button;
    }

    private void addHoverEffect(VBox attachmentBox) {
        attachmentBox.setOnMouseEntered(e -> {
            attachmentBox.setStyle(String.format(
                    "-fx-background-color: %s; " +
                            "-fx-background-radius: 12px; " +
                            "-fx-border-color: %s; " +
                            "-fx-border-radius: 12px; " +
                            "-fx-border-width: 1px; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 12, 0, 0, 4);",
                    BACKGROUND_COLOR, PRIMARY_COLOR
            ));
        });

        attachmentBox.setOnMouseExited(e -> {
            attachmentBox.setStyle(String.format(
                    "-fx-background-color: %s; " +
                            "-fx-background-radius: 12px; " +
                            "-fx-border-color: %s; " +
                            "-fx-border-radius: 12px; " +
                            "-fx-border-width: 1px; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);",
                    BACKGROUND_COLOR, BORDER_COLOR
            ));
        });
    }
}