package com.stchat.server.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stchat.server.model.Message;
import com.stchat.server.service.MessageService;
import org.eclipse.jetty.websocket.api.Session;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class WebSocketManager {
    private static final Map<Integer, Session> userSessions = new ConcurrentHashMap<>();
    private static final Map<Integer, Set<Integer>> conversationSubscriptions = new ConcurrentHashMap<>();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final MessageService messageService = new MessageService();

    public static void addSession(int userId, Session session) {
        userSessions.put(userId, session);
        System.out.println("User " + userId + " connected via WebSocket");
    }

    public static void removeSession(int userId) {
        userSessions.remove(userId);
        conversationSubscriptions.remove(userId);
        System.out.println("User " + userId + " disconnected from WebSocket");
    }

    public static void subscribeToConversation(int userId, int conversationId) {
        conversationSubscriptions.computeIfAbsent(userId, k -> new CopyOnWriteArraySet<>())
                .add(conversationId);

        loadAndSendExistingMessages(userId, conversationId);
        System.out.println("User " + userId + " subscribed to conversation " + conversationId);
    }

    public static void unsubscribeFromConversation(int userId, int conversationId) {
        Set<Integer> userConversations = conversationSubscriptions.get(userId);
        if (userConversations != null) {
            userConversations.remove(conversationId);
        }
        System.out.println("User " + userId + " unsubscribed from conversation " + conversationId);
    }

    private static void loadAndSendExistingMessages(int userId, int conversationId) {
        try {
            var messages = messageService.getMessages(conversationId);

            WebSocketMessage wsMessage = new WebSocketMessage();
            wsMessage.setType("CONVERSATION_MESSAGES");
            wsMessage.setConversationId(conversationId);
            wsMessage.setMessages(messages);

            String messageJson = objectMapper.writeValueAsString(wsMessage);
            sendMessageToUser(userId, messageJson);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void broadcastNewMessage(Message message) {
        try {
            WebSocketMessage wsMessage = new WebSocketMessage();
            wsMessage.setType("NEW_MESSAGE");
            wsMessage.setMessage(message);
            wsMessage.setConversationId(message.getConversationId());

            String messageJson = objectMapper.writeValueAsString(wsMessage);

            conversationSubscriptions.entrySet().stream()
                    .filter(entry -> entry.getValue().contains(message.getConversationId()))
                    .forEach(entry -> sendMessageToUser(entry.getKey(), messageJson));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendMessageToUser(int userId, String messageJson) {
        Session session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.getRemote().sendString(messageJson, null);
            } catch (Exception e) {
                e.printStackTrace();
                userSessions.remove(userId);
            }
        }
    }

    public static class WebSocketMessage {
        private String type;
        private Message message;
        private java.util.List<Message> messages;
        private int conversationId;
        private String error;

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public Message getMessage() { return message; }
        public void setMessage(Message message) { this.message = message; }

        public java.util.List<Message> getMessages() { return messages; }
        public void setMessages(java.util.List<Message> messages) { this.messages = messages; }

        public int getConversationId() { return conversationId; }
        public void setConversationId(int conversationId) { this.conversationId = conversationId; }

        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }
}