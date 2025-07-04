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
                "JOIN conversation_participants cm ON c.id = cm.conversation_id " +
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

    public boolean addParticipantToConversation(int convId, int userId) throws SQLException {
        String sql = "INSERT INTO conversation_participants (conversation_id, user_id) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, convId);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    public Conversation getConversationById(int id) throws SQLException {
        String sql = "SELECT * FROM conversations WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToConversation(rs);
            }
        }

        return null; // Không tìm thấy
    }


    private Conversation mapResultSetToConversation(ResultSet rs) throws SQLException {
        String typeStr = rs.getString("type");
        Conversation.ConversationType type;

        try {
            type = Conversation.ConversationType.valueOf(typeStr);
        } catch (IllegalArgumentException | NullPointerException e) {
            type = Conversation.ConversationType.PRIVATE;
        }

        return new Conversation(
                rs.getInt("id"),
                rs.getString("name"),
                type,
                rs.getString("avatar_url"),
                rs.getString("description"),
                rs.getBoolean("is_archived"),
                rs.getInt("created_by"),
                rs.getTimestamp("created_at"),
                rs.getTimestamp("updated_at")
        );
    }

    public boolean createConversationWithCreator(Conversation conv, int creatorId) {
        String createSql = "INSERT INTO conversations (name, type, avatar_url, description, is_archived, created_by, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String memberSql = "INSERT INTO conversation_members (conversation_id, user_id) VALUES (?, ?)";

        try {
            connection.setAutoCommit(false); // Bắt đầu transaction

            // Tạo conversation
            try (PreparedStatement stmt = connection.prepareStatement(createSql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, conv.getName());
                stmt.setString(2, conv.getType().name());
                stmt.setString(3, conv.getAvatarUrl());
                stmt.setString(4, conv.getDescription());
                stmt.setBoolean(5, conv.isArchived());
                stmt.setInt(6, creatorId);
                stmt.setTimestamp(7, conv.getCreatedAt());
                stmt.setTimestamp(8, conv.getUpdatedAt());
                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int convId = rs.getInt(1);
                    conv.setId(convId);

                    // Thêm người tạo làm thành viên
                    try (PreparedStatement memberStmt = connection.prepareStatement(memberSql)) {
                        memberStmt.setInt(1, convId);
                        memberStmt.setInt(2, creatorId);
                        memberStmt.executeUpdate();
                    }
                }
            }

            connection.commit(); // Thành công
            return true;

        } catch (SQLException e) {
            try {
                connection.rollback(); // Nếu lỗi, rollback lại mọi thao tác
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true); // Trả lại trạng thái mặc định
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public Conversation findPrivateConversationBetween(int userId1, int userId2) throws SQLException {
        String sql = """
        SELECT c.*
        FROM conversations c
        JOIN conversation_participants cp1 ON c.id = cp1.conversation_id
        JOIN conversation_participants cp2 ON c.id = cp2.conversation_id
        WHERE c.type = 'PRIVATE'
          AND cp1.user_id = ?
          AND cp2.user_id = ?
        GROUP BY c.id
        HAVING COUNT(*) = 2
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId1);
            stmt.setInt(2, userId2);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToConversation(rs);
            }
        }
        return null;
    }

    public boolean addMembers(int conversationId, List<Integer> memberIds) throws SQLException {
        String sql = "INSERT INTO conversation_participants (conversation_id, user_id) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (int userId : memberIds) {
                stmt.setInt(1, conversationId);
                stmt.setInt(2, userId);
                stmt.addBatch();
            }
            stmt.executeBatch();
            return true;
        }
    }

    public void updateConversation(Conversation conv) throws SQLException {
        String sql = """
        UPDATE conversations
        SET name = ?, avatar_url = ?, description = ?, updated_at = ?
        WHERE id = ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, conv.getName());
            stmt.setString(2, conv.getAvatarUrl());
            stmt.setString(3, conv.getDescription());
            stmt.setTimestamp(4, conv.getUpdatedAt());
            stmt.setInt(5, conv.getId());
            stmt.executeUpdate();
        }
    }

    public List<String> getMediaUrls(int conversationId, String type) throws SQLException {
        List<String> urls = new ArrayList<>();

        String sql = """
        SELECT file_url
        FROM messages
        WHERE conversation_id = ?
          AND file_url IS NOT NULL
          AND message_type = ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, conversationId);
            stmt.setString(2, type.toUpperCase()); // IMAGE, VIDEO, FILE, LINK...
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                urls.add(rs.getString("file_url"));
            }
        }

        return urls;
    }


}
