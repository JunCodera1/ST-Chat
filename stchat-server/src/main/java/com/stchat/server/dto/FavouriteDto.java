package com.stchat.server.dto;

import java.time.LocalDateTime;

public class FavouriteDto {
    private int userId;
    private int favoriteUserId;
    private LocalDateTime createdAt;

    public FavouriteDto(int userId, int favoriteUserId, LocalDateTime createdAt) {
        this.userId = userId;
        this.favoriteUserId = favoriteUserId;
        this.createdAt = createdAt;
    }

    public int getUserId() {
        return userId;
    }

    public int getFavoriteUserId() {
        return favoriteUserId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
