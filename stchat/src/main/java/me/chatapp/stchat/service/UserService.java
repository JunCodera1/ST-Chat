package me.chatapp.stchat.service;

import me.chatapp.stchat.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserService {
    public static List<String> fetchAllUsernames() {
        List<String> usernames = new ArrayList<>();
        String query = "SELECT username FROM users"; // bảng 'users'

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                usernames.add(rs.getString("username"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return usernames;
    }
}

