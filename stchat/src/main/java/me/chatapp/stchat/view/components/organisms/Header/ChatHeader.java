package me.chatapp.stchat.view.components.organisms.Header;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import me.chatapp.stchat.api.UserApiClient;
import me.chatapp.stchat.view.components.atoms.Circle.AvatarCircleHeader;
import me.chatapp.stchat.view.components.atoms.Button.IconButton;
import me.chatapp.stchat.view.components.molecules.Conversation.ConversationInfoBlock;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class ChatHeader {
    private final HBox container;
    private final AvatarCircleHeader avatarCircle;
    private final ConversationInfoBlock infoBlock;
    private final IconButton callBtn, videoBtn, infoBtn;

    private Runnable onCallAction, onVideoCallAction, onInfoAction;

    public ChatHeader() {
        container = new HBox(12);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(12));
        container.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #e4e6ea;
            -fx-border-width: 0 0 1 0;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 3, 0, 0, 1);
            """);

        avatarCircle = new AvatarCircleHeader("?", Color.web("#42b883"));

        infoBlock = new ConversationInfoBlock();
        HBox.setHgrow(infoBlock, Priority.ALWAYS);

        callBtn = new IconButton("fas-phone");
        videoBtn = new IconButton("fas-video");
        infoBtn = new IconButton("fas-info-circle");


        callBtn.setOnAction(e -> { if (onCallAction != null) onCallAction.run(); });
        videoBtn.setOnAction(e -> { if (onVideoCallAction != null) onVideoCallAction.run(); });
        infoBtn.setOnAction(e -> { if (onInfoAction != null) onInfoAction.run(); });

        HBox actionButtons = new HBox(8, callBtn, videoBtn, infoBtn);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);

        container.getChildren().addAll(avatarCircle, infoBlock, actionButtons);
        setButtonsEnabled(false);
    }

    public void setActiveConversation(String username) {
        UserApiClient client = new UserApiClient();

        client.findUserByUsername(username).ifPresentOrElse(user -> {
            Platform.runLater(() -> {
                infoBlock.setName(user.getFirstName() + " " + user.getLastName());
                infoBlock.setStatus(user.isActive() ? "Online" : "Last seen: " + formatTime(user.getLastSeen()));

                if (user.getAvatarUrl() != null && !user.getAvatarUrl().isBlank()) {
                    avatarCircle.setImageFromUrl(user.getAvatarUrl());
                } else {
                    avatarCircle.setInitials(generateInitials(username));
                    avatarCircle.setColor(getColorFromName(username));
                }

                setButtonsEnabled(true);
            });
        }, () -> {
            // Trường hợp không tìm thấy user
            Platform.runLater(() -> {
                infoBlock.setName(username);
                infoBlock.setStatus("Unknown");
                avatarCircle.setInitials(generateInitials(username));
                setButtonsEnabled(false);
            });
        });
    }

    private String formatTime(Timestamp timestamp) {
        if (timestamp == null) return "Offline";

        LocalDateTime time = timestamp.toLocalDateTime();
        LocalDateTime now = LocalDateTime.now();

        long minutes = ChronoUnit.MINUTES.between(time, now);
        long hours = ChronoUnit.HOURS.between(time, now);
        long days = ChronoUnit.DAYS.between(time, now);

        if (minutes < 1) return "Just now";
        if (minutes < 60) return minutes + " minutes ago";
        if (hours < 24) return hours + " hours ago";

        if (days == 0) {
            return "Today at " + time.format(DateTimeFormatter.ofPattern("HH:mm"));
        }

        if (days == 1) {
            return "Yesterday at " + time.format(DateTimeFormatter.ofPattern("HH:mm"));
        }

        return time.format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"));
    }

    private String generateInitials(String name) {
        String[] parts = name.split(" ");
        if (parts.length >= 2) {
            return ("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase();
        }
        return name.substring(0, Math.min(2, name.length())).toUpperCase();
    }



    public void setOnlineStatus(String status) {
        infoBlock.setStatus(status);
    }

    public void setButtonsEnabled(boolean enabled) {
        callBtn.setEnabled(enabled);
        videoBtn.setEnabled(enabled);
        infoBtn.setEnabled(enabled);
    }

    public void setOnCallAction(Runnable r) { this.onCallAction = r; }
    public void setOnVideoCallAction(Runnable r) { this.onVideoCallAction = r; }
    public void setOnInfoAction(Runnable r) { this.onInfoAction = r; }

    public HBox getComponent() {
        return container;
    }

    private Color getColorFromName(String name) {
        String[] palette = {
                "#42b883", "#1877f2", "#fd79a8", "#6c5ce7", "#a29bfe",
                "#fd63c3", "#ee5a24", "#00b894", "#0984e3", "#e17055",
                "#81ecec", "#fab1a0"
        };
        return Color.web(palette[Math.abs(name.hashCode()) % palette.length]);
    }
}
