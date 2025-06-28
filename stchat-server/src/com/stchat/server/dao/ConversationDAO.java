package com.stchat.server.dao;

import com.stchat.server.model.Conversation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConversationDAO {
    private final Connection connection;

    public ConversationDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Conversation> getConversationsByUserId(int userId) throws SQLException {
        List<Conversation> conversations = new ArrayList<>();
        String sql = "SELECT c.* FROM conversations c " +
                "JOIN conversation_members cm ON c.id = cm.conversation_id " +
                "WHERE cm.user_id = ? AND c.is_archived = false";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                conversations.add(mapResultSetToConversation(rs));
            }
        }

        return conversations;
    }

    public Conversation createConversation(Conversation conv) throws SQLException {
        String sql = "INSERT INTO conversations (name, type, avatar_url, description, is_archived, created_by, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, conv.getName());
            stmt.setString(2, conv.getType().name());
            stmt.setString(3, conv.getAvatarUrl());
            stmt.setString(4, conv.getDescription());
            stmt.setBoolean(5, conv.isArchived());
            stmt.setInt(6, conv.getCreatedBy());
            stmt.setTimestamp(7, conv.getCreatedAt());
            stmt.setTimestamp(8, conv.getUpdatedAt());

            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                conv.setId(rs.getInt(1));
            }
        }

        return conv;
    }

    private Conversation mapResultSetToConversation(ResultSet rs) throws SQLException {
        return new Conversation(
                rs.getInt("id"),
                rs.getString("name"),
                Conversation.ConversationType.valueOf(rs.getString("type")),
                rs.getString("avatar_url"),
                rs.getString("description"),
                rs.getBoolean("is_archived"),
                rs.getInt("created_by"),
                rs.getTimestamp("created_at"),
                rs.getTimestamp("updated_at")
        );
    }
}
