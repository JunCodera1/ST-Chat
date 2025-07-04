package me.chatapp.stchat.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import me.chatapp.stchat.model.Message;

import java.io.IOException;
import java.net.URI;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MessageApiClient {
    private static final String BASE_URL = "http://localhost:7070/api";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public MessageApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    // 1. Lấy danh sách messages theo conversation ID
    public CompletableFuture<List<Message>> getMessages(int conversationId) {
        String url = BASE_URL + "/messages?conversationId=" + conversationId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            return objectMapper.readValue(response.body(),
                                    new TypeReference<List<Message>>() {});
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to parse messages", e);
                        }
                    } else {
                        throw new RuntimeException("Failed to fetch messages: " + response.statusCode());
                    }
                });
    }

    // 2. Gửi message mới
    public CompletableFuture<Boolean> sendMessage(Message message) {
        String url = BASE_URL + "/messages";

        try {
            String json = objectMapper.writeValueAsString(message);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> response.statusCode() == 201);

        } catch (IOException e) {
            return CompletableFuture.completedFuture(false);
        }
    }

    // 3. Cập nhật message
    public CompletableFuture<Boolean> updateMessage(int messageId, String newContent) {
        String url = BASE_URL + "/messages/" + messageId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "text/plain")
                .PUT(HttpRequest.BodyPublishers.ofString(newContent))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> response.statusCode() == 200);
    }

    // 4. Xóa message
    public CompletableFuture<Boolean> deleteMessage(int messageId) {
        String url = BASE_URL + "/messages/" + messageId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .DELETE()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> response.statusCode() == 200);
    }

    // 5. Pin/Unpin message
    public CompletableFuture<Boolean> togglePinMessage(int messageId, boolean pin) {
        String url = BASE_URL + "/messages/" + messageId + "/pin?pin=" + pin;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> response.statusCode() == 200);
    }

    // 6. Tìm kiếm messages
    public CompletableFuture<List<Message>> searchMessages(int conversationId, String keyword) {
        String url = BASE_URL + "/messages/search?conversationId=" + conversationId + "&q=" + keyword;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            return objectMapper.readValue(response.body(),
                                    new TypeReference<List<Message>>() {});
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to parse search results", e);
                        }
                    } else {
                        throw new RuntimeException("Search failed: " + response.statusCode());
                    }
                });
    }

    // 7. Lấy media messages (FILE, IMAGE, LINK)
    public CompletableFuture<List<Message>> getMediaMessages(int conversationId, String mediaType) {
        String url = BASE_URL + "/messages/media?conversationId=" + conversationId + "&type=" + mediaType;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            return objectMapper.readValue(response.body(),
                                    new TypeReference<List<Message>>() {});
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to parse media messages", e);
                        }
                    } else {
                        throw new RuntimeException("Failed to fetch media: " + response.statusCode());
                    }
                });
    }

    // 8. Lấy pinned messages
    public CompletableFuture<List<Message>> getPinnedMessages(int conversationId) {
        String url = BASE_URL + "/messages/pinned?conversationId=" + conversationId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            return objectMapper.readValue(response.body(),
                                    new TypeReference<List<Message>>() {});
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to parse pinned messages", e);
                        }
                    } else {
                        throw new RuntimeException("Failed to fetch pinned messages: " + response.statusCode());
                    }
                });
    }

    // 9. Lấy messages theo khoảng thời gian
    public CompletableFuture<List<Message>> getMessagesByTimeRange(int conversationId,
                                                                   Timestamp from, Timestamp to) {
        String url = BASE_URL + "/messages/time-range?conversationId=" + conversationId +
                "&from=" + from.toString() + "&to=" + to.toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            return objectMapper.readValue(response.body(),
                                    new TypeReference<List<Message>>() {});
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to parse time range messages", e);
                        }
                    } else {
                        throw new RuntimeException("Failed to fetch messages by time range: " + response.statusCode());
                    }
                });
    }

    public void close() {
        // HttpClient sẽ tự động cleanup
    }
}