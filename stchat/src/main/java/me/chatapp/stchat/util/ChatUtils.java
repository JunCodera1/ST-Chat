package me.chatapp.stchat.util;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import me.chatapp.stchat.model.Message;

import java.io.File;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ChatUtils {
    public void addEmptyState(VBox messageContainer) {
        Label emptyStateLabel = new Label("No messages yet");
        emptyStateLabel.setStyle("-fx-text-fill: #9e9e9e; -fx-font-size: 14px;");
        emptyStateLabel.setAlignment(Pos.CENTER);
        VBox emptyStateContainer = new VBox(emptyStateLabel);
        emptyStateContainer.setAlignment(Pos.CENTER);
        emptyStateContainer.setPadding(new Insets(50));
        messageContainer.getChildren().add(emptyStateContainer);
    }

    public void clearMessages(VBox messageContainer) {
        Platform.runLater(() -> {
            messageContainer.getChildren().clear();
            addEmptyState(messageContainer);
        });
    }

    public void scrollToBottom(ScrollPane scrollPane, VBox messageContainer) {
        Platform.runLater(() -> {
            scrollPane.layout();
            double contentHeight = messageContainer.getBoundsInLocal().getHeight();
            double viewportHeight = scrollPane.getViewportBounds().getHeight();
            if (contentHeight > viewportHeight) {
                scrollPane.setVvalue(1.0);
            }
        });
    }

    public void showTypingIndicator(VBox messageContainer, String senderName, ScrollPane scrollPane) {
        Platform.runLater(() -> {
            removeTypingIndicator(messageContainer);
            HBox typingBox = new HBox();
            typingBox.setAlignment(Pos.CENTER_LEFT);
            typingBox.setSpacing(8);
            typingBox.setPadding(new Insets(8, 16, 8, 16));
            typingBox.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 12px; " +
                    "-fx-border-color: #dee2e6; -fx-border-width: 1px; -fx-border-radius: 12px;");

            Label typingLabel = new Label(senderName + " is typing...");
            typingLabel.setStyle("-fx-text-fill: #6c757d; -fx-font-style: italic;");

            Label dots = new Label("●●●");
            dots.setStyle("-fx-text-fill: #6c757d;");

            Timeline animation = new Timeline(
                    new KeyFrame(Duration.millis(500),
                            e -> dots.setText(dots.getText().equals("●●●") ? "   " : "●●●"))
            );
            animation.setCycleCount(Timeline.INDEFINITE);
            animation.play();

            typingBox.getChildren().addAll(typingLabel, dots);
            typingBox.setUserData("typing-indicator");
            messageContainer.getChildren().add(typingBox);
            scrollToBottom(scrollPane, messageContainer);
        });
    }

    public void hideTypingIndicator(VBox messageContainer) {
        Platform.runLater(() -> removeTypingIndicator(messageContainer));
    }

    private void removeTypingIndicator(VBox messageContainer) {
        messageContainer.getChildren().removeIf(node -> "typing-indicator".equals(node.getUserData()));
    }

    public void searchMessages(VBox messageContainer, String query) {
        if (query == null || query.trim().isEmpty()) {
            for (javafx.scene.Node node : messageContainer.getChildren()) {
                node.setVisible(true);
            }
            return;
        }

        String lowerQuery = query.toLowerCase().trim();
        for (javafx.scene.Node node : messageContainer.getChildren()) {
            if (node instanceof VBox vbox && vbox.getUserData() instanceof Message message) {
                boolean matches = message.getContent().toLowerCase().contains(lowerQuery) ||
                        message.getSender().toLowerCase().contains(lowerQuery) ||
                        (message.hasAttachment() &&
                                message.getAttachment().getFileName().toLowerCase().contains(lowerQuery));
                node.setVisible(matches);
            }
        }
    }

    public void exportChatHistory(File file, List<Message> messages) {
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.println("Chat Export - " + LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            writer.println("=" + "=".repeat(50));
            writer.println();

            for (Message message : messages) {
                writer.println("[" + message.getCreatedAtFormatted() + "] " +
                        message.getSender() + ":");
                if (message.hasAttachment()) {
                    writer.println("  📎 " + message.getAttachment().getFileName() +
                            " (" + message.getAttachment().getFormattedSize() + ")");
                } else {
                    writer.println("  " + message.getContent());
                }
                writer.println();
            }
        } catch (Exception e) {
            showAlert("Export Error", "Failed to export chat history: " + e.getMessage());
        }
    }

    public void scrollToMessage(ScrollPane scrollPane, VBox messageContainer, Message targetMessage) {
        Platform.runLater(() -> {
            for (int i = 0; i < messageContainer.getChildren().size(); i++) {
                javafx.scene.Node node = messageContainer.getChildren().get(i);
                if (node instanceof VBox vbox && vbox.getUserData() == targetMessage) {
                    double totalHeight = messageContainer.getBoundsInLocal().getHeight();
                    double nodeY = node.getBoundsInParent().getMinY();
                    double scrollValue = nodeY / totalHeight;
                    scrollPane.setVvalue(Math.max(0, Math.min(1, scrollValue)));
                    highlightMessage(vbox);
                    break;
                }
            }
        });
    }

    private void highlightMessage(VBox messageBox) {
        String originalStyle = messageBox.getStyle();
        messageBox.setStyle(originalStyle + "-fx-background-color: rgba(255, 235, 59, 0.3);");
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(2),
                        e -> messageBox.setStyle(originalStyle))
        );
        timeline.play();
    }

    public List<Message> getAllMessages(VBox messageContainer) {
        List<Message> messages = new ArrayList<>();
        for (javafx.scene.Node node : messageContainer.getChildren()) {
            if (node instanceof VBox vbox && vbox.getUserData() instanceof Message) {
                messages.add((Message) vbox.getUserData());
            }
        }
        return messages;
    }

    public int getMessageCount(VBox messageContainer) {
        int count = 0;
        for (javafx.scene.Node node : messageContainer.getChildren()) {
            if (node instanceof VBox vbox && vbox.getUserData() instanceof Message) {
                count++;
            }
        }
        return count;
    }

    public javafx.scene.Node createSeparator() {
        Line separator = new Line();
        separator.setStroke(Color.web("#e9ecef"));
        separator.setStrokeWidth(1);
        separator.setStartX(0);
        separator.setEndX(800);
        return separator;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}