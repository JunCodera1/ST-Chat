package com.stchat.server.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


import com.stchat.server.Main;
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
    private boolean connected;

    public ClientHandler(Socket clientSocket, Main server) {
        this.clientSocket = clientSocket;
        this.server = server;
        this.connected = true;
    }

    @Override
    public void run() {
        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(clientSocket.getOutputStream(), true);

            String request = reader.readLine();
            System.out.println("Received from client: " + request);

            if (request == null || !request.trim().startsWith("{")) {
                writer.println("{\"status\":\"error\",\"message\":\"Invalid or non-JSON request.\"}");
                disconnect();
                return;
            }

            JSONObject json = new JSONObject(request);
            JSONObject response = AuthProcessor.handle(json);
            writer.println(response.toString());

            if (!"LOGIN".equalsIgnoreCase(json.optString("type")) ||
                    !"success".equalsIgnoreCase(response.optString("status"))) {
                disconnect();
                return;
            }

            this.username = json.optString("username");
            server.addClient(username, this);
            sendMessage("System: Welcome " + username + " to ST Chat!");

        } catch (IOException e) {
            if (connected) {
                log.error("Lỗi khi xử lý client {}: {}", username, e.getMessage());
            }
        } finally {
            disconnect();
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