package com.stchat.server.web.controller;

import com.stchat.server.dto.FavouriteDto;
import com.stchat.server.model.Favourite;
import com.stchat.server.service.FavouriteService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FavouriteController {
    private static final FavouriteService favouriteService = new FavouriteService();

    public static void registerRoutes(Javalin app) {
        app.post("/api/favourites/add", FavouriteController::addFavourite);
        app.delete("/api/favourites", FavouriteController::removeFavourite);
        app.get("/api/favourites/{userId}", FavouriteController::getFavoritesByUserId);
        app.get("/api/favourites/check", FavouriteController::checkFavoriteExists);
    }

    private static void addFavourite(Context ctx) {
        try {
            Map<String, Integer> body = ctx.bodyAsClass(Map.class);
            int userId = body.get("userId");
            int targetUserId = body.get("targetUserId");

            System.out.println("📥 [FavouriteController] Received addFavorite request: userId=" + userId + ", targetUserId=" + targetUserId);

            boolean added = favouriteService.addFavourite(userId, targetUserId);

            if (added) {
                ctx.result("Favourite added");
            } else {
                ctx.status(400).result("Unable to add favourite");
            }

        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).result("Server error: " + e.getMessage());
        }
    }

    private static void removeFavourite(Context ctx) {
        Map<String, Integer> body = ctx.bodyAsClass(Map.class);
        int userId = body.get("userId");
        int favoriteUserId = body.get("favoriteUserId");

        boolean removed = favouriteService.removeFavourite(userId, favoriteUserId);
        if (removed) {
            ctx.result("Favourite removed successfully");
        } else {
            ctx.status(400).result("Failed to remove favourite");
        }
    }

    private static void getFavoritesByUserId(Context ctx) {
        try {
            int userId = Integer.parseInt(ctx.pathParam("userId"));
            List<Favourite> favourites = favouriteService.getFavoritesByUserId(userId);
            if (favourites == null) favourites = List.of();

            List<FavouriteDto> dtoList = favourites.stream()
                    .filter(f -> f.getCreatedAt() != null)
                    .map(f -> new FavouriteDto(
                            f.getUserId(),
                            f.getFavoriteUserId(),
                            f.getCreatedAt().toLocalDateTime()))
                    .collect(Collectors.toList());

            ctx.json(dtoList);
        } catch (Exception e) {
            e.printStackTrace(); // 👈 ghi log rõ ràng
            ctx.status(500).result("Server error: " + e.getMessage());
        }
    }


    private static void checkFavoriteExists(Context ctx) {
        int userId = Integer.parseInt(ctx.queryParam("userId"));
        int favoriteUserId = Integer.parseInt(ctx.queryParam("favoriteUserId"));

        boolean exists = favouriteService.isFavoriteExists(userId, favoriteUserId);
        ctx.json(Map.of("exists", exists));
    }
}
