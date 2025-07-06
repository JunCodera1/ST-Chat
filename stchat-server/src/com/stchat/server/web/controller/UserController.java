package com.stchat.server.web.controller;

import com.stchat.server.model.User;
import com.stchat.server.service.UserService;
import com.stchat.server.util.PasswordUtil;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UserController {
    private static final UserService userService = new UserService();

    public static void registerRoutes(Javalin app) {
        app.post("/api/users/register", UserController::registerUser);
        app.get("/api/users", UserController::getAllUsers);
        app.get("/api/users/{username}", UserController::getUserByUsername);
        app.delete("/api/users/{id}", UserController::deleteUser);
        app.put("/api/users/{id}", UserController::updateUser);
        app.post("/api/users/reset-password", UserController::resetPassword);
        app.post("/api/users/change-password", UserController::updatePassword);
        app.get("/api/users-count", UserController::getUserCount);
        app.put("/api/users/{id}/avatar", UserController::updateAvatar);
    }

    private static void registerUser(Context ctx) {
        try {
            Map<String, String> body = ctx.bodyAsClass(Map.class);
            String username = body.get("username");
            String email = body.get("email");
            String password = body.get("password");
            String firstName = body.get("firstName");
            String lastName = body.get("lastName");

            boolean success = userService.registerUser(username, email, password, firstName, lastName);

            if (success) ctx.status(201).result("User registered successfully");
            else ctx.status(400).result("Username or email already exists");

        } catch (Exception e) {
            ctx.status(500).result("Registration error: " + e.getMessage());
        }
    }

    private static void getAllUsers(Context ctx) {
        List<User> users = userService.getAllUsers();
        ctx.json(users);
    }

    private static void getUserByUsername(Context ctx) {
        String username = ctx.pathParam("username");
        Optional<User> userOpt = userService.getUserByUsername(username);
        userOpt.ifPresentOrElse(
                user -> ctx.json(user),
                () -> ctx.status(404).result("User not found")
        );
    }

    private static void deleteUser(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        boolean deleted = userService.deleteUser(id);
        if (deleted) ctx.result("User deleted successfully");
        else ctx.status(404).result("User not found");
    }

    private static void updateUser(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        Map<String, String> body = ctx.bodyAsClass(Map.class);
        String username = body.get("username");
        String email = body.get("email");

        boolean updated = userService.updateUser(id, username, email);
        if (updated) ctx.result("User updated successfully");
        else ctx.status(400).result("Update failed");
    }

    private static void resetPassword(Context ctx) {
        Map<String, String> body = ctx.bodyAsClass(Map.class);
        String email = body.get("email");

        String result = userService.resetPassword(email);
        if (result != null) ctx.result(result);
        else ctx.status(400).result("Unable to reset password");
    }

    private static void updatePassword(Context ctx) {
        Map<String, String> body = ctx.bodyAsClass(Map.class);
        String email = body.get("email");
        String newPassword = body.get("newPassword");

        String hashed = PasswordUtil.hashPassword(newPassword);
        boolean updated = userService.updatePassword(email, hashed);

        if (updated) ctx.result("Password updated successfully");
        else ctx.status(400).result("Failed to update password");
    }

    private static void getUserCount(Context ctx) {
        int count = userService.getUserCount();
        ctx.json(Map.of("userCount", count));
    }

    private static void updateAvatar(Context ctx) {
        int userId = Integer.parseInt(ctx.pathParam("id"));
        Map<String, String> body = ctx.bodyAsClass(Map.class);
        String avatarUrl = body.get("avatarUrl");

        boolean updated = userService.updateAvatar(userId, avatarUrl);

        if (updated) ctx.result("Avatar updated successfully");
        else ctx.status(400).result("Failed to update avatar");
    }

}
