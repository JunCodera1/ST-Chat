package com.stchat.server.model;

import java.sql.Timestamp;

public class PinnedMessages {
    private int id;
    private int conversationId;
    private int messageId;
    private int pinnedBy;
    private Timestamp pinnedAt;

    public PinnedMessages() {}

    public PinnedMessages(int id, int conversationId, int messageId, int pinnedBy, Timestamp pinnedAt) {
        this.id = id;
        this.conversationId = conversationId;
        this.messageId = messageId;
        this.pinnedBy = pinnedBy;
        this.pinnedAt = pinnedAt;
    }

    // Getters and Setters

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

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getPinnedBy() {
        return pinnedBy;
    }

    public void setPinnedBy(int pinnedBy) {
        this.pinnedBy = pinnedBy;
    }

    public Timestamp getPinnedAt() {
        return pinnedAt;
    }

    public void setPinnedAt(Timestamp pinnedAt) {
        this.pinnedAt = pinnedAt;
    }

    @Override
    public String toString() {
        return "PinnedMessages{" +
                "id=" + id +
                ", conversationId=" + conversationId +
                ", messageId=" + messageId +
                ", pinnedBy=" + pinnedBy +
                ", pinnedAt=" + pinnedAt +
                '}';
    }
}
