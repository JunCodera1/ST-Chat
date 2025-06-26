package model;

import java.sql.Timestamp;

public class ConversationParticipants {
    private int id;
    private int conversationId;
    private int userId;
    private Role role;
    private Timestamp joinedAt;
    private Timestamp leftAt;
    private boolean isMuted;
    private int lastReadMessageId;

    public enum Role {
        OWNER,
        ADMIN,
        MEMBER
    }

    public ConversationParticipants() {}

    public ConversationParticipants(int id, int conversationId, int userId, Role role,
                                    Timestamp joinedAt, Timestamp leftAt, boolean isMuted,
                                    int lastReadMessageId) {
        this.id = id;
        this.conversationId = conversationId;
        this.userId = userId;
        this.role = role;
        this.joinedAt = joinedAt;
        this.leftAt = leftAt;
        this.isMuted = isMuted;
        this.lastReadMessageId = lastReadMessageId;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getConversationId() {
        return conversationId;
    }

    public void setConversationId(int conversationId) {
        this.conversationId = conversationId;
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

    public Timestamp getLeftAt() {
        return leftAt;
    }

    public void setLeftAt(Timestamp leftAt) {
        this.leftAt = leftAt;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
    }

    public int getLastReadMessageId() {
        return lastReadMessageId;
    }

    public void setLastReadMessageId(int lastReadMessageId) {
        this.lastReadMessageId = lastReadMessageId;
    }

    @Override
    public String toString() {
        return "ConversationParticipants{" +
                "id=" + id +
                ", conversationId=" + conversationId +
                ", userId=" + userId +
                ", role=" + role +
                ", joinedAt=" + joinedAt +
                ", leftAt=" + leftAt +
                ", isMuted=" + isMuted +
                ", lastReadMessageId=" + lastReadMessageId +
                '}';
    }
}
