package com.stchat.server.model;

import java.sql.Timestamp;

public class ArchivedConversations {
    private int id;
    private int userId;
    private int conversationId;
    private Timestamp archivedAt;

    public ArchivedConversations() {}

    public ArchivedConversations(int id, int userId, int conversationId, Timestamp archivedAt) {
        this.id = id;
        this.userId = userId;
        this.conversationId = conversationId;
        this.archivedAt = archivedAt;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getConversationId() {
        return conversationId;
    }

    public void setConversationId(int conversationId) {
        this.conversationId = conversationId;
    }

    public Timestamp getArchivedAt() {
        return archivedAt;
    }

    public void setArchivedAt(Timestamp archivedAt) {
        this.archivedAt = archivedAt;
    }

    @Override
    public String toString() {
        return "ArchivedConversations{" +
                "id=" + id +
                ", userId=" + userId +
                ", conversationId=" + conversationId +
                ", archivedAt=" + archivedAt +
                '}';
    }
}
