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
    private final PendingPasswordChangeDAO pendingDAO;

    public PasswordService(UserDAO userDAO, PendingPasswordChangeDAO pendingDAO) {
        this.userDAO = userDAO;
        this.pendingDAO = pendingDAO;
    }

    public boolean changePassword(String email, String oldPassword, String newPassword) {
        User user = userDAO.getUserByEmail(email);
        if (user == null || !PasswordUtil.matchPassword(oldPassword, user.getPasswordHash())) {
            LOGGER.warning("Invalid credentials for email: " + email);
            return false;
        }

        String hashedNewPassword = PasswordUtil.hashPassword(newPassword);
        boolean updated = userDAO.updatePassword(email, hashedNewPassword);

        if (updated) {
            String subject = "ST Chat - Mật khẩu đã thay đổi";
            String content = "Xin chào " + user.getUsername() + ",\n\nMật khẩu của bạn đã được thay đổi thành công.\n"
                    + "Nếu bạn không thực hiện hành động này, vui lòng liên hệ hỗ trợ ngay.";
            EmailSender.send(email, subject, content);
            return true;
        }

        return false;
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
            // Xóa yêu cầu cũ nếu có
            PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM pending_password_changes WHERE email = ?");
            deleteStmt.setString(1, email);
            deleteStmt.executeUpdate();

            // Thêm yêu cầu mới
            PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO pending_password_changes (email, new_password_hash, token) VALUES (?, ?, ?)");
            insertStmt.setString(1, email);
            insertStmt.setString(2, hashedNewPassword);
            insertStmt.setString(3, token);
            insertStmt.executeUpdate();

            // Gửi email xác nhận
            String confirmLink = "http://localhost:9090/api/confirm-password-change?token=" + token;
            String subject = "ST Chat - Xác nhận thay đổi mật khẩu";
            String content = "Xin chào " + username + ",\n\n"
                    + "Bạn đã yêu cầu đổi mật khẩu. Nhấn vào nút dưới đây để xác nhận:\n\n"
                    + "<a href=\"" + confirmLink + "\" style=\"padding:10px 20px; background-color:#667eea; color:white; border-radius:5px; text-decoration:none;\">Xác nhận đổi mật khẩu</a>\n\n"
                    + "Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email.";

            return EmailSender.sendHtml(email, subject, content); // sendHtml là hàm gửi HTML
        } catch (SQLException e) {
            LOGGER.severe("Error storing password change request: " + e.getMessage());
            return false;
        }
    }

    public static boolean confirmPasswordChange(String token) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement selectStmt = conn.prepareStatement("SELECT email, new_password_hash FROM pending_password_changes WHERE token = ?");
            selectStmt.setString(1, token);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                String email = rs.getString("email");
                String newHashedPassword = rs.getString("new_password_hash");

                // Cập nhật mật khẩu thật sự
                PreparedStatement updateStmt = conn.prepareStatement("UPDATE users SET password_hash = ? WHERE email = ?");
                updateStmt.setString(1, newHashedPassword);
                updateStmt.setString(2, email);
                int updated = updateStmt.executeUpdate();

                if (updated > 0) {
                    PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM pending_password_changes WHERE email = ?");
                    deleteStmt.setString(1, email);
                    deleteStmt.executeUpdate();

                    LOGGER.info("Password changed successfully after confirmation: " + email);
                    return true;
                }
            }
        } catch (SQLException e) {
            LOGGER.severe("Error confirming password change: " + e.getMessage());
        }

        return false;
    }

    public String resetPassword(String email) {
        User user = userDAO.getUserByEmail(email);
        if (user == null) return null;

        String newPassword = PasswordGenerator.generate(10);
        String hashed = PasswordUtil.hashPassword(newPassword);

        if (userDAO.updatePassword(email, hashed)) {
            String subject = "ST Chat - Mật khẩu mới";
            String content = "Xin chào " + user.getUsername() + ",\n\nMật khẩu mới của bạn là: " + newPassword + "\nHãy đăng nhập và thay đổi mật khẩu ngay.";
            if (EmailSender.send(email, subject, content)) {
                return "Mật khẩu mới đã được gửi tới email: " + email;
            } else {
                return "Đặt lại mật khẩu thành công, nhưng không gửi được email.";
            }
        }
        return null;
    }
}