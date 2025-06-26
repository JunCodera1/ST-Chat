package model;

import java.sql.Timestamp;

public class NotificationSettings {
    private int id;
    private int userId;
    private int conversationId;
    private boolean emailNotifications;
    private boolean pushNotifications;
    private boolean desktopNotifications;
    private boolean soundNotifications;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public NotificationSettings() {}

    public NotificationSettings(int id, int userId, int conversationId,
                                boolean emailNotifications, boolean pushNotifications,
                                boolean desktopNotifications, boolean soundNotifications,
                                Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.userId = userId;
        this.conversationId = conversationId;
        this.emailNotifications = emailNotifications;
        this.pushNotifications = pushNotifications;
        this.desktopNotifications = desktopNotifications;
        this.soundNotifications = soundNotifications;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public boolean isEmailNotifications() {
        return emailNotifications;
    }

    public void setEmailNotifications(boolean emailNotifications) {
        this.emailNotifications = emailNotifications;
    }

    public boolean isPushNotifications() {
        return pushNotifications;
    }

    public void setPushNotifications(boolean pushNotifications) {
        this.pushNotifications = pushNotifications;
    }

    public boolean isDesktopNotifications() {
        return desktopNotifications;
    }

    public void setDesktopNotifications(boolean desktopNotifications) {
        this.desktopNotifications = desktopNotifications;
    }

    public boolean isSoundNotifications() {
        return soundNotifications;
    }

    public void setSoundNotifications(boolean soundNotifications) {
        this.soundNotifications = soundNotifications;
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
        return "NotificationSettings{" +
                "id=" + id +
                ", userId=" + userId +
                ", conversationId=" + conversationId +
                ", emailNotifications=" + emailNotifications +
                ", pushNotifications=" + pushNotifications +
                ", desktopNotifications=" + desktopNotifications +
                ", soundNotifications=" + soundNotifications +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
