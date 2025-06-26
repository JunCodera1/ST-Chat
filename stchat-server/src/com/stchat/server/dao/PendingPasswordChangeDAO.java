package com.stchat.server.dao;

import com.stchat.server.database.DatabaseConnection;
import com.stchat.server.model.PendingPasswordChange;

import java.sql.*;
import java.util.Optional;
import java.util.logging.Logger;

public class PendingPasswordChangeDAO {
    private static final Logger LOGGER = Logger.getLogger(PendingPasswordChangeDAO.class.getName());

    public boolean save(String email, String hashedPassword, String token) {
        String deleteSql = "DELETE FROM pending_password_changes WHERE email = ?";
        String insertSql = "INSERT INTO pending_password_changes (email, new_password_hash, token) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (
                    PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
                    PreparedStatement insertStmt = conn.prepareStatement(insertSql)
            ) {
                // Xoá yêu cầu cũ
                deleteStmt.setString(1, email);
                deleteStmt.executeUpdate();

                // Thêm yêu cầu mới
                insertStmt.setString(1, email);
                insertStmt.setString(2, hashedPassword);
                insertStmt.setString(3, token);
                insertStmt.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                LOGGER.severe("Failed to save pending password change: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            LOGGER.severe("Database error in save(): " + e.getMessage());
        }

        return false;
    }

    public Optional<PendingPasswordChange> findByToken(String token) {
        String sql = "SELECT email, new_password_hash FROM pending_password_changes WHERE token = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String email = rs.getString("email");
                String hashed = rs.getString("new_password_hash");
                return Optional.of(new PendingPasswordChange(email, hashed, token));
            }

        } catch (SQLException e) {
            LOGGER.severe("Failed to retrieve pending change by token: " + e.getMessage());
        }

        return Optional.empty();
    }

    public boolean deleteByEmail(String email) {
        String sql = "DELETE FROM pending_password_changes WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.severe("Failed to delete pending password change: " + e.getMessage());
        }

        return false;
    }
}
