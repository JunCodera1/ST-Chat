package me.chatapp.stchat.util;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class ValidateUtil {
    public static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    public static boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static int calculatePasswordStrength(@NotNull String password) {
        if (password.isEmpty()) return 0;

        int score = 0;
        if (password.length() >= 8) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[0-9].*")) score++;
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) score++;

        return Math.min(score, 4);
    }

    public static String getValidationError(
            String email, String currentPassword, String newPassword, String confirmPassword) {

        if (email.isEmpty() || currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            return "Please fill in all fields";
        }

        if (!isValidEmail(email)) {
            return "Please enter a valid email address";
        }

        if (newPassword.length() < 6) {
            return "New password must be at least 6 characters";
        }

        if (!newPassword.equals(confirmPassword)) {
            return "New passwords do not match";
        }

        if (currentPassword.equals(newPassword)) {
            return "New password must be different from current password";
        }

        return null; // hợp lệ
    }

    public static String validateRegister(String username, String email, String password, String confirmPassword) {
        if (username == null || email == null || password == null || confirmPassword == null ||
                username.trim().isEmpty() || email.trim().isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            return "Please fill in all fields";
        }

        if (username.length() < 3) {
            return "Username must be at least 3 characters";
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return "Please enter a valid email address";
        }

        if (password.length() < 6) {
            return "Password must be at least 6 characters";
        }

        if (!password.equals(confirmPassword)) {
            return "Passwords do not match";
        }

        return null; // ✅ hợp lệ
    }
}
