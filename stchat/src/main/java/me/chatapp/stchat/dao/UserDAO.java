package me.chatapp.stchat.dao;

import me.chatapp.stchat.database.DatabaseConnection;
import me.chatapp.stchat.model.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class UserDAO {
    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());

    /**
     * Đăng ký người dùng mới
     */
    public boolean registerUser(String username, String email, String password) {
        String sql = "INSERT INTO users (username, email, password_hash) VALUES (?, ?, ?)";

        // Kiểm tra trước khi tạo kết nối
        if (isUsernameExists(username)) {
            LOGGER.warning("Username already exist: " + username);
            return false;
        }

        if (isEmailExists(email)) {
            LOGGER.warning("Email already exist: " + email);
            return false;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String hashedPassword = hashPassword(password);
            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setString(3, hashedPassword);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                LOGGER.info("Login success: " + username);
                return true;
            }

        } catch (SQLException e) {
            LOGGER.severe("Error occured when login: " + e.getMessage());
        }

        return false;
    }

    /**
     * Xác thực người dùng đăng nhập
     */
    public boolean authenticateUser(String username, String password) {
        String sql = "SELECT password_hash FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                String inputHash = hashPassword(password);

                if (storedHash.equals(inputHash)) {
                    LOGGER.info("Login success: " + username);
                    return true;
                } else {
                    LOGGER.warning("Password is not correct: " + username);
                }
            } else {
                LOGGER.warning("Not found a user: " + username);
            }

        } catch (SQLException e) {
            LOGGER.severe("Error when authenticating: " + e.getMessage());
        }

        return false;
    }

    /**
     * Get info by username
     */
    public User getUserByUsername(String username) {
        String sql = "SELECT id, username, email, created_at FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getTimestamp("created_at")
                );
            }

        } catch (SQLException e) {
            LOGGER.severe("Error occurred when get user: " + e.getMessage());
        }

        return null;
    }

    /**
     * get info by email
     */
    public User getUserByEmail(String email) {
        String sql = "SELECT id, username, email, created_at FROM users WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getTimestamp("created_at")
                );
            }

        } catch (SQLException e) {
            LOGGER.severe("Error occurred when get user: " + e.getMessage());
        }

        return null;
    }

    /**
     * Username it exists ?
     */
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

    /**
     * Email is already exist ?
     */
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

    /**
     * Get all users
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, email, created_at FROM users ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
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

    /**
     * Update info user
     */
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
            LOGGER.severe("Lỗi khi cập nhật user: " + e.getMessage());
        }

        return false;
    }

    /**
     * Change pass
     */
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        if (!authenticateUser(username, oldPassword)) {
            LOGGER.warning("Old password is not corrected: " + username);
            return false;
        }

        String sql = "UPDATE users SET password_hash = ?, updated_at = CURRENT_TIMESTAMP WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String hashedNewPassword = hashPassword(newPassword);

            pstmt.setString(1, hashedNewPassword);
            pstmt.setString(2, username);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                LOGGER.info("Changed password for " + username);
                return true;
            }

        } catch (SQLException e) {
            LOGGER.severe("Error occurred when changing password:  " + e.getMessage());
        }

        return false;
    }

    /**
     * Delete user
     */
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

    /**
     * Hash mật khẩu bằng SHA-256
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            // Convert byte array to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            LOGGER.severe("Error when hash the password: " + e.getMessage());
            throw new RuntimeException("Can't hash the password", e);
        }
    }

    /**
     * Count user
     */
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
}