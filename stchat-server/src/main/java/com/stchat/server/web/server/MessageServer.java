package com.stchat.server.web.server;

import com.stchat.server.web.controller.FileController;
import com.stchat.server.web.controller.MessageController;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class MessageServer {
    public static void start() {
        Javalin app = Javalin.create()
                .before(MessageServer::setCorsHeaders)
                .options("/*", ctx -> {
                    setCorsHeaders(ctx);
                    ctx.status(200);
                })
                .start(7070);


        MessageController.registerRoutes(app);
        FileController.registerRoutes(app);

        System.out.println("Message server is running at http://localhost:7070/api/messages");
    }

    private static void setCorsHeaders(Context ctx) {
        ctx.header("Access-Control-Allow-Origin", "*");
        ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH");
        ctx.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }
}
