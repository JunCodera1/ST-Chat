package com.stchat.server.handler;

import com.stchat.server.dao.PendingPasswordChangeDAO;
import com.stchat.server.dao.UserDAO;
import com.stchat.server.service.AuthService;
import com.stchat.server.service.PasswordService;
import org.json.JSONObject;
import com.stchat.server.util.JsonResponseUtil;

public class AuthProcessor {

    public static JSONObject handle(JSONObject request) {
        String type = request.optString("type");
        switch (type.toUpperCase()) {
            case "LOGIN": return handleLogin(request);
            case "REGISTER": return handleRegister(request);
            case "FORGOT_PASSWORD": return handleForgotPassword(request);
            case "CHANGE_PASSWORD": return handleChangePassword(request);
            default: return JsonResponseUtil.error("Unknown auth type.");
        }
    }

    private static JSONObject handleLogin(JSONObject request) {
        String username = request.optString("username");
        String password = request.optString("password");

        if (username.isEmpty() || password.isEmpty()) {
            return JsonResponseUtil.error("Username and password are required.");
        }

        UserDAO userDAO = new UserDAO();
        boolean authenticated = AuthService.authenticateUser(username, password);

        if (authenticated) {
            return new JSONObject()
                    .put("status", "success")
                    .put("username", username);
        } else {
            return JsonResponseUtil.error("Invalid username or password.");
        }
    }

    private static JSONObject handleRegister(JSONObject request) {
        String username = request.optString("username");
        String email = request.optString("email");
        String password = request.optString("password");

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            return JsonResponseUtil.error("Please fill all fields.");
        }

        UserDAO userDAO = new UserDAO();
        boolean registered = userDAO.registerUser(username, email, password);

        if (registered) {
            return JsonResponseUtil.success("Account created successfully.");
        } else {
            return JsonResponseUtil.error("Username or email already exists.");
        }
    }

    private static JSONObject handleForgotPassword(JSONObject request) {
        String email = request.optString("email");

        if (email.isEmpty()) {
            return JsonResponseUtil.error("Email is required.");
        }

        UserDAO userDAO = new UserDAO();
        String resetMessage = userDAO.resetPassword(email);

        if (resetMessage != null) {
            return JsonResponseUtil.success(resetMessage);
        } else {
            return JsonResponseUtil.error("Email not found.");
        }
    }

    private static JSONObject handleChangePassword(JSONObject request) {
        String email = request.optString("email");
        String currentPassword = request.optString("currentPassword");
        String newPassword = request.optString("newPassword");

        if (email.isEmpty() || currentPassword.isEmpty() || newPassword.isEmpty()) {
            return JsonResponseUtil.error("All fields are required.");
        }

        UserDAO userDAO = new UserDAO();
        PendingPasswordChangeDAO pendingDAO = new PendingPasswordChangeDAO();
        PasswordService passwordService = new PasswordService(userDAO, pendingDAO);

        boolean requestSent = passwordService.requestPasswordChange(email, currentPassword, newPassword);

        if (requestSent) {
            return JsonResponseUtil.success("Please check your email to confirm the password change.");
        } else {
            return JsonResponseUtil.error("Invalid current password or user not found.");
        }
    }

}

