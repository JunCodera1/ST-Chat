package me.chatapp.stchat.util;

import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;

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
    public static void setupFileFilters(FileChooser fileChooser) {
        FileChooser.ExtensionFilter allFiles = new FileChooser.ExtensionFilter("All Files", "*.*");
        FileChooser.ExtensionFilter imageFiles = new FileChooser.ExtensionFilter("Images",
                "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp", "*.svg", "*.webp");
        FileChooser.ExtensionFilter documentFiles = new FileChooser.ExtensionFilter("Documents",
                "*.pdf", "*.doc", "*.docx", "*.txt", "*.rtf", "*.odt", "*.xls", "*.xlsx");
        FileChooser.ExtensionFilter videoFiles = new FileChooser.ExtensionFilter("Videos",
                "*.mp4", "*.avi", "*.mkv", "*.mov", "*.wmv", "*.flv", "*.webm");
        FileChooser.ExtensionFilter audioFiles = new FileChooser.ExtensionFilter("Audio",
                "*.mp3", "*.wav", "*.ogg", "*.m4a", "*.flac", "*.aac");

        fileChooser.getExtensionFilters().addAll(
                allFiles, imageFiles, documentFiles, videoFiles, audioFiles
        );

        fileChooser.setSelectedExtensionFilter(allFiles);
    }

    public static File convertMp3ToWav(File mp3File) throws IOException, InterruptedException {
        String wavFilePath = mp3File.getAbsolutePath().replace(".mp3", ".wav");
        File wavFile = new File(wavFilePath);

        ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-y", "-i", mp3File.getAbsolutePath(), wavFile.getAbsolutePath());
        pb.redirectErrorStream(true);
        Process process = pb.start();

        int exitCode = process.waitFor();
        if (exitCode != 0 || !wavFile.exists()) {
            throw new IOException("Chuyển mp3 sang wav thất bại");
        }

        return wavFile;
    }


    public static String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return (bytes / 1024) + " KB";
        if (bytes < 1024 * 1024 * 1024) return (bytes / (1024 * 1024)) + " MB";
        return (bytes / (1024 * 1024 * 1024)) + " GB";
    }
}

