package com.stchat.server.model;

import java.sql.Timestamp;

public class TypingIndicators {
    private int id;
    private int conversationId;
    private int userId;
    private boolean isTyping;
    private Timestamp lastTyped;

    public TypingIndicators() {}

    public TypingIndicators(int id, int conversationId, int userId, boolean isTyping, Timestamp lastTyped) {
        this.id = id;
        this.conversationId = conversationId;
        this.userId = userId;
        this.isTyping = isTyping;
        this.lastTyped = lastTyped;
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getConversationId() {
        return conversationId;
    }

    public void setConversationId(int conversationId) {
        this.conversationId = conversationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isTyping() {
        return isTyping;
    }

    public void setTyping(boolean typing) {
        isTyping = typing;
    }

    public Timestamp getLastTyped() {
        return lastTyped;
    }

    public void setLastTyped(Timestamp lastTyped) {
        this.lastTyped = lastTyped;
    }

    @Override
    public String toString() {
        return "TypingIndicators{" +
                "id=" + id +
                ", conversationId=" + conversationId +
                ", userId=" + userId +
                ", isTyping=" + isTyping +
                ", lastTyped=" + lastTyped +
                '}';
    }
}
