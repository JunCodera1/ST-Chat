package me.chatapp.stchat.view.components.organisms;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StatusBar {
    private final HBox statusContainer;
    private final Label statusLabel;
    private final Label connectionLabel;
    private final Label timeLabel;
    private final ProgressIndicator progressIndicator;

    public StatusBar() {
        statusContainer = new HBox(15);
        statusContainer.setAlignment(Pos.CENTER_LEFT);
        statusContainer.setPadding(new Insets(8, 15, 8, 15));
        statusContainer.setStyle("""
            -fx-background-color: #f8f9fa;
            -fx-border-color: #dee2e6;
            -fx-border-width: 1 0 0 0;
            """);

        // Status indicator
        statusLabel = new Label("Ready");
        statusLabel.getStyleClass().add("status-disconnected");

        // Connection info
        connectionLabel = new Label("");
        connectionLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c757d;");

        // Progress indicator (hidden by default)
        progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxSize(16, 16);
        progressIndicator.setVisible(false);

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Time label
        timeLabel = new Label();
        timeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c757d;");
        updateTime();

        // Update time every second
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(
                        javafx.util.Duration.seconds(1),
                        e -> updateTime()
                )
        );
        timeline.setCycleCount(javafx.animation.Timeline.INDEFINITE);
        timeline.play();

        statusContainer.getChildren().addAll(
                statusLabel,
                connectionLabel,
                progressIndicator,
                spacer,
                timeLabel
        );
    }

    private void updateTime() {
        String currentTime = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("HH:mm:ss")
        );
        timeLabel.setText(currentTime);
    }

    public HBox getComponent() {
        return statusContainer;
    }

    public void setStatus(String status, boolean connected) {
        statusLabel.setText(status);
        if (connected) {
            statusLabel.getStyleClass().clear();
            statusLabel.getStyleClass().add("status-connected");
        } else {
            statusLabel.getStyleClass().clear();
            statusLabel.getStyleClass().add("status-disconnected");
        }
    }

    public void setConnectionInfo(String info) {
        connectionLabel.setText(info);
    }

    public void showProgress(boolean show) {
        progressIndicator.setVisible(show);
    }

    public void showConnecting() {
        setStatus("Connecting...", false);
        showProgress(true);
    }

    public void showConnected(String serverInfo) {
        setStatus("Connected", true);
        setConnectionInfo("Connected to " + serverInfo);
        showProgress(false);
    }

    public void showDisconnected() {
        setStatus("Disconnected", false);
        setConnectionInfo("");
        showProgress(false);
    }

    public void showError(String error) {
        setStatus("Error: " + error, false);
        setConnectionInfo("");
        showProgress(false);
    }

    // Getters
    public Label getStatusLabel() { return statusLabel; }
    public Label getConnectionLabel() { return connectionLabel; }
    public Label getTimeLabel() { return timeLabel; }
}