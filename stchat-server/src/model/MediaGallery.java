package model;

import java.sql.Timestamp;

public class MediaGallery {
    private int id;
    private int conversationId;
    private int messageId;
    private String fileUrl;
    private String fileName;
    private FileType fileType;
    private String fileSize;
    private String thumbnailUrl;
    private int uploadedBy;
    private Timestamp createdAt;

    public enum FileType {
        IMAGE,
        VIDEO,
        AUDIO,
        DOCUMENT,
        OTHER
    }

    public MediaGallery() {}

    public MediaGallery(int id, int conversationId, int messageId, String fileUrl, String fileName,
                        FileType fileType, String fileSize, String thumbnailUrl, int uploadedBy, Timestamp createdAt) {
        this.id = id;
        this.conversationId = conversationId;
        this.messageId = messageId;
        this.fileUrl = fileUrl;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.thumbnailUrl = thumbnailUrl;
        this.uploadedBy = uploadedBy;
        this.createdAt = createdAt;
    }

    // Getters & Setters

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    // Các getter/setter khác giống như trước...

    @Override
    public String toString() {
        return "MediaGallery{" +
                "id=" + id +
                ", conversationId=" + conversationId +
                ", messageId=" + messageId +
                ", fileName='" + fileName + '\'' +
                ", fileType=" + fileType +
                ", fileSize='" + fileSize + '\'' +
                ", uploadedBy=" + uploadedBy +
                ", createdAt=" + createdAt +
                '}';
    }
}
