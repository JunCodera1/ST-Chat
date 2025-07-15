package com.stchat.server.web.controller;

import io.javalin.Javalin;
import io.javalin.http.Context;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

public class FileController {
    private static final String UPLOAD_DIR = "uploads"; // bạn nên cấu hình

    public static void registerRoutes(Javalin app) {
        app.post("/api/files/upload", FileController::handleFileUpload);
    }

    private static void handleFileUpload(Context ctx) {
        // Lấy tất cả file field tên "file"
        var uploadedFiles = ctx.uploadedFiles("file");

        if (uploadedFiles.isEmpty()) {
            ctx.status(400).result("No file uploaded");
            return;
        }

        var uploadedFile = uploadedFiles.get(0);
        var fileName = uploadedFile.filename(); // ✅ đúng API mới
        var fileContent = uploadedFile.content(); // ✅ trả về InputStream

        try {
            String extension = getFileExtension(fileName);
            String generatedName = UUID.randomUUID() + "." + extension;

            Path uploadPath = Path.of("uploads", generatedName);
            Files.createDirectories(uploadPath.getParent());

            Files.copy(fileContent, uploadPath, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = "/static/" + generatedName;

            ctx.status(201).json(Map.of(
                    "status", "success",
                    "url", fileUrl,
                    "fileName", fileName,
                    "fileSize", uploadedFile.size()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).result("Upload failed");
        }
    }


    private static String getFileExtension(String fileName) {
        int dot = fileName.lastIndexOf(".");
        return (dot == -1) ? "" : fileName.substring(dot + 1);
    }
}
