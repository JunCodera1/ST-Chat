package me.chatapp.stchat.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import me.chatapp.stchat.model.Conversation;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ConversationApiClient {

    private static final String BASE_URL = "http://localhost:7071/api/conversations";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ConversationApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    // 1. Lấy danh sách hội thoại theo userId
    public CompletableFuture<List<Conversation>> getConversationsByUserId(int userId) {
        String url = BASE_URL + "?userId=" + userId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    try {
                        if (response.statusCode() == 200) {
                            return objectMapper.readValue(response.body(), new TypeReference<List<Conversation>>() {});
                        } else {
                            throw new RuntimeException("Failed to fetch conversations: " + response.statusCode());
                        }
                    } catch (IOException e) {
                        throw new RuntimeException("Error parsing conversations", e);
                    }
                });
    }

    // 2. Tạo mới một hội thoại
    public CompletableFuture<Conversation> createConversation(Conversation conversation) {
        try {
            String json = objectMapper.writeValueAsString(conversation);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        if (response.statusCode() == 201) {
                            try {
                                return objectMapper.readValue(response.body(), Conversation.class);
                            } catch (IOException e) {
                                throw new RuntimeException("Error parsing created conversation", e);
                            }
                        } else {
                            throw new RuntimeException("Failed to create conversation: " + response.statusCode());
                        }
                    });

        } catch (IOException e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    // 3. Lấy chi tiết một hội thoại
    public CompletableFuture<Conversation> getConversationById(int id) {
        String url = BASE_URL + "/" + id;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    try {
                        if (response.statusCode() == 200) {
                            return objectMapper.readValue(response.body(), Conversation.class);
                        } else {
                            throw new RuntimeException("Conversation not found: " + response.statusCode());
                        }
                    } catch (IOException e) {
                        throw new RuntimeException("Error parsing conversation", e);
                    }
                });
    }

    public CompletableFuture<Conversation> createPrivateConversation(int userId1, int userId2) {
        try {
            String body = objectMapper.writeValueAsString(Map.of(
                    "userId1", userId1,
                    "userId2", userId2
            ));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/private"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        int status = response.statusCode();
                        String responseBody = response.body();

                        if (status == 200) {
                            try {
                                return objectMapper.readValue(responseBody, Conversation.class);
                            } catch (IOException e) {
                                throw new RuntimeException("❌ Error parsing response JSON: " + responseBody, e);
                            }
                        } else {
                            System.err.println("❌ Failed to create conversation.");
                            System.err.println("Status code: " + status);
                            System.err.println("Response body: " + responseBody);

                            throw new RuntimeException("Failed to get/create private conversation: HTTP " + status);
                        }
                    });

        } catch (IOException e) {
            return CompletableFuture.failedFuture(e);
        }
    }


    public CompletableFuture<Boolean> addMembersToConversation(int conversationId, List<Integer> memberIds) {
        try {
            String json = objectMapper.writeValueAsString(memberIds);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/" + conversationId + "/members"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> response.statusCode() == 200);

        } catch (IOException e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    // 6. Cập nhật thông tin hội thoại
    public CompletableFuture<Conversation> updateConversationInfo(int conversationId, Conversation updatedInfo) {
        try {
            String json = objectMapper.writeValueAsString(updatedInfo);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/" + conversationId))
                    .header("Content-Type", "application/json")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                    .build();

            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        try {
                            if (response.statusCode() == 200) {
                                return objectMapper.readValue(response.body(), Conversation.class);
                            } else {
                                throw new RuntimeException("Failed to update conversation: " + response.statusCode());
                            }
                        } catch (IOException e) {
                            throw new RuntimeException("Error parsing updated conversation", e);
                        }
                    });

        } catch (IOException e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    // 7. Lấy media (image/file/link...) trong hội thoại
    public CompletableFuture<List<String>> getMediaInConversation(int conversationId, String type) {
        String url = BASE_URL + "/" + conversationId + "/media?type=" + type;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    try {
                        if (response.statusCode() == 200) {
                            return objectMapper.readValue(response.body(), new TypeReference<List<String>>() {});
                        } else {
                            throw new RuntimeException("Failed to get media: " + response.statusCode());
                        }
                    } catch (IOException e) {
                        throw new RuntimeException("Error parsing media list", e);
                    }
                });
    }

    public CompletableFuture<Conversation> createChannelConversation(String channelName) {
        try {
            String body = objectMapper.writeValueAsString(Map.of("channelName", channelName));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/channel"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        if (response.statusCode() == 200) {
                            try {
                                return objectMapper.readValue(response.body(), Conversation.class);
                            } catch (IOException e) {
                                throw new RuntimeException("Error parsing channel conversation", e);
                            }
                        } else {
                            throw new RuntimeException("Failed to create or get channel conversation: " + response.statusCode());
                        }
                    });
        } catch (IOException e) {
            return CompletableFuture.failedFuture(e);
        }
    }

}
