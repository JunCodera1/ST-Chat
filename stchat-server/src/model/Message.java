package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message {
    private final int id;
    private final String senderUsername;
    private final String receiverUsername; // null for public messages
    private final String messageText;
    private final LocalDateTime sentAt;
    private boolean isRead;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

    // Constructor for database messages
    public Message(int id, String senderUsername, String receiverUsername, String messageText, LocalDateTime sentAt, boolean isRead) {
        this.id = id;
        this.senderUsername = senderUsername;
        this.receiverUsername = receiverUsername;
        this.messageText = messageText;
        this.sentAt = sentAt;
        this.isRead = isRead;
    }

    // Constructor for new messages (without ID)
    public Message(String senderUsername, String receiverUsername, String messageText, LocalDateTime sentAt, boolean isRead) {
        this.id = -1; // Will be set by database
        this.senderUsername = senderUsername;
        this.receiverUsername = receiverUsername;
        this.messageText = messageText;
        this.sentAt = sentAt;
        this.isRead = isRead;
    }

    // Constructor for public messages
    public Message(String senderUsername, String messageText, LocalDateTime sentAt) {
        this.id = -1;
        this.senderUsername = senderUsername;
        this.receiverUsername = null; // Public message
        this.messageText = messageText;
        this.sentAt = sentAt;
        this.isRead = false;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }

    public String getMessageText() {
        return messageText;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        this.isRead = read;
    }

    // Utility methods
    public boolean isPrivateMessage() {
        return receiverUsername != null;
    }

    public boolean isPublicMessage() {
        return receiverUsername == null;
    }

    public String getFormattedTime() {
        return sentAt.format(TIME_FORMATTER);
    }

    public String getFormattedDateTime() {
        return sentAt.format(DATETIME_FORMATTER);
    }

    @Override
    public String toString() {
        if (isPrivateMessage()) {
            return String.format("[%s] %s -> %s: %s", getFormattedTime(), senderUsername, receiverUsername, messageText);
        } else {
            return String.format("[%s] %s: %s", getFormattedTime(), senderUsername, messageText);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Message message = (Message) obj;
        return id == message.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}