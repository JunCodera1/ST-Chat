package com.stchat.server.web.server;

import com.stchat.server.web.controller.UserController;
import io.javalin.Javalin;

public class UserServer {
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
                .start(7072);

        UserController.registerRoutes(app);

        System.out.println("User server is running at http://localhost:7072/api/users");
    }
}
