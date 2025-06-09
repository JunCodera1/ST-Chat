package me.chatapp.stchat.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message {
    private final String sender;
    private final String content;
    private final MessageType type;
    private final LocalDateTime timestamp;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public Message(String sender, String content, MessageType type, LocalDateTime timestamp) {
        this.sender = sender;
        this.content = content;
        this.type = type;
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public MessageType getType() {
        return type;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getFormattedTime() {
        return timestamp.format(TIME_FORMATTER);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s", getFormattedTime(), sender, content);
    }
}
