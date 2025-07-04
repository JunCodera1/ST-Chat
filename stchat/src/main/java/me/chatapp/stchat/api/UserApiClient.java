package me.chatapp.stchat.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.chatapp.stchat.model.User;

import java.net.URI;
import java.net.http.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class UserApiClient {
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CompletableFuture<List<User>> getAllUsers() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/users"))
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(json -> {
                    try {
                        return objectMapper.readValue(json, new TypeReference<>() {});
                    } catch (Exception e) {
                        throw new RuntimeException("Error parsing user list", e);
                    }
                });
    }

    public void close() {
        System.out.println("UserApiClient closed.");
    }
}
