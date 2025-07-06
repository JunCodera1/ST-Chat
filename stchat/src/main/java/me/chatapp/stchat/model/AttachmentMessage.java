// AttachmentMessage.java
package me.chatapp.stchat.model;

public class AttachmentMessage {
    private String fileName;
    private String fileType;
    private long fileSize;
    private String filePath;
    private String fileExtension;
    private byte[] fileData; // For storing file content if needed

    public AttachmentMessage(String fileName, String fileType, long fileSize, String filePath, String fileExtension) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.filePath = filePath;
        this.fileExtension = fileExtension;
    }

    // Getters and setters
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getFileExtension() { return fileExtension; }
    public void setFileExtension(String fileExtension) { this.fileExtension = fileExtension; }

    public byte[] getFileData() { return fileData; }
    public void setFileData(byte[] fileData) { this.fileData = fileData; }

    public String getFormattedSize() {
        if (fileSize < 1024) return fileSize + " B";
        if (fileSize < 1024 * 1024) return (fileSize / 1024) + " KB";
        if (fileSize < 1024 * 1024 * 1024) return (fileSize / (1024 * 1024)) + " MB";
        return (fileSize / (1024 * 1024 * 1024)) + " GB";
    }

    public String getFileIcon() {
        switch (fileType.toLowerCase()) {
            case "image": return "🖼️";
            case "pdf": return "📄";
            case "document": return "📝";
            case "video": return "🎬";
            case "audio": return "🎵";
            default: return "📎";
        }
    }

    public boolean isImage() {
        return fileType.equalsIgnoreCase("image");
    }

    public boolean isVideo() {
        return fileType.equalsIgnoreCase("video");
    }

    public boolean isAudio() {
        return fileType.equalsIgnoreCase("audio");
    }
}