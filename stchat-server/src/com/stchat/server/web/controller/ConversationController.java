package com.stchat.server.web.controller;

import com.stchat.server.model.Conversation;
import com.stchat.server.service.ConversationService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;

public class ConversationController {

    public static void registerRoutes(Javalin app) {
        app.get("/api/conversations", ConversationController::getConversationsByUserId);
        app.post("/api/conversations", ConversationController::createConversation);
        app.get("/api/conversations/:id", ConversationController::getConversationById);

        app.post("/api/conversations/private", ConversationController::getOrCreatePrivateConversation);
        app.post("/api/conversations/:id/members", ConversationController::addMembersToConversation);
        app.patch("/api/conversations/:id", ConversationController::updateConversationInfo);
        app.get("/api/conversations/:id/media", ConversationController::getMediaInConversation);
    }

    private static void getConversationsByUserId(Context ctx) {
        String userIdParam = ctx.queryParam("userId");
        if (userIdParam == null) {
            ctx.status(400).result("Missing userId query parameter");
            return;
        }

        try {
            int userId = Integer.parseInt(userIdParam);
            List<Conversation> conversations = ConversationService.getConversationsForUser(userId);
            ctx.json(conversations);
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid userId format");
        } catch (Exception e) {
            ctx.status(500).result("Server error: " + e.getMessage());
        }
    }

    private static void createConversation(Context ctx) {
        try {
            Conversation conversation = ctx.bodyAsClass(Conversation.class);
            Conversation created = ConversationService.createConversation(conversation);
            ctx.status(201).json(created);
        } catch (Exception e) {
            ctx.status(500).result("Error creating conversation: " + e.getMessage());
        }
    }

    private static void getConversationById(Context ctx) {
        String idParam = ctx.pathParam("id");

        try {
            int conversationId = Integer.parseInt(idParam);
            Conversation convo = ConversationService.getConversationById(conversationId);
            if (convo == null) {
                ctx.status(404).result("Conversation not found");
                return;
            }
            ctx.json(convo);
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid conversation id");
        }
    }

    private static void getOrCreatePrivateConversation(Context ctx) {
        try {
            Map body = ctx.bodyAsClass(Map.class);
            int user1 = (int) body.get("userId1");
            int user2 = (int) body.get("userId2");

            Conversation convo = ConversationService.getOrCreatePrivateConversation(user1, user2);
            ctx.status(200).json(convo);
        } catch (Exception e) {
            ctx.status(500).result("Error handling private conversation: " + e.getMessage());
        }
    }

    private static void addMembersToConversation(Context ctx) {
        try {
            int conversationId = Integer.parseInt(ctx.pathParam("id"));
            List<Integer> newMemberIds = ctx.bodyAsClass(List.class);

            boolean success = ConversationService.addMembers(conversationId, newMemberIds);
            if (success) {
                ctx.status(200).result("Members added successfully");
            } else {
                ctx.status(400).result("Failed to add members");
            }
        } catch (Exception e) {
            ctx.status(500).result("Error adding members: " + e.getMessage());
        }
    }

    private static void updateConversationInfo(Context ctx) {
        try {
            int conversationId = Integer.parseInt(ctx.pathParam("id"));
            Conversation updatedInfo = ctx.bodyAsClass(Conversation.class);

            Conversation updated = ConversationService.updateConversationInfo(conversationId, updatedInfo);
            if (updated != null) {
                ctx.status(200).json(updated);
            } else {
                ctx.status(404).result("Conversation not found");
            }
        } catch (Exception e) {
            ctx.status(500).result("Error updating conversation: " + e.getMessage());
        }
    }

    private static void getMediaInConversation(Context ctx) {
        try {
            int conversationId = Integer.parseInt(ctx.pathParam("id"));
            String type = ctx.queryParam("type"); // image | video | file | link

            List<String> mediaUrls = ConversationService.getMedia(conversationId, type);
            ctx.json(mediaUrls);
        } catch (Exception e) {
            ctx.status(500).result("Error getting media: " + e.getMessage());
        }
    }
}
