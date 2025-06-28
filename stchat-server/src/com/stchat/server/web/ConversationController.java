package com.stchat.server.web;

import com.stchat.server.model.Conversation;
import com.stchat.server.service.ConversationService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class ConversationController {

    public static void registerRoutes(Javalin app) {
        app.get("/api/conversations", ConversationController::getConversationsByUserId);
        app.post("/api/conversations", ConversationController::createConversation);
        app.get("/api/conversations/:id", ConversationController::getConversationById);
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
            // Optional: nếu bạn muốn implement sau
            ctx.status(501).result("Not implemented");
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid conversation id");
        }
    }
}
