package model;

import java.sql.Timestamp;

public class BookMarks {
    private int id;
    private int userId;
    private BookMarksType bookMarksType;
    private int referenceId; // ID của đối tượng được đánh dấu
    private String title;
    private String description;
    private String url;
    private String fileUrl;
    private String fileName;
    private int fileSize; // byte
    private FileType fileType;
    private String thumbnailUrl;
    private Timestamp createdAt;

    public enum BookMarksType {
        MESSAGE,
        FILE,
        LINK,
        MEDIA,
        LOCATION
    }

    public enum FileType {
        IMAGE,
        VIDEO,
        AUDIO,
        DOCUMENT,
        ARCHIVE,
        OTHER
    }

    public BookMarks() {}

    public BookMarks(int id, int userId, BookMarksType bookMarksType, int referenceId, String title,
                     String description, String url, String fileUrl, String fileName, int fileSize,
                     FileType fileType, String thumbnailUrl, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.bookMarksType = bookMarksType;
        this.referenceId = referenceId;
        this.title = title;
        this.description = description;
        this.url = url;
        this.fileUrl = fileUrl;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileType = fileType;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public BookMarksType getBookMarksType() {
        return bookMarksType;
    }

    public void setBookMarksType(BookMarksType bookMarksType) {
        this.bookMarksType = bookMarksType;
    }

    public int getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(int referenceId) {
        this.referenceId = referenceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
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
        return "BookMarks{" +
                "id=" + id +
                ", userId=" + userId +
                ", bookMarksType=" + bookMarksType +
                ", referenceId=" + referenceId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileSize=" + fileSize +
                ", fileType=" + fileType +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
