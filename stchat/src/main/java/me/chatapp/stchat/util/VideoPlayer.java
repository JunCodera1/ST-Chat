// Alternative VideoPlayer với nhiều options khắc phục GPU issues
package me.chatapp.stchat.util;

import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.scene.layout.StackPane;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class VideoPlayer {
    // Multiple VLC configurations to try
    private static final String[][] VLC_OPTIONS = {
            // Option 1: Đề xuất chính - Dùng GL, tắt tăng tốc phần cứng hoàn toàn
            {
                    "--no-video-title-show",
                    "--quiet",
                    "--intf=dummy",
                    "--no-osd",
                    "--avcodec-hw=none",          // Tắt hardware acceleration
                    "--no-sub-autodetect-file",
                    "--no-xlib",                  // Tránh lỗi GTK/GDK
                    "--vout=gl",                  // Dùng OpenGL
            },

            // Option 2: Dành cho môi trường X11 - fallback an toàn
            {
                    "--no-video-title-show",
                    "--quiet",
                    "--intf=dummy",
                    "--no-osd",
                    "--no-sub-autodetect-file",
                    "--vout=xcb_x11",             // Dành cho backend X11
                    "--avcodec-hw=none",
                    "--no-xlib"
            },

            // Option 3: Đơn giản - fallback tối thiểu
            {
                    "--quiet",
                    "--intf=dummy",
                    "--no-osd",
                    "--avcodec-hw=none",
                    "--no-xlib"
            },

            // Option 4: Minimal, chỉ disable interface và output logs
            {
                    "--quiet",
                    "--intf=dummy"
            }
    };


    private MediaPlayerFactory mediaPlayerFactory;
    private EmbeddedMediaPlayer mediaPlayer;
    private Canvas videoCanvas;
    private SwingNode videoNode;
    private StackPane videoContainer;

    // Callbacks
    private Consumer<Float> onProgressUpdate;
    private Consumer<String> onTimeUpdate;
    private Consumer<String> onTotalTimeUpdate;
    private Runnable onPlaybackFinished;
    private Runnable onVideoReady;

    private boolean isInitialized = false;
    private String pendingVideoPath = null;
    private int currentOptionIndex = 0;

    public VideoPlayer() {
        initializePlayerWithRetry();
    }

    private void initializePlayerWithRetry() {
        for (int i = 0; i < VLC_OPTIONS.length; i++) {
            currentOptionIndex = i;
            System.out.println("Trying VLC option set " + (i + 1) + "...");

            if (initializePlayerWithOptions(VLC_OPTIONS[i])) {
                System.out.println("VLC initialized successfully with option set " + (i + 1));
                return;
            }

            // Clean up failed attempt
            cleanup();
        }

        System.err.println("Failed to initialize VLC with all option sets");
        isInitialized = false;
    }

    private boolean initializePlayerWithOptions(String[] options) {
        try {
            // Create media player factory with specific options
            mediaPlayerFactory = new MediaPlayerFactory(options);

            // Create embedded media player
            mediaPlayer = mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer();

            // Create video canvas with error handling
            videoCanvas = new Canvas();
            videoCanvas.setBackground(Color.BLACK);

            // Set video surface with error handling
            try {
                mediaPlayer.videoSurface().set(mediaPlayerFactory.videoSurfaces().newVideoSurface(videoCanvas));
            } catch (Exception e) {
                System.err.println("Failed to set video surface: " + e.getMessage());
                return false;
            }

            // Create SwingNode to embed canvas in JavaFX
            videoNode = new SwingNode();
            SwingUtilities.invokeLater(() -> {
                try {
                    JPanel panel = new JPanel(new BorderLayout());
                    panel.add(videoCanvas, BorderLayout.CENTER);
                    panel.setBackground(Color.BLACK);
                    videoNode.setContent(panel);
                } catch (Exception e) {
                    System.err.println("Failed to create video panel: " + e.getMessage());
                }
            });

            // Create container
            videoContainer = new StackPane(videoNode);

            // Add event listeners
            setupEventListeners();

            isInitialized = true;

            // If there was a pending video, play it now
            if (pendingVideoPath != null) {
                play(pendingVideoPath);
                pendingVideoPath = null;
            }

            return true;

        } catch (Exception e) {
            System.err.println("Failed to initialize VLC with current options: " + e.getMessage());
            return false;
        }
    }

    private void setupEventListeners() {
        mediaPlayer.events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            @Override
            public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
                if (onTimeUpdate != null) {
                    String timeStr = formatTime(newTime);
                    Platform.runLater(() -> onTimeUpdate.accept(timeStr));
                }

                if (onProgressUpdate != null) {
                    long totalTime = mediaPlayer.status().length();
                    if (totalTime > 0) {
                        float progress = (float) newTime / totalTime;
                        Platform.runLater(() -> onProgressUpdate.accept(progress));
                    }
                }
            }

            @Override
            public void lengthChanged(MediaPlayer mediaPlayer, long newLength) {
                if (onTotalTimeUpdate != null) {
                    String timeStr = formatTime(newLength);
                    Platform.runLater(() -> onTotalTimeUpdate.accept(timeStr));
                }
            }

            @Override
            public void finished(MediaPlayer mediaPlayer) {
                if (onPlaybackFinished != null) {
                    Platform.runLater(() -> onPlaybackFinished.run());
                }
            }

            @Override
            public void mediaPlayerReady(MediaPlayer mediaPlayer) {
                if (onVideoReady != null) {
                    Platform.runLater(() -> onVideoReady.run());
                }
            }

            @Override
            public void error(MediaPlayer mediaPlayer) {
                Platform.runLater(() -> {
                    System.err.println("VLC Media Player Error - trying next option set...");

                    // Try next option set if available
                    if (currentOptionIndex < VLC_OPTIONS.length - 1) {
                        cleanup();
                        initializePlayerWithRetry();
                    } else if (onPlaybackFinished != null) {
                        onPlaybackFinished.run();
                    }
                });
            }
        });
    }

    public void play(String videoPath) {
        if (!isInitialized) {
            pendingVideoPath = videoPath;
            return;
        }

        try {
            if (mediaPlayer != null) {
                mediaPlayer.media().play(videoPath);
            }
        } catch (Exception e) {
            System.err.println("Error playing video: " + e.getMessage());
            // Try to reinitialize with next option set
            if (currentOptionIndex < VLC_OPTIONS.length - 1) {
                cleanup();
                initializePlayerWithRetry();
                if (isInitialized) {
                    mediaPlayer.media().play(videoPath);
                }
            }
        }
    }

    public void pause() {
        if (mediaPlayer != null && isInitialized) {
            try {
                mediaPlayer.controls().pause();
            } catch (Exception e) {
                System.err.println("Error pausing video: " + e.getMessage());
            }
        }
    }

    public void resume() {
        if (mediaPlayer != null && isInitialized) {
            try {
                mediaPlayer.controls().play();
            } catch (Exception e) {
                System.err.println("Error resuming video: " + e.getMessage());
            }
        }
    }

    public void stop() {
        if (mediaPlayer != null && isInitialized) {
            try {
                mediaPlayer.controls().stop();
            } catch (Exception e) {
                System.err.println("Error stopping video: " + e.getMessage());
            }
        }
    }

    public void seek(double position) {
        if (mediaPlayer != null && isInitialized) {
            try {
                long totalTime = mediaPlayer.status().length();
                if (totalTime > 0) {
                    long seekTime = (long) (totalTime * position);
                    mediaPlayer.controls().setTime(seekTime);
                }
            } catch (Exception e) {
                System.err.println("Error seeking video: " + e.getMessage());
            }
        }
    }

    public void setVolume(float volume) {
        if (mediaPlayer != null && isInitialized) {
            try {
                int vlcVolume = Math.round(volume * 100);
                mediaPlayer.audio().setVolume(vlcVolume);
            } catch (Exception e) {
                System.err.println("Error setting volume: " + e.getMessage());
            }
        }
    }

    public boolean isPlaying() {
        if (mediaPlayer != null && isInitialized) {
            try {
                return mediaPlayer.status().isPlaying();
            } catch (Exception e) {
                System.err.println("Error checking playing status: " + e.getMessage());
            }
        }
        return false;
    }

    public boolean isPaused() {
        if (mediaPlayer != null && isInitialized) {
            try {
                return mediaPlayer.status().state() == uk.co.caprica.vlcj.player.base.State.PAUSED;
            } catch (Exception e) {
                System.err.println("Error checking paused status: " + e.getMessage());
            }
        }
        return false;
    }

    public StackPane getVideoContainer() {
        return videoContainer;
    }

    public void setSize(double width, double height) {
        if (videoContainer != null) {
            Platform.runLater(() -> {
                videoContainer.setPrefSize(width, height);
                videoContainer.setMaxSize(width, height);
                videoContainer.setMinSize(width, height);
            });

            SwingUtilities.invokeLater(() -> {
                if (videoCanvas != null) {
                    videoCanvas.setSize((int) width, (int) height);
                    videoCanvas.setPreferredSize(new Dimension((int) width, (int) height));
                }
            });
        }
    }

    // Callback setters
    public void setOnProgressUpdate(Consumer<Float> callback) {
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

    public void setOnVideoReady(Runnable callback) {
        this.onVideoReady = callback;
    }

    private String formatTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    public void cleanup() {
        try {
            isInitialized = false;

            if (mediaPlayer != null) {
                mediaPlayer.controls().stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }

            if (mediaPlayerFactory != null) {
                mediaPlayerFactory.release();
                mediaPlayerFactory = null;
            }

            if (videoNode != null) {
                SwingUtilities.invokeLater(() -> {
                    videoNode.setContent(null);
                });
                videoNode = null;
            }

            videoCanvas = null;
            videoContainer = null;

        } catch (Exception e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
    }
}