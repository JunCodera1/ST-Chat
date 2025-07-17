package com.stchat.server.service;

import com.stchat.server.database.DatabaseConnection;
import com.stchat.server.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.stchat.server.dao.UserDAO.LOGGER;

public class AuthService {
    public static boolean authenticateUser(String identifier, String password) {
        String sql = "SELECT password_hash FROM users WHERE username = ? OR email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, identifier);
            pstmt.setString(2, identifier);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");

                if (PasswordUtil.matchPassword(password, storedHash)) {
                    LOGGER.info("Login success: " + identifier);
                    return true;
                } else {
                    LOGGER.warning("Password is not correct: " + identifier);
                }
            } else {
                LOGGER.warning("Not found user: " + identifier);
            }

        } catch (SQLException e) {
            LOGGER.severe("Error when authenticating: " + e.getMessage());
        }

        return false;
    }
}
