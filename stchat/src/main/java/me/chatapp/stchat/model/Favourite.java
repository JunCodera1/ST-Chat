package me.chatapp.stchat.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class Favourite {

    @JsonProperty("userId")
    private int userId;

    @JsonProperty("favoriteUserId")
    private int favoriteUserId;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    public Favourite() {
        // Default constructor for Jackson
    }

    public Favourite(int userId, int favoriteUserId, LocalDateTime createdAt) {
        this.userId = userId;
        this.favoriteUserId = favoriteUserId;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFavoriteUserId() {
        return favoriteUserId;
    }

    public void setFavoriteUserId(int favoriteUserId) {
        this.favoriteUserId = favoriteUserId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Favourite{" +
                "userId=" + userId +
                ", favoriteUserId=" + favoriteUserId +
                ", createdAt=" + createdAt +
                '}';
    }
}
