package com.stchat.server.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stchat.server.handler.WebSocketManager;
import com.stchat.server.model.Message;
import com.stchat.server.service.ConversationService;
import com.stchat.server.service.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.sql.Timestamp;
import java.util.Map;
import java.util.Objects;

public class MessageController {
    private static final MessageService messageService = new MessageService();

    public static void registerRoutes(Javalin app) {
        app.get("/api/messages", MessageController::getMessagesByConversation);
        app.post("/api/messages", MessageController::createMessage);
        app.put("/api/messages/{id}", MessageController::updateMessage);
        app.delete("/api/messages/{id}", MessageController::deleteMessage);
        app.patch("/api/messages/{id}/pin", MessageController::togglePinMessage);
        app.get("/api/messages/search", MessageController::searchMessages);
        app.get("/api/messages/media", MessageController::getMediaMessages);
        app.get("/api/messages/pinned", MessageController::getPinnedMessages);
        app.get("/api/messages/time-range", MessageController::getMessagesByTimeRange);
        app.post("/api/messages/direct", MessageController::sendDirectMessage);
    }

    private static void getMessagesByConversation(Context ctx) {
        int conversationId = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("conversationId")));
        ctx.json(messageService.getMessages(conversationId));
    }

    private static void createMessage(Context ctx) {
        Message message = ctx.bodyAsClass(Message.class);
        System.out.println(">>> [SERVER] Received message: " + message);

        if (messageService.sendMessage(message)) {
            System.out.println(">>> [SERVER] Message saved successfully.");
            ctx.status(201).result("Message created");
        } else {
            System.out.println(">>> [SERVER] Failed to save message.");
            ctx.status(500).result("Failed to create message");
        }
    }


    private static void updateMessage(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        String newContent = ctx.body();
        if (messageService.editMessage(id, newContent)) {
            ctx.status(200).result("Message updated");
        } else {
            ctx.status(500).result("Update failed");
        }
    }

    private static void deleteMessage(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        if (messageService.deleteMessage(id)) {
            ctx.status(200).result("Message deleted");
        } else {
            ctx.status(500).result("Delete failed");
        }
    }

    private static void togglePinMessage(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        boolean pin = Boolean.parseBoolean(ctx.queryParam("pin"));
        if (messageService.pinMessage(id, pin)) {
            ctx.status(200).result("Pin state updated");
        } else {
            ctx.status(500).result("Failed to update pin state");
        }
    }

    private static void searchMessages(Context ctx) {
        int conversationId = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("conversationId")));
        String keyword = ctx.queryParam("q");
        ctx.json(messageService.searchMessages(conversationId, keyword));
    }

    private static void getMediaMessages(Context ctx) {
        int conversationId = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("conversationId")));
        String typeParam = ctx.queryParam("type");

        try {
            assert typeParam != null;
            Message.MessageType type = Message.MessageType.valueOf(typeParam.toUpperCase());
            ctx.json(messageService.getMedia(conversationId, type));
        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Invalid media type");
        }
    }

    private static void getPinnedMessages(Context ctx) {
        int conversationId = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("conversationId")));
        ctx.json(messageService.getPinnedMessages(conversationId));
    }

    private static void getMessagesByTimeRange(Context ctx) {
        int conversationId = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("conversationId")));
        Timestamp from = Timestamp.valueOf(Objects.requireNonNull(ctx.queryParam("from"))); // Format: yyyy-[m]m-[d]d hh:mm:ss
        Timestamp to = Timestamp.valueOf(Objects.requireNonNull(ctx.queryParam("to")));
        ctx.json(messageService.getMessagesByTimeRange(conversationId, from, to));
    }

    private static void sendDirectMessage(Context ctx) {
        Map<String, Object> body = ctx.bodyAsClass(Map.class);

        int senderId = Integer.parseInt(body.get("senderId").toString());
        int receiverId = Integer.parseInt(body.get("receiverId").toString());
        String content = body.get("content").toString();

        var conversation = ConversationService.createPrivateConversation(senderId, receiverId);

        Message message = new Message();
        message.setConversationId(conversation.getId());
        message.setSenderId(senderId);
        message.setContent(content);
        message.setMessageType(Message.MessageType.TEXT);
        message.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        boolean success = messageService.sendMessage(message);

        if (success) {
            try {
                String json = new ObjectMapper().writeValueAsString(message);
                WebSocketManager.sendMessageToUser(receiverId, json);
            } catch (Exception e) {
                e.printStackTrace();
            }

            ctx.status(201).json(Map.of(
                    "status", "success",
                    "conversationId", conversation.getId()
            ));
        } else {
            ctx.status(500).result("Failed to send message");
        }
    }

}