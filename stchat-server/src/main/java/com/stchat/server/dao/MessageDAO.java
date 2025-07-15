package com.stchat.server.dao;

import com.stchat.server.database.DatabaseConnection;
import com.stchat.server.model.Message;
import com.stchat.server.model.Message.MessageType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MessageDAO {
    private static final Logger LOGGER = Logger.getLogger(MessageDAO.class.getName());

    public List<Message> getMessagesByConversationId(int conversationId) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM message WHERE conversation_id = ? ORDER BY created_at ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, conversationId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                messages.add(extractMessageFromResultSet(rs));
            }

        } catch (SQLException e) {
            LOGGER.severe("Failed to fetch messages: " + e.getMessage());
        }

        return messages;
    }

    // Thêm tin nhắn mới
    public boolean addMessage(Message message) {
        String sql = "INSERT INTO message (conversation_id, sender_id, content, message_type, reply_to_message_id, " +
                "file_url, file_name, file_size, is_edited, is_deleted, is_pinned, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, message.getConversationId());
            pstmt.setInt(2, message.getSenderId());
            pstmt.setString(3, message.getContent());
            pstmt.setString(4, message.getMessageType() != null ? message.getMessageType().name() : null);
            pstmt.setObject(5, message.getReplyToMessageId(), Types.INTEGER);
            pstmt.setString(6, message.getFileUrl());
            pstmt.setString(7, message.getFileName());
            pstmt.setInt(8, message.getFileSize());
            pstmt.setBoolean(9, message.isEdited());
            pstmt.setBoolean(10, message.isDeleted());
            pstmt.setBoolean(11, message.isPinned());
            pstmt.setTimestamp(12, message.getCreatedAt());
            pstmt.setTimestamp(13, message.getUpdatedAt());
            System.out.println("[DEBUG] INSERTING message with conversation_id = " + message.getConversationId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.severe("Failed to insert message: " + e.getMessage());
        }

        return false;
    }

    // Xóa mềm tin nhắn (is_deleted = true)
    public boolean softDeleteMessage(int messageId) {
        String sql = "UPDATE message SET is_deleted = TRUE, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, messageId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.severe("Failed to soft delete message: " + e.getMessage());
        }

        return false;
    }

    // Cập nhật nội dung tin nhắn
    public boolean updateMessageContent(int messageId, String newContent) {
        String sql = "UPDATE message SET content = ?, is_edited = TRUE, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newContent);
            pstmt.setInt(2, messageId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.severe("Failed to update message: " + e.getMessage());
        }

        return false;
    }

    // Ghim hoặc bỏ ghim tin nhắn
    public boolean togglePinMessage(int messageId, boolean pinned) {
        String sql = "UPDATE message SET is_pinned = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBoolean(1, pinned);
            pstmt.setInt(2, messageId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.severe("Failed to toggle pin: " + e.getMessage());
        }

        return false;
    }

    // Helper để parse ResultSet thành Message object
    private Message extractMessageFromResultSet(ResultSet rs) throws SQLException {
        Message msg = new Message();
        msg.setId(rs.getInt("id"));
        msg.setConversationId(rs.getInt("conversation_id"));
        msg.setSenderId(rs.getInt("sender_id"));
        msg.setContent(rs.getString("content"));

        String type = rs.getString("message_type");
        if (type != null) {
            try {
                msg.setMessageType(MessageType.valueOf(type));
            } catch (IllegalArgumentException e) {
                LOGGER.warning("Unknown message type: " + type);
            }
        }

        msg.setReplyToMessageId(rs.getObject("reply_to_message_id") != null ? rs.getInt("reply_to_message_id") : null);
        msg.setFileUrl(rs.getString("file_url"));
        msg.setFileName(rs.getString("file_name"));
        msg.setFileSize(rs.getInt("file_size"));
        msg.setEdited(rs.getBoolean("is_edited"));
        msg.setDeleted(rs.getBoolean("is_deleted"));
        msg.setPinned(rs.getBoolean("is_pinned"));
        msg.setCreatedAt(rs.getTimestamp("created_at"));
        msg.setUpdatedAt(rs.getTimestamp("updated_at"));



        return msg;
    }

    public List<Message> getMessagesByType(int conversationId, MessageType type) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM message WHERE conversation_id = ? AND message_type = ? AND is_deleted = false ORDER BY created_at ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, conversationId);
            pstmt.setString(2, type.name());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                messages.add(extractMessageFromResultSet(rs));
            }

        } catch (SQLException e) {
            LOGGER.severe("Failed to fetch messages by type: " + e.getMessage());
        }

        return messages;
    }

    public List<Message> getPinnedMessages(int conversationId) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM message WHERE conversation_id = ? AND is_pinned = true AND is_deleted = false ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, conversationId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                messages.add(extractMessageFromResultSet(rs));
            }

        } catch (SQLException e) {
            LOGGER.severe("Failed to fetch pinned messages: " + e.getMessage());
        }

        return messages;
    }

    public List<Message> searchMessages(int conversationId, String keyword) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM message WHERE conversation_id = ? AND content LIKE ? AND is_deleted = false ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, conversationId);
            pstmt.setString(2, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                messages.add(extractMessageFromResultSet(rs));
            }

        } catch (SQLException e) {
            LOGGER.severe("Failed to search messages: " + e.getMessage());
        }

        return messages;
    }

    public List<Message> getMessagesByTimeRange(int conversationId, Timestamp from, Timestamp to) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM message WHERE conversation_id = ? AND created_at BETWEEN ? AND ? AND is_deleted = false ORDER BY created_at ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, conversationId);
            pstmt.setTimestamp(2, from);
            pstmt.setTimestamp(3, to);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                messages.add(extractMessageFromResultSet(rs));
            }

        } catch (SQLException e) {
            LOGGER.severe("Failed to fetch messages by time range: " + e.getMessage());
        }

        return messages;
    }

}
