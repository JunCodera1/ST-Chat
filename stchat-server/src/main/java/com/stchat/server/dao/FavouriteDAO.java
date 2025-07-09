package com.stchat.server.dao;

import com.stchat.server.database.DatabaseConnection;
import com.stchat.server.model.Favourite;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FavouriteDAO {

    public boolean addFavourite(int userId, int favoriteUserId) {
        String sql = "INSERT INTO favourites (user_id, favorite_user_id, created_at) VALUES (?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, favoriteUserId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeFavourite(int userId, int favoriteUserId) {
        String sql = "DELETE FROM favourites WHERE user_id = ? AND favorite_user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, favoriteUserId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Favourite> getFavoritesByUserId(int userId) {
        List<Favourite> result = new ArrayList<>();
        String sql = "SELECT * FROM favourites WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(new Favourite(
                        rs.getInt("user_id"),
                        rs.getInt("favorite_user_id"),
                        rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Favourite getFavorite(int userId, int favoriteUserId) {
        String sql = "SELECT * FROM favourites WHERE user_id = ? AND favorite_user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, favoriteUserId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Favourite(userId, favoriteUserId, rs.getTimestamp("created_at"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isFavoriteExists(int userId, int favoriteUserId) {
        String sql = "SELECT COUNT(*) FROM favourites WHERE user_id = ? AND favorite_user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, favoriteUserId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

