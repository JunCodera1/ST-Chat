package me.chatapp.stchat;

import me.chatapp.stchat.api.SocketClient;

public class MultiClientLauncher {
    public static void main(String[] args) {
        new Thread(() -> {
            try {
                SocketClient client = new SocketClient("localhost", 8080);
                client.simulateRegister("user1", "user1@example.com", "123456");
                Thread.sleep(100); // Đợi 1 chút rồi login
                client = new SocketClient("localhost", 8080);
                client.simulateLogin("user1", "123456");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                SocketClient client = new SocketClient("localhost", 8080);
                client.simulateRegister("user2", "user2@example.com", "654321");
                Thread.sleep(100);
                client = new SocketClient("localhost", 8080);
                client.simulateLogin("user2", "654321");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}


