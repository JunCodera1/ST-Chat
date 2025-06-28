package com.stchat.server.service;

import com.stchat.server.dao.ConversationDAO;
import com.stchat.server.model.Conversation;
import com.stchat.server.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class ConversationService {

    public static List<Conversation> getConversationsForUser(int userId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            ConversationDAO dao = new ConversationDAO(conn);
            return dao.getConversationsByUserId(userId);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get conversations", e);
        }
    }

    public static Conversation createConversation(Conversation conv) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            ConversationDAO dao = new ConversationDAO(conn);

            Timestamp now = new Timestamp(System.currentTimeMillis());
            conv.setCreatedAt(now);
            conv.setUpdatedAt(now);
            return dao.createConversation(conv);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create conversation", e);
        }
    }
}
