package com.stchat.server.dao;

import com.stchat.server.database.DatabaseConnection;
import com.stchat.server.model.User;
import com.stchat.server.util.EmailSender;
import com.stchat.server.util.PasswordGenerator;
import com.stchat.server.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class UserDAO {
    public static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());

    public boolean registerUser(String username, String email, String password, String firstName, String lastName) {
        String sql = "INSERT INTO users (username, email, password_hash, first_name, last_name, created_at) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

        // Kiểm tra trước khi tạo kết nối
        if (isUsernameExists(username)) {
            LOGGER.warning("Username already exists: " + username);
            return false;
        }

        if (isEmailExists(email)) {
            LOGGER.warning("Email already exists: " + email);
            return false;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String hashedPassword = PasswordUtil.hashPassword(password);

            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setString(3, hashedPassword);
            pstmt.setString(4, firstName);
            pstmt.setString(5, lastName);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                LOGGER.info("Register success: " + username);
                return true;
            }

        } catch (SQLException e) {
            LOGGER.severe("Error occurred during register: " + e.getMessage());
        }

        return false;
    }


    public User getUserByUsername(String username) {
        String sql = "SELECT id, username, email, created_at, first_name, last_name FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getTimestamp("created_at")
                );
            }

        } catch (SQLException e) {
            LOGGER.severe("Error occurred when get user: " + e.getMessage());
        }

        return null;
    }


    public User getUserByEmail(String email) {
        String sql = "SELECT id, username, first_name, last_name, email, created_at FROM users WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getTimestamp("created_at")
                );

            }

        } catch (SQLException e) {
            LOGGER.severe("Error occurred when get user: " + e.getMessage());
        }

        return null;
    }

    public Optional<User> getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getTimestamp("created_at")
                );

                return Optional.of(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }


    public boolean isUsernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            LOGGER.severe("Error occurred when check username: " + e.getMessage());
        }

        return false;
    }

    public boolean isEmailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            LOGGER.severe("Error occurred when checking email: " + e.getMessage());
        }

        return false;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, first_name, last_name, email, created_at FROM users ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getTimestamp("created_at")
                );

                users.add(user);
            }

        } catch (SQLException e) {
            LOGGER.severe("Error occurred when get list user: " + e.getMessage());
        }

        return users;
    }

    public boolean updateUser(int userId, String username, String email) {
        String sql = "UPDATE users SET username = ?, email = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setInt(3, userId);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                LOGGER.info("Update success : " + userId);
                return true;
            }

        } catch (SQLException e) {
            LOGGER.severe("Error when update user: " + e.getMessage());
        }

        return false;
    }

    public String getUsernameByEmail(String email) {
        String sql = "SELECT username FROM users WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("username");
            }
        } catch (SQLException e) {
            LOGGER.severe("Error getting username by email: " + e.getMessage());
        }
        return null;
    }


    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                LOGGER.info("Delete success user: " + userId);
                return true;
            }

        } catch (SQLException e) {
            LOGGER.severe("Error is occurred when deleting a user : " + e.getMessage());
        }

        return false;
    }

    public String resetPassword(String email) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT username FROM users WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String username = rs.getString("username");

                String newPassword = PasswordGenerator.generate(10);
                String hashedPassword = PasswordUtil.hashPassword(newPassword);

                String update = "UPDATE users SET password_hash = ? WHERE email = ?";
                PreparedStatement updateStmt = conn.prepareStatement(update);
                updateStmt.setString(1, hashedPassword);
                updateStmt.setString(2, email);
                updateStmt.executeUpdate();

                String subject = "ST Chat - Mật khẩu mới";
                String content = "Xin chào " + username + ",\n\nMật khẩu mới của bạn là: " + newPassword + "\nHãy đăng nhập và thay đổi mật khẩu ngay.";

                boolean emailSent = EmailSender.send(email, subject, content);
                if (emailSent) {
                    return "Mật khẩu mới đã được gửi tới email: " + email;
                } else {
                    return "Đặt lại mật khẩu thành công, nhưng không gửi được email.";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getUserCount() {
        String sql = "SELECT COUNT(*) FROM users";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            LOGGER.severe("Error occured when counting user: " + e.getMessage());
        }

        return 0;
    }

    public boolean updatePassword(String email, String hashed) {
        String sql = "UPDATE users SET password_hash = ?, updated_at = CURRENT_TIMESTAMP WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, hashed);
            pstmt.setString(2, email);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            LOGGER.severe("Failed to update password for email: " + email + " - " + e.getMessage());
        }

        return false;
    }

    public boolean updateAvatarUrl(int userId, String avatarUrl) {
        String sql = "UPDATE users SET avatar_url = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, avatarUrl);
            pstmt.setInt(2, userId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                LOGGER.info("Avatar updated for user ID: " + userId);
                return true;
            }

        } catch (SQLException e) {
            LOGGER.severe("Failed to update avatar for user ID: " + userId + " - " + e.getMessage());
        }

        return false;
    }

}