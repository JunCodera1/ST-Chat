package com.stchat.server.web;

import io.javalin.Javalin;

public class MessageServer {
    public static void start() {
        Javalin app = Javalin.create()
                .before(ctx -> {
                    ctx.header("Access-Control-Allow-Origin", "*");
                    ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                    ctx.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
                })
                .options("/*", ctx -> {
                    ctx.status(200);
                })
                .start(7070);

        MessageController.registerRoutes(app);

        System.out.println("Message server is running at http://localhost:7070/api/messages");
    }
}
