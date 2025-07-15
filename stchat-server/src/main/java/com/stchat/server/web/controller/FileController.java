package com.stchat.server.web.controller;

import io.javalin.Javalin;
import io.javalin.http.Context;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

public class FileController {
    private static final String UPLOAD_DIR = "/home/jun/IdeaProjects/ST-Chat/stchat-server/uploads/";


    public static void registerRoutes(Javalin app) {
        app.post("/api/files/upload", FileController::handleFileUpload);
    }

    private static void handleFileUpload(Context ctx) {
        var uploadedFiles = ctx.uploadedFiles("file");

        if (uploadedFiles.isEmpty()) {
            ctx.status(400).result("No file uploaded");
            return;
        }

        var uploadedFile = uploadedFiles.get(0);
        var fileName = uploadedFile.filename();
        var fileContent = uploadedFile.content();

        try {
            String extension = getFileExtension(fileName).toLowerCase();
            String originalName = UUID.randomUUID().toString();
            String inputFileName = originalName + "." + extension;
            String outputFileName = originalName + ".wav";

            Path inputPath = Path.of(UPLOAD_DIR, inputFileName);
            Path outputPath = Path.of(UPLOAD_DIR, outputFileName);

            // Lưu file gốc (mp3)
            Files.createDirectories(inputPath.getParent());
            Files.copy(fileContent, inputPath, StandardCopyOption.REPLACE_EXISTING);

            // Nếu là file .mp3, convert sang .wav bằng ffmpeg
            if (extension.equals("mp3")) {
                Process process = new ProcessBuilder(
                        "ffmpeg", "-y", // overwrite if exists
                        "-i", inputPath.toString(),
                        outputPath.toString()
                ).inheritIO().start();

                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    throw new RuntimeException("FFmpeg failed to convert file.");
                }

                // Xóa file gốc mp3 nếu muốn
                Files.deleteIfExists(inputPath);

                ctx.status(201).json(Map.of(
                        "status", "success",
                        "url", outputPath.toString(),
                        "fileName", outputFileName,
                        "filePath", outputPath.toString(),
                        "fileSize", Files.size(outputPath)
                ));
            } else {
                ctx.status(201).json(Map.of(
                        "status", "success",
                        "url", inputPath.toString(),
                        "fileName", inputFileName,
                        "filePath", inputPath.toString(),
                        "fileSize", Files.size(inputPath)
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).result("Upload failed: " + e.getMessage());
        }
    }



    private static String getFileExtension(String fileName) {
        int dot = fileName.lastIndexOf(".");
        return (dot == -1) ? "" : fileName.substring(dot + 1);
    }
}
