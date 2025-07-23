package me.chatapp.stchat.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import me.chatapp.stchat.model.Message;
import me.chatapp.stchat.view.components.organisms.Panel.ChatPanel;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;
import me.chatapp.stchat.controller.MessageController;

public class SocketClient {
    private final Socket socket;
    private final PrintWriter writer;
    private final BufferedReader reader;
    private Consumer<Message> messageListener;
    private ChatPanel activeChatPanel;
    private int currentConversationId = -1;

    public void setActiveChatPanel(int conversationId, ChatPanel panel) {
        this.activeChatPanel = panel;
        this.currentConversationId = conversationId;
    }


    public SocketClient(String host, int port) throws IOException {
        socket = new Socket(host, port);
        writer = new PrintWriter(socket.getOutputStream(), true);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    private static SocketClient instance;

    public static SocketClient getInstance() {
        return instance;
    }

    public static void createInstance(String host, int port) throws IOException {
        if (instance == null) {
            instance = new SocketClient(host, port);
        }
    }

    public void send(String message) {
        writer.println(message);
    }

    public String receive() throws IOException {
        return reader.readLine();
    }

    public boolean isConnected() {
        return socket != null && !socket.isClosed() && socket.isConnected();
    }

    public void close() {
        try {
            if (isConnected()) {
                writer.println("/logout");
                writer.flush();
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi đóng kết nối Socket: " + e.getMessage());
        }
    }

    public void sendTypingStatus(int fromId, int toId, boolean isTyping) {
        JSONObject json = new JSONObject();
        json.put("type", "typing");
        json.put("from", fromId);
        json.put("to", toId);
        json.put("isTyping", isTyping);

        writer.println(json.toString());
    }

    public void startListening(Consumer<String> messageConsumer) {
        new Thread(() -> {
            String line;
            ObjectMapper mapper = new ObjectMapper();

            try {
                while ((line = reader.readLine()) != null) {
                    messageConsumer.accept(line); // Gửi raw string JSON
                }
            } catch (IOException e) {
                System.err.println("❌ Socket bị đóng hoặc lỗi khi nhận tin nhắn.");
                e.printStackTrace();
            }
        }).start();
    }



    public void setMessageListener(Consumer<Message> listener) {
        this.messageListener = listener;
    }

    public void sendMessage(Message message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JSONObject json = new JSONObject(objectMapper.writeValueAsString(message));
            json.put("type", "MESSAGE"); // Đảm bảo đúng chữ hoa
            send(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isOpen() {
        return socket != null && !socket.isClosed() && socket.isConnected();
    }


    public void sendLogout(int userId) {
        JSONObject logout = new JSONObject();
        logout.put("type", "LOGOUT");
        logout.put("userId", userId);
        send(logout.toString());
    }

    // Trong thread lắng nghe socket:
    private void listenForMessages() {
        new Thread(() -> {
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    JSONObject json = new JSONObject(line);
                    String type = json.optString("type", "");
                    if (type.equals("message")) {
                        // Parse message và hiển thị lên UI
                        Message msg = parseMessageFromJson(json);
                        Platform.runLater(() -> {
                            MessageController.getInstance().receiveMessage(msg);
                        });
                    } else if (type.equals("typing")) {
                        int fromId = json.getInt("from");
                        boolean isTyping = json.getBoolean("isTyping");
                        String senderName = getUserNameById(fromId); // Cần implement hàm này
                        Platform.runLater(() -> {
                            ChatPanel chatPanel = getActiveChatPanel(); // Cần implement hàm này
                            if (chatPanel != null) {
                                if (isTyping) {
                                    chatPanel.showTypingIndicator(senderName);
                                } else {
                                    chatPanel.hideTypingIndicator();
                                }
                            }
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "Socket-Listener").start();
    }

    // Thêm các hàm hỗ trợ:
    private Message parseMessageFromJson(JSONObject json) {
        // Parse các trường cần thiết từ json để tạo Message
        Message msg = new Message();
        msg.setSenderId(json.getInt("from"));
        msg.setReceiverId(json.getInt("to"));
        msg.setContent(json.getString("content"));
        msg.setConversationId(json.optInt("conversationId", -1));
        // ... parse các trường khác nếu cần
        return msg;
    }

    private String getUserNameById(int userId) {
        // TODO: Lấy tên user từ userId (có thể từ cache hoặc gọi API)
        return "User " + userId;
    }

    private ChatPanel getActiveChatPanel() {
        // TODO: Lấy ChatPanel đang active (có thể từ AppContext hoặc MessageController)
        return MessageController.getInstance().getActiveChatPanel();
    }
}

