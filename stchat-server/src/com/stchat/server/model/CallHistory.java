package com.stchat.server.model;

import java.sql.Timestamp;

public class CallHistory {
    private int id;
    private int callerId;
    private int receiverId;
    private int conversationId;
    private CallType callType;
    private CallStatus status;
    private int duration;
    private Timestamp startedAt;
    private Timestamp endedAt;
    private Timestamp createdAt;

    public enum CallType {
        VOICE,
        VIDEO
    }

    public enum CallStatus {
        MISSED,
        REJECTED,
        ANSWERED,
        CANCELLED
    }


    public CallHistory() {}

    public CallHistory(int id, int callerId, int receiverId, int conversationId,
                       CallType callType, CallStatus status, int duration,
                       Timestamp startedAt, Timestamp endedAt, Timestamp createdAt) {
        this.id = id;
        this.callerId = callerId;
        this.receiverId = receiverId;
        this.conversationId = conversationId;
        this.callType = callType;
        this.status = status;
        this.duration = duration;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.createdAt = createdAt;
    }

    // Getters & Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCallerId() {
        return callerId;
    }

    public void setCallerId(int callerId) {
        this.callerId = callerId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public int getConversationId() {
        return conversationId;
    }

    public void setConversationId(int conversationId) {
        this.conversationId = conversationId;
    }

    public CallType getCallType() {
        return callType;
    }

    public void setCallType(CallType callType) {
        this.callType = callType;
    }

    public CallStatus getStatus() {
        return status;
    }

    public void setStatus(CallStatus status) {
        this.status = status;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Timestamp getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Timestamp startedAt) {
        this.startedAt = startedAt;
    }

    public Timestamp getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(Timestamp endedAt) {
        this.endedAt = endedAt;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "CallHistory{" +
                "id=" + id +
                ", callerId=" + callerId +
                ", receiverId=" + receiverId +
                ", callType=" + callType +
                ", status='" + status + '\'' +
                ", duration=" + duration +
                ", startedAt=" + startedAt +
                ", endedAt=" + endedAt +
                ", createdAt=" + createdAt +
                '}';
    }
}
