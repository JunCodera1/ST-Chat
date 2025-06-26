package model;

import java.sql.Timestamp;

public class MessageStatus {
    private int id;
    private int messageId;
    private int userId;
    private Status status;
    private Timestamp timestamp;

    public enum Status {
        UNREAD,
        DELIVERED,
        READ,
        SEEN
    }

    public MessageStatus() {}

    public MessageStatus(int id, int messageId, int userId, Status status, Timestamp timestamp) {
        this.id = id;
        this.messageId = messageId;
        this.userId = userId;
        this.status = status;
        this.timestamp = timestamp;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "MessageStatus{" +
                "id=" + id +
                ", messageId=" + messageId +
                ", userId=" + userId +
                ", status=" + status +
                ", timestamp=" + timestamp +
                '}';
    }
}
