package com.stchat.server.model;

import java.sql.Timestamp;

public class Favourite {

    private int userId;
    private int favoriteUserId;
    private Timestamp createdAt;

    public Favourite() {
        // Required for Jackson
    }

    public Favourite(int userId, int favoriteUserId, Timestamp createdAt) {
        this.userId = userId;
        this.favoriteUserId = favoriteUserId;
        this.createdAt = Timestamp.valueOf(createdAt.toLocalDateTime());
    }

    // ✅ Add getters (important for Jackson)
    public int getUserId() {
        return userId;
    }

    public int getFavoriteUserId() {
        return favoriteUserId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    // Optional: Add setters if needed
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setFavoriteUserId(int favoriteUserId) {
        this.favoriteUserId = favoriteUserId;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
