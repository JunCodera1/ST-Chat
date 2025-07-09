package com.stchat.server.web.server;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.stchat.server.web.controller.FavouriteController;
import com.stchat.server.web.mapper.CustomJacksonMapper;
import io.javalin.Javalin;
import io.javalin.plugin.bundled.CorsPluginConfig;

public class FavouriteServer {
    public static void start() {
        Javalin app = Javalin.create(config -> {
            config.jsonMapper(new CustomJacksonMapper(mapper -> {
                mapper.registerModule(new JavaTimeModule());
                mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            }));

            config.bundledPlugins.enableCors(cors -> cors.addRule(CorsPluginConfig.CorsRule::anyHost));
        });

        app.before(ctx -> {
            ctx.header("Access-Control-Allow-Origin", "*");
            ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            ctx.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
        });

        app.options("/*", ctx -> ctx.status(200));

        FavouriteController.registerRoutes(app);

        app.start(7078);
        System.out.println("✅ Favourite server is running at http://localhost:7078/api/favourites");
    }
}


