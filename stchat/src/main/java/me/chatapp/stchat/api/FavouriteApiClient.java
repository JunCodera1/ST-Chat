package me.chatapp.stchat.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import me.chatapp.stchat.model.Favourite;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class FavouriteApiClient {
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    private final String baseUrl = "http://localhost:7078/api/favourites";

    public CompletableFuture<Boolean> addFavorite(int userId, int targetUserId) {
        Map<String, Integer> requestBody = Map.of(
                "userId", userId,
                "targetUserId", targetUserId
        );

        try {
            String json = objectMapper.writeValueAsString(requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/add"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                System.out.println("📤 [FavouriteApiClient] Status: " + response.statusCode());
                System.out.println("📤 [FavouriteApiClient] Response body: " + response.body());
                return response.statusCode() == 200;
            });

        } catch (Exception e) {
            e.printStackTrace();
            return CompletableFuture.completedFuture(false);
        }
    }

    public CompletableFuture<List<Favourite>> getFavouritesByUserId(int userId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/" + userId))
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(json -> {
                    try {
                        return objectMapper.readValue(json, new TypeReference<>() {
                        });
                    } catch (IOException e) {
                        throw new RuntimeException("Error parsing favourites", e);
                    }
                });
    }

    public CompletableFuture<Boolean> removeFavorite(int userId, int targetUserId) {
        String uri = baseUrl + "/" + userId + "/" + targetUserId;
        System.out.println(uri);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .DELETE()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                .thenApply(response -> {
                    System.out.println("🧾 removeFavorite - Status: " + response.statusCode());
                    return response.statusCode() == 200;
                });
    }

    public void close() {
        System.out.println("FavouriteApiClient closed.");
    }
}
