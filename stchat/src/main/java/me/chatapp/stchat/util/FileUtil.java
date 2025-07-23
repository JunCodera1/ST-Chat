package me.chatapp.stchat.util;

import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import me.chatapp.stchat.model.AttachmentMessage;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.IOException;

import static me.chatapp.stchat.util.CSSUtil.*;
import static me.chatapp.stchat.util.CSSUtil.PRIMARY_COLOR;
import static me.chatapp.stchat.util.CSSUtil.SECONDARY_COLOR;
import static me.chatapp.stchat.util.CSSUtil.TEXT_SECONDARY;
import static me.chatapp.stchat.util.DisplayUtil.createModernButton;

public class FileUtil {
    public static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }
    public static String getFileType(String extension) {
        return switch (extension) {
            case "png", "jpg", "jpeg", "gif", "bmp" -> "Image";
            case "pdf" -> "PDF";
            case "doc", "docx" -> "Document";
            case "txt" -> "Text";
            case "mp4", "avi", "mkv", "mov" -> "Video";
            case "mp3", "wav", "ogg" -> "Audio";
            default -> "File";
        };
    }
    public static void setupFileFilters(FileChooser fileChooser) {
        FileChooser.ExtensionFilter allFiles = new FileChooser.ExtensionFilter("All Files", "*.*");
        FileChooser.ExtensionFilter imageFiles = new FileChooser.ExtensionFilter("Images",
                "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp", "*.svg", "*.webp");
        FileChooser.ExtensionFilter documentFiles = new FileChooser.ExtensionFilter("Documents",
                "*.pdf", "*.doc", "*.docx", "*.txt", "*.rtf", "*.odt", "*.xls", "*.xlsx");
        FileChooser.ExtensionFilter videoFiles = new FileChooser.ExtensionFilter("Videos",
                "*.mp4", "*.avi", "*.mkv", "*.mov", "*.wmv", "*.flv", "*.webm");
        FileChooser.ExtensionFilter audioFiles = new FileChooser.ExtensionFilter("Audio",
                "*.mp3", "*.wav", "*.ogg", "*.m4a", "*.flac", "*.aac");

        fileChooser.getExtensionFilters().addAll(
                allFiles, imageFiles, documentFiles, videoFiles, audioFiles
        );

        fileChooser.setSelectedExtensionFilter(allFiles);
    }

    public static File convertMp3ToWav(File mp3File) throws IOException, InterruptedException {
        String wavFilePath = mp3File.getAbsolutePath().replace(".mp3", ".wav");
        File wavFile = new File(wavFilePath);

        ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-y", "-i", mp3File.getAbsolutePath(), wavFile.getAbsolutePath());
        pb.redirectErrorStream(true);
        Process process = pb.start();

        int exitCode = process.waitFor();
        if (exitCode != 0 || !wavFile.exists()) {
            throw new IOException("Chuyển mp3 sang wav thất bại");
        }

        return wavFile;
    }
    public static boolean isSupportedVideoFormat(String fileName) {
        String extension = fileName.toLowerCase();
        return extension.endsWith(".mp4") ||
                extension.endsWith(".m4v") ||
                extension.endsWith(".flv") ||
                extension.endsWith(".mov");  // Added .mov as it's often supported
    }

    public static String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return (bytes / 1024) + " KB";
        if (bytes < 1024 * 1024 * 1024) return (bytes / (1024 * 1024)) + " MB";
        return (bytes / (1024 * 1024 * 1024)) + " GB";
    }

    public static ImageView createImagePreview(AttachmentMessage attachment, MessageActions messageActions){
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

    public static VBox createModernAudioPlayer(AttachmentMessage attachment) {
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
        final AudioPlayer player = new AudioPlayer();

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

        audioContainer.setUserData(player);

        return audioContainer;
    }
}

