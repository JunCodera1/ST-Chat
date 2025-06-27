package com.stchat.server.model;

import java.sql.Timestamp;

public class Message {
    private int id;
    private int conservationId;
    private int senderId;
    private String content;
    private MessageType messageType;
    private Integer replyToMessageId;
    private String fileUrl;
    private String fileName;
    private int fileSize;
    private boolean isEdited;
    private boolean isDeleted;
    private boolean isPinned;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public enum MessageType {
        FILE, IMAGE, LINK
    }

    // Constructors
    public Message() {}

    public Message(int id, int conservationId, int senderId, String content, MessageType messageType,
                   Integer replyToMessageId, String fileUrl, String fileName, int fileSize,
                   boolean isEdited, boolean isDeleted, boolean isPinned,
                   Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.conservationId = conservationId;
        this.senderId = senderId;
        this.content = content;
        this.messageType = messageType;
        this.replyToMessageId = replyToMessageId;
        this.fileUrl = fileUrl;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.isEdited = isEdited;
        this.isDeleted = isDeleted;
        this.isPinned = isPinned;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getConservationId() {
        return conservationId;
    }

    public int getSenderId() {
        return senderId;
    }

    public String getContent() {
        return content;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public Integer getReplyToMessageId() {
        return replyToMessageId;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public int getFileSize() {
        return fileSize;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setConservationId(int conservationId) {
        this.conservationId = conservationId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public void setReplyToMessageId(Integer replyToMessageId) {
        this.replyToMessageId = replyToMessageId;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public void setEdited(boolean edited) {
        isEdited = edited;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }



    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", messageType=" + messageType +
                ", createdAt=" + createdAt +
                '}';
    }
}
