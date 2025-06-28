package com.stchat.server.service;

import com.stchat.server.dao.MessageDAO;
import com.stchat.server.model.Message;

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
}
