package me.chatapp.stchat.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.chatapp.stchat.model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class UserApiClient {
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CompletableFuture<List<User>> getAllUsers() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:6060/api/users"))
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(json -> {
                    try {
                        return objectMapper.readValue(json, new TypeReference<List<User>>() {});
                    } catch (Exception e) {
                        throw new RuntimeException("Error parsing user list", e);
                    }
                });
    }

    public Optional<User> findUserByUsername(String username) {
        try {
            URL url = new URL("http://localhost:6060/api/users/" + username); // server dùng `{username}`
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == 200) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    User user = objectMapper.readValue(response.toString(), User.class);
                    return Optional.of(user);
                }
            } else {
                return Optional.empty();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public CompletableFuture<User> getUserById(int id) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:6060/api/users/id/" + id))
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() != 200) {
                        throw new RuntimeException("User not found. Status: " + response.statusCode());
                    }

                    try {
                        return objectMapper.readValue(response.body(), User.class);
                    } catch (IOException e) {
                        throw new RuntimeException("Error parsing user from JSON", e);
                    }
                });
    }



    public void close() {
        System.out.println("UserApiClient closed.");
    }
}
