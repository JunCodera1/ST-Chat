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

    // Getters and Setters (chèn bằng IDE hoặc tự viết)

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
