package me.chatapp.stchat.util;

public class FileUtil {
    public static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }
    public static String getFileType(String extension) {
        return switch (extension) {
            case "png", "jpg", "jpeg", "gif", "bmp" -> "Image";
            case "pdf" -> "PDF";
            case "doc", "docx" -> "Document";
            case "txt" -> "Text";
            case "mp4", "avi", "mkv", "mov" -> "Video";
            case "mp3", "wav", "ogg" -> "Audio";
            default -> "File";
        };
    }
    public static String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return (bytes / 1024) + " KB";
        if (bytes < 1024 * 1024 * 1024) return (bytes / (1024 * 1024)) + " MB";
        return (bytes / (1024 * 1024 * 1024)) + " GB";
    }
}

