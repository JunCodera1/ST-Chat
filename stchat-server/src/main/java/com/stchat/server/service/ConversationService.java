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

    public static Conversation getConversationById(int id) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            ConversationDAO dao = new ConversationDAO(conn);
            return dao.getConversationById(id);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get conversation by ID", e);
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

    public static Conversation getOrCreatePrivateConversation(int user1, int user2) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            ConversationDAO dao = new ConversationDAO(conn);

            Conversation existing = dao.findPrivateConversationBetween(user1, user2);
            if (existing != null) return existing;

            Timestamp now = new Timestamp(System.currentTimeMillis());

            Conversation conv = new Conversation();
            conv.setType(Conversation.ConversationType.PRIVATE);
            conv.setCreatedBy(user1); // hoặc user2, tùy bạn
            conv.setCreatedAt(now);
            conv.setUpdatedAt(now);

            Conversation created = dao.createConversation(conv);
            dao.addMembers(created.getId(), List.of(user1, user2));

            return created;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get or create private conversation", e);
        }
    }

    public static boolean addMembers(int conversationId, List<Integer> memberIds) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            ConversationDAO dao = new ConversationDAO(conn);
            return dao.addMembers(conversationId, memberIds);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add members", e);
        }
    }

    public static Conversation updateConversationInfo(int id, Conversation info) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            ConversationDAO dao = new ConversationDAO(conn);
            Conversation existing = dao.getConversationById(id);
            if (existing == null) return null;

            existing.setName(info.getName());
            existing.setDescription(info.getDescription());
            existing.setAvatarUrl(info.getAvatarUrl());
            existing.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

            dao.updateConversation(existing);
            return existing;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update conversation", e);
        }
    }

    public static List<String> getMedia(int conversationId, String type) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            ConversationDAO dao = new ConversationDAO(conn);
            return dao.getMediaUrls(conversationId, type);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get media", e);
        }
    }

}
