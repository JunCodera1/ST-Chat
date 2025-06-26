package com.stchat.server.model;

import java.sql.Timestamp;

public class MessageReactions {
    private int id;
    private int messageId;
    private int userId;
    private String emoji; // hoặc enum nếu muốn cố định
    private Timestamp createdAt;

    public MessageReactions() {}

    public MessageReactions(int id, int messageId, int userId, String emoji, Timestamp createdAt) {
        this.id = id;
        this.messageId = messageId;
        this.userId = userId;
        this.emoji = emoji;
        this.createdAt = createdAt;
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

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "MessageReactions{" +
                "id=" + id +
                ", messageId=" + messageId +
                ", userId=" + userId +
                ", emoji='" + emoji + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
