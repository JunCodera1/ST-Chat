package com.stchat.server.web.server;

import com.stchat.server.model.User;
import com.stchat.server.service.UserService;
import io.javalin.Javalin;
import io.javalin.http.UploadedFile;
import io.javalin.http.staticfiles.Location;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Optional;
import java.util.UUID;

public class StaticFileServer {

    public static void start(int port, String absoluteBaseDir) {
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add(staticFileConfig -> {
                staticFileConfig.directory = absoluteBaseDir;
                staticFileConfig.hostedPath = "/uploads";
                staticFileConfig.location = Location.EXTERNAL;
            });
        });

        app.post("/api/users/{id}/avatar", ctx -> {
            int userId;
            try {
                userId = Integer.parseInt(ctx.pathParam("id"));
            } catch (NumberFormatException e) {
                ctx.status(400).result("Invalid user ID");
                return;
            }

            UploadedFile file = ctx.uploadedFile("file");
            if (file == null) {
                ctx.status(400).result("Missing file");
                return;
            }

            UserService userService = new UserService();
            Optional<User> optionalUser = userService.getUserById(userId);
            if (optionalUser.isEmpty()) {
                ctx.status(404).result("User not found");
                return;
            }

            User user = optionalUser.get();

            // ❌ Fix xóa avatar cũ đúng path tuyệt đối
            if (user.getAvatarUrl() != null && !user.getAvatarUrl().isBlank()) {
                String oldFilename = Paths.get(user.getAvatarUrl()).getFileName().toString();
                Path oldPath = Paths.get(absoluteBaseDir, "avatars", oldFilename);
                try {
                    Files.deleteIfExists(oldPath);
                } catch (IOException e) {
                    System.err.println("⚠️ Failed to delete old avatar: " + e.getMessage());
                }
            }

            // ✅ Lưu avatar mới vào đúng nơi server đang host
            String newFileName = UUID.randomUUID() + "-" + file.filename();
            Path newPath = Paths.get(absoluteBaseDir, "avatars", newFileName);
            Files.createDirectories(newPath.getParent());

            try (InputStream is = file.content()) {
                Files.copy(is, newPath, StandardCopyOption.REPLACE_EXISTING);
            }

            String newAvatarUrl = "/uploads/avatars/" + newFileName;
            boolean success = userService.updateAvatar(userId, newAvatarUrl);

            if (success) {
                ctx.status(200).result(newAvatarUrl);
            } else {
                ctx.status(500).result("Failed to update avatar URL in database");
            }
        });

        app.start(port);
        System.out.println("✅ Static file server running at http://localhost:" + port + "/uploads/avatars/{filename}");
    }
}

