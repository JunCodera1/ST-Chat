package com.stchat.server.model;

import java.sql.Timestamp;

public class MessageAttachments {
    private int id;
    private int messageId;
    private String fileUrl;
    private String fileName;
    private FileType fileType;
    private int fileSize; // in bytes
    private String thumbnailUrl;
    private Timestamp createdAt;

    public enum FileType {
        IMAGE,
        VIDEO,
        AUDIO,
        DOCUMENT,
        ARCHIVE,
        OTHER
    }

    public MessageAttachments() {}

    public MessageAttachments(int id, int messageId, String fileUrl, String fileName, FileType fileType,
                              int fileSize, String thumbnailUrl, Timestamp createdAt) {
        this.id = id;
        this.messageId = messageId;
        this.fileUrl = fileUrl;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.thumbnailUrl = thumbnailUrl;
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

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "MessageAttachments{" +
                "id=" + id +
                ", messageId=" + messageId +
                ", fileName='" + fileName + '\'' +
                ", fileType=" + fileType +
                ", fileSize=" + fileSize +
                ", createdAt=" + createdAt +
                '}';
    }
}
