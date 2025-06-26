package com.stchat.server.service;

import com.stchat.server.dao.UserDAO;
import com.stchat.server.database.DatabaseConnection;
import com.stchat.server.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthService {
    public static boolean authenticateUser(String username, String password) {
        String sql = "SELECT password_hash FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                String inputHash = PasswordUtil.hashPassword(password);

                if (storedHash.equals(inputHash)) {
                    UserDAO.LOGGER.info("Login success: " + username);
                    return true;
                } else {
                    UserDAO.LOGGER.warning("Password is not correct: " + username);
                }
            } else {
                UserDAO.LOGGER.warning("Not found a user: " + username);
            }

        } catch (SQLException e) {
            UserDAO.LOGGER.severe("Error when authenticating: " + e.getMessage());
        }

        return false;
    }
}
