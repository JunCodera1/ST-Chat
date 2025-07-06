package me.chatapp.stchat.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.chatapp.stchat.model.Message;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

public class SocketClient {
    private final Socket socket;
    private final PrintWriter writer;
    private final BufferedReader reader;
    private Consumer<Message> messageListener;

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
                writer.println("/logout");
                writer.flush();
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi đóng kết nối Socket: " + e.getMessage());
        }
    }

    public void startListening(java.util.function.Consumer<String> messageConsumer) {
        new Thread(() -> {
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    messageConsumer.accept(line);
                }
            } catch (IOException e) {
                System.err.println("Disconnected from server.");
            }
        }).start();
    }


    public void setMessageListener(java.util.function.Consumer<Message> listener) {
        this.messageListener = listener;
    }

    public void sendMessage(Message message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(message);
            send(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startListening() {
        new Thread(() -> {
            String line;
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.findAndRegisterModules(); // để hỗ trợ LocalDateTime

                while ((line = reader.readLine()) != null) {
                    try {
                        Message message = objectMapper.readValue(line, Message.class);
                        if (messageListener != null) {
                            messageListener.accept(message);
                        }
                    } catch (Exception parseEx) {
                        System.err.println("Không thể parse message: " + line);
                    }
                }
            } catch (IOException e) {
                System.err.println("Disconnected from server.");
            }
        }).start();
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

    public void simulateRegister(String username, String firstname, String lastName, String email, String password) throws IOException {
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        JSONObject registerRequest = new JSONObject()
                .put("type", "REGISTER")
                .put("username", username)
                .put("firstname", firstname)
                .put("lastname", lastName)
                .put("email", email)
                .put("password", password);

        writer.println(registerRequest.toString());

        String response = reader.readLine();
        System.out.println("[" + username + "] Register response: " + response);

        socket.close();
    }

}

