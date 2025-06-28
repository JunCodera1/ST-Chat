package com.stchat.server.web;

import com.stchat.server.model.Message;
import com.stchat.server.service.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.Objects;

public class MessageController {
    private static final MessageService messageService = new MessageService();

    public static void registerRoutes(Javalin app) {
        app.get("/api/messages", MessageController::getMessagesByConversation);
        app.post("/api/messages", MessageController::createMessage);
        app.put("/api/messages/{id}", MessageController::updateMessage);
        app.delete("/api/messages/{id}", MessageController::deleteMessage);
        app.patch("/api/messages/{id}/pin", MessageController::togglePinMessage);
    }

    private static void getMessagesByConversation(Context ctx) {
        int conversationId = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("conversationId")));
        ctx.json(messageService.getMessages(conversationId));
    }

    private static void createMessage(Context ctx) {
        Message message = ctx.bodyAsClass(Message.class);
        if (messageService.sendMessage(message)) {
            ctx.status(201).result("Message created");
        } else {
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
}
