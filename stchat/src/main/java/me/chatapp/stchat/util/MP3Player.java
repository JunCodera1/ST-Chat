package me.chatapp.stchat.util;

import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.application.Platform;
import javafx.util.Duration;
import javazoom.jl.player.Player;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import javax.sound.sampled.*;
import java.io.*;
import java.util.function.Consumer;

public class MP3Player {
    private AdvancedPlayer player;
    private Thread playThread;
    private Timeline progressTimeline;
    private boolean isPlaying = false;
    private boolean isPaused = false;
    private long totalFrames = 0;
    private long currentFrame = 0;
    private String currentFilePath;
    private float volume = 0.5f;

    // Callbacks
    private Consumer<Double> onProgressUpdate;
    private Consumer<String> onTimeUpdate;
    private Consumer<String> onTotalTimeUpdate;
    private Runnable onPlaybackFinished;

    public MP3Player() {
        setupProgressTimeline();
    }

    private void setupProgressTimeline() {
        progressTimeline = new Timeline(new KeyFrame(Duration.millis(100), e -> {
            if (isPlaying && !isPaused) {
                updateProgress();
            }
        }));
        progressTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    public void play(String filePath) {
        if (isPlaying && currentFilePath != null && currentFilePath.equals(filePath)) {
            // Resume if paused
            if (isPaused) {
                resume();
            }
            return;
        }

        stop();
        currentFilePath = filePath;

        playThread = new Thread(() -> {
            try {
                // Calculate total frames for progress
                calculateTotalFrames(filePath);

                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filePath));
                player = new AdvancedPlayer(bis);

                player.setPlayBackListener(new PlaybackListener() {
                    @Override
                    public void playbackStarted(PlaybackEvent evt) {
                        isPlaying = true;
                        isPaused = false;
                        Platform.runLater(() -> {
                            progressTimeline.play();
                            if (onTotalTimeUpdate != null) {
                                onTotalTimeUpdate.accept(formatTime(totalFrames));
                            }
                        });
                    }

                    @Override
                    public void playbackFinished(PlaybackEvent evt) {
                        isPlaying = false;
                        isPaused = false;
                        currentFrame = 0;
                        Platform.runLater(() -> {
                            progressTimeline.stop();
                            if (onProgressUpdate != null) {
                                onProgressUpdate.accept(0.0);
                            }
                            if (onTimeUpdate != null) {
                                onTimeUpdate.accept("0:00");
                            }
                            if (onPlaybackFinished != null) {
                                onPlaybackFinished.run();
                            }
                        });
                    }
                });

                // Apply volume by creating a volume-controlled audio stream
                player.play();

            } catch (Exception e) {
                e.printStackTrace();
                isPlaying = false;
                isPaused = false;
            }
        });

        playThread.start();
    }

    public void pause() {
        if (isPlaying && !isPaused) {
            isPaused = true;
            if (player != null) {
                player.close();
            }
            progressTimeline.pause();
        }
    }

    public void resume() {
        if (isPaused && currentFilePath != null) {
            isPaused = false;

            playThread = new Thread(() -> {
                try {
                    // Resume from current position
                    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(currentFilePath));
                    player = new AdvancedPlayer(bis);

                    // Skip to current position
                    if (currentFrame > 0) {
                        player.play((int) currentFrame, Integer.MAX_VALUE);
                    } else {
                        player.play();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            playThread.start();
            progressTimeline.play();
        }
    }

    public void stop() {
        isPlaying = false;
        isPaused = false;
        currentFrame = 0;

        if (progressTimeline != null) {
            progressTimeline.stop();
        }

        if (player != null) {
            player.close();
            player = null;
        }

        if (playThread != null && playThread.isAlive()) {
            playThread.interrupt();
        }

        Platform.runLater(() -> {
            if (onProgressUpdate != null) {
                onProgressUpdate.accept(0.0);
            }
            if (onTimeUpdate != null) {
                onTimeUpdate.accept("0:00");
            }
        });
    }

    public void setVolume(float volume) {
        this.volume = Math.max(0.0f, Math.min(1.0f, volume));
        // Note: JLayer doesn't support runtime volume control
        // You would need to restart playback for volume changes
    }

    public void seek(double position) {
        if (totalFrames > 0) {
            currentFrame = (long) (position * totalFrames);
            if (isPlaying) {
                // Restart from new position
                String filePath = currentFilePath;
                stop();
                play(filePath);
            }
        }
    }

    private void calculateTotalFrames(String filePath) {
        try {
            // Estimate total frames - this is approximate
            File file = new File(filePath);
            long fileSize = file.length();

            // Rough estimation for MP3: 128kbps = ~4 minutes per MB
            // This is very approximate - for accurate duration, you'd need to parse MP3 headers
            totalFrames = (fileSize / 4000) * 60; // Very rough estimation

        } catch (Exception e) {
            totalFrames = 0;
        }
    }

    private void updateProgress() {
        if (totalFrames > 0) {
            currentFrame++;
            double progress = (double) currentFrame / totalFrames;

            Platform.runLater(() -> {
                if (onProgressUpdate != null) {
                    onProgressUpdate.accept(Math.min(progress, 1.0));
                }
                if (onTimeUpdate != null) {
                    onTimeUpdate.accept(formatTime(currentFrame));
                }
            });
        }
    }

    private String formatTime(long frames) {
        // Convert frames to seconds (approximate)
        int seconds = (int) (frames / 100); // Assuming 100 frames per second
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    // Setters for callbacks
    public void setOnProgressUpdate(Consumer<Double> callback) {
        this.onProgressUpdate = callback;
    }

    public void setOnTimeUpdate(Consumer<String> callback) {
        this.onTimeUpdate = callback;
    }

    public void setOnTotalTimeUpdate(Consumer<String> callback) {
        this.onTotalTimeUpdate = callback;
    }

    public void setOnPlaybackFinished(Runnable callback) {
        this.onPlaybackFinished = callback;
    }

    public boolean isPlaying() {
        return isPlaying && !isPaused;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public float getVolume() {
        return volume;
    }
}