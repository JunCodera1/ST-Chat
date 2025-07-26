package com.stchat.server.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Timestamp;


import com.stchat.server.Main;
import com.stchat.server.model.User;
import com.stchat.server.service.UserService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientHandler implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(ClientHandler.class);
    private final Socket clientSocket;
    private final Main server;
    private BufferedReader reader;
    private PrintWriter writer;
    private String username;
    private final UserService userService = new UserService();
    private User user;
    private final AuthProcessor authProcessor;

    private boolean connected;

    public ClientHandler(Socket clientSocket, Main server) {
        this.clientSocket = clientSocket;
        this.server = server;
        this.connected = true;
        this.authProcessor = new AuthProcessor(server);
    }

    @Override
    public void run() {
        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(clientSocket.getOutputStream(), true);

            // Đọc dòng đầu tiên từ client
            String request = reader.readLine();
            if (request == null || !request.trim().startsWith("{")) {
                writer.println("{\"status\":\"error\",\"message\":\"Invalid or non-JSON request.\"}");
                disconnect();
                return;
            }

            JSONObject json = new JSONObject(request);
            String type = json.optString("type");

            JSONObject response = authProcessor.handle(json);

            writer.println(response.toString());

            if (!"LOGIN".equalsIgnoreCase(type) || !"success".equalsIgnoreCase(response.optString("status"))) {
                disconnect();
                return;
            }

            JSONObject userJson = response.optJSONObject("user");
            if (userJson == null) {
                disconnect();
                return;
            }

            this.user = new User();
            user.setId(userJson.optInt("id"));
            user.setUsername(userJson.optString("username"));
            user.setEmail(userJson.optString("email"));
            user.setFirstName(userJson.optString("firstName"));
            user.setLastName(userJson.optString("lastName"));
            user.setAvatarUrl(userJson.optString("avatarUrl"));
            user.setActive(userJson.optBoolean("isActive"));

            this.username = user.getUsername();

            server.addClient(username, this);
            userService.setUserActiveStatus(user.getId(), true);

            sendMessage("System: Welcome " + username + " to ST Chat!");

            this.connected = true;

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("Received from " + username + ": " + line);
                JSONObject incoming = new JSONObject(line);
                handleClientMessage(incoming);
            }

        } catch (IOException e) {
            if (connected) {
                log.error("Lỗi khi xử lý client {}: {}", username, e.getMessage());
            }
        } finally {
            if (user != null) {
                userService.setUserActiveStatus(user.getId(), false);
                userService.setLastSeen(user.getId(), new Timestamp(System.currentTimeMillis()));
            }
            disconnect();
        }
    }


    private void handleClientMessage(JSONObject json) {
        String type = json.optString("type");

        if ("MESSAGE".equalsIgnoreCase(type)) {
            String to = json.optString("to");
            String content = json.optString("content");

            if (to == null || content == null) return;

            ClientHandler recipient = server.getClientHandler(to);
            if (recipient != null) {
                JSONObject msg = new JSONObject()
                        .put("type", "MESSAGE")
                        .put("from", username)
                        .put("to", to)
                        .put("content", content)
                        .put("timestamp", System.currentTimeMillis());

                recipient.sendMessage(msg.toString());
            }
        }
        else if ("TYPING_STATUS".equalsIgnoreCase(type)) {
            String to = json.optString("to");
            boolean isTyping = json.optBoolean("typing");

            ClientHandler recipient = server.getClientHandler(to);
            if (recipient != null) {
                JSONObject typingMsg = new JSONObject()
                        .put("type", "TYPING_STATUS")
                        .put("from", username)
                        .put("typing", isTyping);

                recipient.sendMessage(typingMsg.toString());
            }
        }
    }


    public boolean sendMessage(String message) {
        if (writer != null && connected) {
            try {
                writer.println(message);
                return !writer.checkError();
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public void disconnect() {
        connected = false;

        if (username != null) {
            server.removeClient(username);
        }

        try {
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            log.error("Lỗi khi đóng kết nối: {}", e.getMessage());
        }
    }
}