package com.stchat.server.model;

import java.sql.Timestamp;

public class Channel {
    private String name;
    private String description;
    private boolean isPrivate;
    private int createdBy;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Channel() {}

    public Channel(String name, String description, boolean isPrivate, int createdBy, Timestamp createdAt, Timestamp updatedAt) {
        this.name = name;
        this.description = description;
        this.isPrivate = isPrivate;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters & Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
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
        return "Channel{" +
                "name='" + name + '\'' +
                ", isPrivate=" + isPrivate +
                ", createdBy=" + createdBy +
                ", createdAt=" + createdAt +
                '}';
    }
}
