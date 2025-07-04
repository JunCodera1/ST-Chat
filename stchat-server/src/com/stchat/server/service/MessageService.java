package com.stchat.server.service;

import com.stchat.server.dao.MessageDAO;
import com.stchat.server.model.Message;

import java.sql.Timestamp;
import java.util.List;

public class MessageService {
    private final MessageDAO messageDAO = new MessageDAO();

    public List<Message> getMessages(int conversationId) {
        return messageDAO.getMessagesByConversationId(conversationId);
    }

    public boolean sendMessage(Message msg) {
        return messageDAO.addMessage(msg);
    }

    public boolean editMessage(int id, String content) {
        return messageDAO.updateMessageContent(id, content);
    }

    public boolean deleteMessage(int id) {
        return messageDAO.softDeleteMessage(id);
    }

    public boolean pinMessage(int id, boolean pin) {
        return messageDAO.togglePinMessage(id, pin);
    }

    public boolean replyToMessage(Message replyMsg) {
        return messageDAO.addMessage(replyMsg); // replyMsg có set replyToMessageId
    }

    public boolean sendFileMessage(Message msg) {
        // set messageType = FILE, IMAGE, LINK
        return messageDAO.addMessage(msg);
    }

    public List<Message> getMedia(int conversationId, Message.MessageType type) {
        return messageDAO.getMessagesByType(conversationId, type);
    }

    public List<Message> searchMessages(int conversationId, String keyword) {
        return messageDAO.searchMessages(conversationId, keyword);
    }

    public List<Message> getPinnedMessages(int conversationId) {
        return messageDAO.getPinnedMessages(conversationId);
    }

    public List<Message> getMessagesByTimeRange(int conversationId, Timestamp start, Timestamp end) {
        return messageDAO.getMessagesByTimeRange(conversationId, start, end);
    }
}
