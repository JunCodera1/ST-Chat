package com.stchat.server.web;

import com.stchat.server.service.PasswordService;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class PasswordChangeServer {

    public static void start() {
        Javalin app = Javalin.create().start(9090);

        // Middleware để đặt Content-Type mặc định
        app.before(ctx -> ctx.contentType("text/plain"));

        app.get("/api/confirm-password-change", PasswordChangeServer::handleConfirmPasswordChange);

        System.out.println("PasswordChangeServer đang chạy tại http://localhost:9090/api/confirm-password-change?token=...");
    }

    private static void handleConfirmPasswordChange(Context ctx) {
        String token = ctx.queryParam("token");
        if (token == null || token.isEmpty()) {
            ctx.status(400).result("Thiếu token!");
            return;
        }

        PasswordService.confirmPasswordChange(token);
        ctx.result("✅ Xác nhận token: " + token);
    }
}
