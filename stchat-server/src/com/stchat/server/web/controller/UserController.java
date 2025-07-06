package com.stchat.server.web.controller;

import com.stchat.server.dao.UserDAO;
import com.stchat.server.model.User;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;

public class UserController {
    private static final UserDAO userDAO = new UserDAO();

    public static void registerRoutes(Javalin app) {
        app.post("/api/users/register", UserController::registerUser);
        app.get("/api/users", UserController::getAllUsers);
        app.get("/api/users/{username}", UserController::getUserByUsername);
        app.delete("/api/users/{id}", UserController::deleteUser);
        app.put("/api/users/{id}", UserController::updateUser);
        app.post("/api/users/reset-password", UserController::resetPassword);
        app.post("/api/users/change-password", UserController::updatePassword);
        app.get("/api/users-count", UserController::getUserCount);
    }

    private static void registerUser(Context ctx) {
        try {
            Map<String, String> body = ctx.bodyAsClass(Map.class);
            String username = body.get("username");
            String email = body.get("email");
            String password = body.get("password");
            String firstName = body.get("firstName");
            String lastName = body.get("lastName");

            boolean success = userDAO.registerUser(username, email, password, firstName, lastName);

            if (success) ctx.status(201).result("User registered successfully");
            else ctx.status(400).result("Username or email already exists");

        } catch (Exception e) {
            ctx.status(500).result("Registration error: " + e.getMessage());
        }
    }

    private static void getAllUsers(Context ctx) {
        List<User> users = userDAO.getAllUsers();
        ctx.json(users);
    }

    private static void getUserByUsername(Context ctx) {
        String username = ctx.pathParam("username");
        User user = userDAO.getUserByUsername(username);
        if (user != null) ctx.json(user);
        else ctx.status(404).result("User not found");
    }

    private static void deleteUser(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        boolean deleted = userDAO.deleteUser(id);
        if (deleted) ctx.result("User deleted successfully");
        else ctx.status(404).result("User not found");
    }

    private static void updateUser(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        Map<String, String> body = ctx.bodyAsClass(Map.class);
        String username = body.get("username");
        String email = body.get("email");

        boolean updated = userDAO.updateUser(id, username, email);
        if (updated) ctx.result("User updated successfully");
        else ctx.status(400).result("Update failed");
    }

    private static void resetPassword(Context ctx) {
        Map<String, String> body = ctx.bodyAsClass(Map.class);
        String email = body.get("email");

        String result = userDAO.resetPassword(email);
        if (result != null) ctx.result(result);
        else ctx.status(400).result("Unable to reset password");
    }

    private static void updatePassword(Context ctx) {
        Map<String, String> body = ctx.bodyAsClass(Map.class);
        String email = body.get("email");
        String newPassword = body.get("newPassword");

        String hashed = com.stchat.server.util.PasswordUtil.hashPassword(newPassword);
        boolean updated = userDAO.updatePassword(email, hashed);

        if (updated) ctx.result("Password updated successfully");
        else ctx.status(400).result("Failed to update password");
    }

    private static void getUserCount(Context ctx) {
        int count = userDAO.getUserCount();
        ctx.json(Map.of("userCount", count));
    }
}
