package model;

import java.sql.Timestamp;

public class ChannelMembers {
    private int id;
    private int channelId;
    private int userId;
    private Role role;
    private Timestamp joinedAt;

    public enum Role {
        OWNER,
        ADMIN,
        MEMBER
    }

    public ChannelMembers() {}

    public ChannelMembers(int id, int channelId, int userId, Role role, Timestamp joinedAt) {
        this.id = id;
        this.channelId = channelId;
        this.userId = userId;
        this.role = role;
        this.joinedAt = joinedAt;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Timestamp getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Timestamp joinedAt) {
        this.joinedAt = joinedAt;
    }

    @Override
    public String toString() {
        return "ChannelMembers{" +
                "id=" + id +
                ", channelId=" + channelId +
                ", userId=" + userId +
                ", role=" + role +
                ", joinedAt=" + joinedAt +
                '}';
    }
}
