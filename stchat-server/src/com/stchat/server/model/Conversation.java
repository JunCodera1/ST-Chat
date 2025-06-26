package com.stchat.server.model;

import java.sql.Timestamp;

public class Conversation {
    private int id;
    private String name;
    private ConversationType type;
    private String avatarUrl;
    private String description;
    private boolean isArchived;
    private int createdBy;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public enum ConversationType {
        PRIVATE,
        GROUP,
        CHANNEL
    }

    public Conversation() {}

    public Conversation(int id, String name, ConversationType type, String avatarUrl, String description,
                        boolean isArchived, int createdBy, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.avatarUrl = avatarUrl;
        this.description = description;
        this.isArchived = isArchived;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ConversationType getType() {
        return type;
    }

    public void setType(ConversationType type) {
        this.type = type;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", isArchived=" + isArchived +
                ", createdBy=" + createdBy +
                ", createdAt=" + createdAt +
                '}';
    }
}
