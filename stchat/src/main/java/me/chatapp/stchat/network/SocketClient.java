package me.chatapp.stchat.network;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketClient {
    private final Socket socket;
    private final PrintWriter writer;
    private final BufferedReader reader;

    public SocketClient(String host, int port) throws IOException {
        socket = new Socket(host, port);
        writer = new PrintWriter(socket.getOutputStream(), true);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
    public void logoutAndClose() {
        try {
            send("{\"type\":\"LOGOUT\"}");
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (isConnected()) {
                writer.println("/logout"); // gửi lệnh logout đến server
                writer.flush();
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi đóng kết nối Socket: " + e.getMessage());
        }
    }

    public void simulateLogin(String username, String password) throws IOException {
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        JSONObject loginRequest = new JSONObject()
                .put("type", "LOGIN")
                .put("username", username)
                .put("password", password);

        writer.println(loginRequest.toString());

        String response = reader.readLine();
        System.out.println("[" + username + "] Server response: " + response);

        // Optional: listen for messages in background
        new Thread(() -> {
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    System.out.println("[" + username + "] Received: " + line);
                }
            } catch (IOException e) {
                System.err.println("[" + username + "] Disconnected.");
            }
        }).start();
    }

    public void simulateRegister(String username, String email, String password) throws IOException {
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        JSONObject registerRequest = new JSONObject()
                .put("type", "REGISTER")
                .put("username", username)
                .put("email", email)
                .put("password", password);

        writer.println(registerRequest.toString());

        String response = reader.readLine();
        System.out.println("[" + username + "] Register response: " + response);

        socket.close();
    }

}

