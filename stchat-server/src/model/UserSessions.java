package model;

import java.sql.Timestamp;

public class UserSessions {
    private int id;
    private int userId;
    private String deviceType;
    private String deviceName;
    private String ipAddress;
    private String userAgent;
    private boolean isActive;
    private Timestamp lastActivity;
    private Timestamp createdAt;

    public UserSessions() {}

    public UserSessions(int id, int userId, String deviceType, String deviceName,
                        String ipAddress, String userAgent, boolean isActive,
                        Timestamp lastActivity, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.deviceType = deviceType;
        this.deviceName = deviceName;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.isActive = isActive;
        this.lastActivity = lastActivity;
        this.createdAt = createdAt;
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

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Timestamp getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(Timestamp lastActivity) {
        this.lastActivity = lastActivity;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void updateActivity() {
        this.lastActivity = new Timestamp(System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return "UserSessions{" +
                "id=" + id +
                ", userId=" + userId +
                ", deviceType='" + deviceType + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", isActive=" + isActive +
                ", lastActivity=" + lastActivity +
                ", createdAt=" + createdAt +
                '}';
    }
}
