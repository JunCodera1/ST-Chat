package com.stchat.server.handler;


import com.stchat.server.Main;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

public class CommandHandler {

    public static void handle(String command, ClientHandler client, Main server) {
        String[] parts = command.split(" ", 3); // /msg <username> <message>
        String cmd = parts[0].toLowerCase();

        switch (cmd) {
            case "/help":
                client.sendMessage("System: Các lệnh có sẵn:");
                client.sendMessage("System: /help - Hiển thị trợ giúp");
                client.sendMessage("System: /users - Danh sách người dùng online");
                client.sendMessage("System: /time - Thời gian hiện tại");
                client.sendMessage("System: /quit - Thoát khỏi chat");
                client.sendMessage("System: /msg <username> <message> - Gửi tin nhắn riêng");
                break;

            case "/users":
                Set<String> usernames = server.getClientUsernames();
                StringBuilder list = new StringBuilder("Người dùng online (" + usernames.size() + "): ");
                for (String name : usernames) {
                    list.append(name).append(", ");
                }
                if (!usernames.isEmpty()) {
                    list.setLength(list.length() - 2); // remove last ", "
                }
                client.sendMessage("System: " + list);
                break;

            case "/time":
                String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy"));
                client.sendMessage("System: Thời gian hiện tại: " + currentTime);
                break;

            case "/logout":
            case "/quit":
                client.sendMessage("System: Tạm biệt " + client.getUsername() + "!");
                client.disconnect();
                break;

            case "/msg":
                handlePrivateMessage(parts, client, server);
                break;

            default:
                client.sendMessage("System: Lệnh không hợp lệ: " + cmd + ". Gõ /help để xem danh sách lệnh.");
                break;
        }
    }

    private static void handlePrivateMessage(String[] parts, ClientHandler sender, Main server) {
        if (parts.length < 3) {
            sender.sendMessage("System: Cú pháp: /msg <username> <message>");
            return;
        }

        String targetUsername = parts[1].trim();
        String privateMessage = parts[2].trim();

        if (targetUsername.equalsIgnoreCase(sender.getUsername())) {
            sender.sendMessage("System: Không thể gửi tin nhắn cho chính bạn!");
            return;
        }

        if (privateMessage.isEmpty()) {
            sender.sendMessage("System: Tin nhắn không được để trống!");
            return;
        }

        ClientHandler recipient = server.getClientHandler(targetUsername);
        if (recipient != null && recipient.isConnected()) {
            String formattedMessage = "Private from " + sender.getUsername() + ": " + privateMessage;
            recipient.sendMessage(formattedMessage);
            sender.sendMessage(formattedMessage);
            System.out.println("[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "] " + formattedMessage);
        } else {
            sender.sendMessage("System: Người dùng '" + targetUsername + "' không tồn tại hoặc không online!");
        }
    }
}

