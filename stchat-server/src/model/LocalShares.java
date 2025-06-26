package model;

import java.sql.Timestamp;

public class LocalShares {
    private int id;
    private int messageId;
    private double latitude;
    private double longitude;
    private String address;
    private String locationName;
    private boolean isLive;
    private Timestamp expiredAt;
    private Timestamp createdAt;

    public LocalShares() {}

    public LocalShares(int id, int messageId, double latitude, double longitude,
                       String address, String locationName, boolean isLive,
                       Timestamp expiredAt, Timestamp createdAt) {
        this.id = id;
        this.messageId = messageId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.locationName = locationName;
        this.isLive = isLive;
        this.expiredAt = expiredAt;
        this.createdAt = createdAt;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean live) {
        isLive = live;
    }

    public Timestamp getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(Timestamp expiredAt) {
        this.expiredAt = expiredAt;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "LocalShares{" +
                "id=" + id +
                ", messageId=" + messageId +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", locationName='" + locationName + '\'' +
                ", isLive=" + isLive +
                ", expiredAt=" + expiredAt +
                ", createdAt=" + createdAt +
                '}';
    }
}
