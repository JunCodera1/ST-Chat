package com.stchat.server.model;

import java.sql.Timestamp;

public class UserPreferences {
    private int id;
    private int userId;
    private Theme theme;
    private Language language;
    private String timezone;
    private boolean autoDownloadMedia;
    private boolean showOnlineStatus;
    private boolean readReceipts;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    public enum Theme {
        LIGHT, DARK, SYSTEM_DEFAULT
    }

    public enum Language {
        EN("English"), VI("Vietnamese");

        private final String label;
        Language(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }


    public UserPreferences() {}

    public UserPreferences(int id, int userId, Theme theme, Language language, String timezone,
                           boolean autoDownloadMedia, boolean showOnlineStatus, boolean readReceipts,
                           Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.userId = userId;
        this.theme = theme;
        this.language = language;
        this.timezone = timezone;
        this.autoDownloadMedia = autoDownloadMedia;
        this.showOnlineStatus = showOnlineStatus;
        this.readReceipts = readReceipts;
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

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public boolean isAutoDownloadMedia() {
        return autoDownloadMedia;
    }

    public void setAutoDownloadMedia(boolean autoDownloadMedia) {
        this.autoDownloadMedia = autoDownloadMedia;
    }

    public boolean isShowOnlineStatus() {
        return showOnlineStatus;
    }

    public void setShowOnlineStatus(boolean showOnlineStatus) {
        this.showOnlineStatus = showOnlineStatus;
    }

    public boolean isReadReceipts() {
        return readReceipts;
    }

    public void setReadReceipts(boolean readReceipts) {
        this.readReceipts = readReceipts;
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
        return "UserPreferences{" +
                "id=" + id +
                ", userId=" + userId +
                ", theme='" + theme + '\'' +
                ", language='" + language + '\'' +
                ", timezone='" + timezone + '\'' +
                ", autoDownloadMedia=" + autoDownloadMedia +
                ", showOnlineStatus=" + showOnlineStatus +
                ", readReceipts=" + readReceipts +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

}
