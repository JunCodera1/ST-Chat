package me.chatapp.stchat.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import me.chatapp.stchat.model.AttachmentMessage;
import me.chatapp.stchat.model.Message;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.ArrayList;
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
        this.objectMapper.findAndRegisterModules();
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
                    System.out.println("[DEBUG] Status code: " + response.statusCode());
                    System.out.println("[DEBUG] Response body: " + response.body()); // ← THÊM DÒNG NÀY

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

        return CompletableFuture.supplyAsync(() -> {
            try {
                // debug: in JSON
                ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
                String json = mapper.writeValueAsString(message);
                System.out.println("[DEBUG] Sending JSON: " + json);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("[DEBUG] Response code: " + response.statusCode());
                System.out.println("[DEBUG] Response body: " + response.body());

                return response.statusCode() == 200 || response.statusCode() == 201;
            } catch (Exception e) {
                System.err.println("[ERROR] Failed to send message: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        });
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

    // 10. Gửi tin nhắn trực tiếp từ user A đến user B
    public CompletableFuture<Integer> sendDirectMessage(int senderId, int receiverId, String content) {
        String url = BASE_URL + "/messages/direct";

        String json;
        try {
            var body = new java.util.HashMap<String, Object>();
            body.put("senderId", senderId);
            body.put("receiverId", receiverId);
            body.put("content", content);
            json = objectMapper.writeValueAsString(body);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize direct message body", e);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    System.out.println("[DEBUG] Direct message response code: " + response.statusCode());
                    System.out.println("[DEBUG] Direct message response body: " + response.body());

                    if (response.statusCode() == 200 || response.statusCode() == 201) {
                        try {
                            var responseMap = objectMapper.readValue(response.body(), new TypeReference<java.util.Map<String, Object>>() {});
                            return (int) responseMap.get("conversationId");  // 👈 Lấy ID mới
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to parse direct message response", e);
                        }
                    } else {
                        throw new RuntimeException("Failed to send direct message: " + response.statusCode());
                    }
                });
    }
    public CompletableFuture<String> uploadFile(File file) {
        String boundary = "Boundary-" + System.currentTimeMillis();
        String url = BASE_URL + "/files/upload";

        HttpRequest.BodyPublisher body = createMultipartBody(file, boundary);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(body)
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200 || response.statusCode() == 201) {
                        try {
                            var json = objectMapper.readTree(response.body());
                            return json.get("url").asText();
                        } catch (IOException e) {
                            throw new RuntimeException("Lỗi đọc URL từ response", e);
                        }
                    } else {
                        throw new RuntimeException("Upload thất bại: " + response.statusCode());
                    }
                });
    }

    private HttpRequest.BodyPublisher createMultipartBody(File file, String boundary) {
        var byteArrays = new ArrayList<byte[]>();
        String CRLF = "\r\n";

        String partHeader = "--" + boundary + CRLF +
                "Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"" + CRLF +
                "Content-Type: application/octet-stream" + CRLF + CRLF;

        String partFooter = CRLF + "--" + boundary + "--" + CRLF;

        try {
            byteArrays.add(partHeader.getBytes());
            byteArrays.add(Files.readAllBytes(file.toPath()));
            byteArrays.add(partFooter.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi đọc file", e);
        }

        return HttpRequest.BodyPublishers.ofByteArrays(byteArrays);
    }

    public List<Message> enrichMessagesWithAttachment(List<Message> messages) {
        for (Message msg : messages) {
            if (msg.getFileUrl() != null && msg.getFileName() != null) {
                String fileType = inferFileType(msg.getFileName());
                String fileExtension = msg.getFileName().substring(msg.getFileName().lastIndexOf('.') + 1);
                var attachment = new AttachmentMessage(
                        msg.getFileName(),
                        fileType,
                        msg.getFileSize(),
                        msg.getFileUrl(),
                        fileExtension
                );
                msg.setAttachment(attachment);
            }
        }
        return messages;
    }


    private String inferFileType(String fileName) {
        if (fileName == null) return "file";
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") || lower.endsWith(".gif")) return "image";
        if (lower.endsWith(".mp4") || lower.endsWith(".mov") || lower.endsWith(".avi")) return "video";
        if (lower.endsWith(".mp3") || lower.endsWith(".wav") || lower.endsWith(".ogg")) return "audio";
        if (lower.endsWith(".pdf")) return "pdf";
        if (lower.endsWith(".doc") || lower.endsWith(".docx") || lower.endsWith(".txt")) return "document";
        return "file";
    }


    public void close() {
        // HttpClient sẽ tự động cleanup
    }
}