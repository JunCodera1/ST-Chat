package com.stchat.server.service;

import com.stchat.server.dao.PendingPasswordChangeDAO;
import com.stchat.server.dao.UserDAO;
import com.stchat.server.database.DatabaseConnection;
import com.stchat.server.model.User;
import com.stchat.server.util.EmailSender;
import com.stchat.server.util.PasswordGenerator;
import com.stchat.server.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Logger;

public class PasswordService {
    private static final Logger LOGGER = Logger.getLogger(PasswordService.class.getName());

    private final UserDAO userDAO;

    public PasswordService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public boolean requestPasswordChange(String email, String oldPassword, String newPassword) {
        String username = userDAO.getUsernameByEmail(email);
        if (username == null || !AuthService.authenticateUser(username, oldPassword)) {
            LOGGER.warning("Invalid credentials for email: " + email);
            return false;
        }

        String token = UUID.randomUUID().toString();
        String hashedNewPassword = PasswordUtil.hashPassword(newPassword);

        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM pending_password_changes WHERE email = ?");
            deleteStmt.setString(1, email);
            deleteStmt.executeUpdate();

            PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO pending_password_changes (email, new_password_hash, token) VALUES (?, ?, ?)");
            insertStmt.setString(1, email);
            insertStmt.setString(2, hashedNewPassword);
            insertStmt.setString(3, token);
            insertStmt.executeUpdate();

            String confirmLink = "http://localhost:9090/api/confirm-password-change?token=" + token;
            String subject = "ST Chat - Xác nhận thay đổi mật khẩu";
            String content = "Xin chào " + username + ",\n\n"
                    + "Bạn đã yêu cầu đổi mật khẩu. Nhấn vào nút dưới đây để xác nhận:\n\n"
                    + "<a href=\"" + confirmLink + "\" style=\"padding:10px 20px; background-color:#667eea; color:white; border-radius:5px; text-decoration:none;\">Xác nhận đổi mật khẩu</a>\n\n"
                    + "Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email.";

            return EmailSender.sendHtml(email, subject, content);
        } catch (SQLException e) {
            LOGGER.severe("Error storing password change request: " + e.getMessage());
            return false;
        }
    }

    public static void confirmPasswordChange(String token) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement selectStmt = conn.prepareStatement("SELECT email, new_password_hash FROM pending_password_changes WHERE token = ?");
            selectStmt.setString(1, token);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                String email = rs.getString("email");
                String newHashedPassword = rs.getString("new_password_hash");

                PreparedStatement updateStmt = conn.prepareStatement("UPDATE users SET password_hash = ? WHERE email = ?");
                updateStmt.setString(1, newHashedPassword);
                updateStmt.setString(2, email);
                int updated = updateStmt.executeUpdate();

                if (updated > 0) {
                    PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM pending_password_changes WHERE email = ?");
                    deleteStmt.setString(1, email);
                    deleteStmt.executeUpdate();

                    LOGGER.info("Password changed successfully after confirmation: " + email);
                }
            }
        } catch (SQLException e) {
            LOGGER.severe("Error confirming password change: " + e.getMessage());
        }

    }
}