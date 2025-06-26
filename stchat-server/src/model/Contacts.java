package model;

import java.sql.Timestamp;

public class Contacts {
    private int id;
    private int userId;
    private int contactUserId;
    private String nickname;
    private boolean isBlocked;
    private boolean isFavorite;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Contacts() {}

    public Contacts(int id, int userId, int contactUserId, String nickname,
                    boolean isBlocked, boolean isFavorite,
                    Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.userId = userId;
        this.contactUserId = contactUserId;
        this.nickname = nickname;
        this.isBlocked = isBlocked;
        this.isFavorite = isFavorite;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and setters

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

    public int getContactUserId() {
        return contactUserId;
    }

    public void setContactUserId(int contactUserId) {
        this.contactUserId = contactUserId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
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
        return "Contacts{" +
                "id=" + id +
                ", userId=" + userId +
                ", contactUserId=" + contactUserId +
                ", nickname='" + nickname + '\'' +
                ", isBlocked=" + isBlocked +
                ", isFavorite=" + isFavorite +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
