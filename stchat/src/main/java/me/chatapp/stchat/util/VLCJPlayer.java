package me.chatapp.stchat.util;

import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.application.Platform;
import javafx.util.Duration;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;

import java.util.function.Consumer;

public class VLCJPlayer {
    private MediaPlayerFactory factory;
    private MediaPlayer mediaPlayer;
    private Timeline progressTimeline;
    private boolean isPlaying = false;
    private boolean isPaused = false;
    private String currentFilePath;
    private long totalDuration = 0;

    // Callbacks
    private Consumer<Double> onProgressUpdate;
    private Consumer<String> onTimeUpdate;
    private Consumer<String> onTotalTimeUpdate;
    private Runnable onPlaybackFinished;

    public VLCJPlayer() {
        try {
            factory = new MediaPlayerFactory();
            mediaPlayer = factory.mediaPlayers().newMediaPlayer();
            setupEventListeners();
            setupProgressTimeline();
        } catch (Exception e) {
            System.err.println("Failed to initialize VLCJ: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void setupEventListeners() {
        mediaPlayer.events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            @Override
            public void playing(MediaPlayer mediaPlayer) {
                isPlaying = true;
                isPaused = false;
                totalDuration = mediaPlayer.status().length();

                Platform.runLater(() -> {
                    progressTimeline.play();
                    if (onTotalTimeUpdate != null) {
                        onTotalTimeUpdate.accept(formatTime(totalDuration));
                    }
                });
            }

            @Override
            public void paused(MediaPlayer mediaPlayer) {
                isPaused = true;
                Platform.runLater(() -> progressTimeline.pause());
            }

            @Override
            public void stopped(MediaPlayer mediaPlayer) {
                isPlaying = false;
                isPaused = false;
                Platform.runLater(() -> {
                    progressTimeline.stop();
                    if (onProgressUpdate != null) {
                        onProgressUpdate.accept(0.0);
                    }
                    if (onTimeUpdate != null) {
                        onTimeUpdate.accept("0:00");
                    }
                });
            }

            @Override
            public void finished(MediaPlayer mediaPlayer) {
                isPlaying = false;
                isPaused = false;
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
    }

    private void setupProgressTimeline() {
        progressTimeline = new Timeline(new KeyFrame(Duration.millis(100), e -> {
            if (isPlaying && !isPaused && totalDuration > 0) {
                updateProgress();
            }
        }));
        progressTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    public void play(String filePath) {
        if (mediaPlayer == null) return;

        try {
            currentFilePath = filePath;
            mediaPlayer.media().play(filePath);
        } catch (Exception e) {
            System.err.println("Error playing file: " + e.getMessage());
        }
    }

    public void pause() {
        if (mediaPlayer != null && isPlaying) {
            mediaPlayer.controls().pause();
        }
    }

    public void resume() {
        if (mediaPlayer != null && isPaused) {
            mediaPlayer.controls().play();
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.controls().stop();
        }
    }

    public void setVolume(float volume) {
        if (mediaPlayer != null) {
            // VLC volume range is 0-200 (100 = normal)
            int vlcVolume = (int) (volume * 200);
            mediaPlayer.audio().setVolume(vlcVolume);
        }
    }

    public void seek(double position) {
        if (mediaPlayer != null && totalDuration > 0) {
            long seekTime = (long) (position * totalDuration);
            mediaPlayer.controls().setTime(seekTime);
        }
    }

    private void updateProgress() {
        if (mediaPlayer != null && totalDuration > 0) {
            long currentTime = mediaPlayer.status().time();
            double progress = (double) currentTime / totalDuration;

            Platform.runLater(() -> {
                if (onProgressUpdate != null) {
                    onProgressUpdate.accept(Math.min(progress, 1.0));
                }
                if (onTimeUpdate != null) {
                    onTimeUpdate.accept(formatTime(currentTime));
                }
            });
        }
    }

    private String formatTime(long milliseconds) {
        int seconds = (int) (milliseconds / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    public void cleanup() {
        if (progressTimeline != null) {
            progressTimeline.stop();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        if (factory != null) {
            factory.release();
        }
    }

    // Getters và Setters
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
        return mediaPlayer != null ? mediaPlayer.audio().volume() / 100.0f : 0.5f;
    }
}