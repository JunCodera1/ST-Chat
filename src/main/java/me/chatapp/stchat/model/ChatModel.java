package me.chatapp.stchat.model;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChatModel {
    private final ObservableList<Message> messages;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private boolean connected;
    private String userName;
    private final List<ChatModelListener> listeners;
    private Thread readerThread;

    public ChatModel() {
        this.listeners = new ArrayList<>();
        this.messages = FXCollections.observableArrayList();
        connected = false;

        // Add welcome message
        addSystemMessage("Chào mừng bạn đến với ST Chat!");
    }

    public void addMessage(String sender, String content, MessageType type) {
        Platform.runLater(() -> {
            Message message = new Message(sender, content, type, LocalDateTime.now());
            messages.add(message);
            notifyMessageAdded();
        });
    }

    public void addSystemMessage(String content) {
        addMessage("System", content, MessageType.SYSTEM);
    }

    public void sendMessage(String message) {
        if (connected && writer != null && !message.trim().isEmpty()) {
            writer.println(message);
            writer.flush(); // Ensure message is sent immediately
        }
    }

    public ObservableList<Message> getMessages() {
        return messages;
    }

    public void addListener(ChatModelListener listener) {
        listeners.add(listener);
    }

    private void notifyMessageAdded() {
        for (ChatModelListener listener : listeners) {
            listener.onMessageAdded();
        }
    }

    public void connect(String host, int port, String username) {
        try {
            this.userName = username; // Fixed variable name
            socket = new Socket(host, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            // Send username to server
            writer.println(username);

            connected = true;
            notifyConnectionStatusChanged(true);

            startMessageReader();

        } catch (IOException e) {
            notifyError("Cannot connect to server: " + e.getMessage());
        }
    }

    private void startMessageReader() {
        readerThread = new Thread(() -> {
            try {
                String message;
                while (connected && (message = reader.readLine()) != null) {
                    final String receivedMessage = message;
                    Platform.runLater(() -> notifyMessageReceived(receivedMessage));
                }
            } catch (IOException e) {
                if (connected) { // Only notify error if we were supposed to be connected
                    Platform.runLater(() -> {
                        notifyError("Connection lost: " + e.getMessage());
                        connected = false;
                        notifyConnectionStatusChanged(false);
                    });
                }
            }
        });
        readerThread.setDaemon(true);
        readerThread.start();
    }

    private void notifyMessageReceived(String message) {
        for (ChatModelListener listener : listeners) {
            listener.onMessageReceived(message);
        }
    }

    private void notifyError(String error) {
        for (ChatModelListener listener : listeners) {
            listener.onError(error); // Fixed: was calling wrong method
        }
    }

    private void notifyConnectionStatusChanged(boolean connected) {
        for (ChatModelListener listener : listeners) {
            listener.onConnectionStatusChanged(connected);
        }
    }

    public void disconnect() {
        try {
            connected = false;

            // Interrupt the reader thread
            if (readerThread != null && readerThread.isAlive()) {
                readerThread.interrupt();
            }

            if (writer != null) {
                writer.close();
                writer = null;
            }
            if (reader != null) {
                reader.close();
                reader = null;
            }
            if (socket != null) {
                socket.close();
                socket = null;
            }

            notifyConnectionStatusChanged(false);
        } catch (IOException e) {
            notifyError("Error during disconnect: " + e.getMessage());
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public String getUserName() {
        return userName;
    }

    public interface ChatModelListener {
        void onMessageAdded();
        void onMessageReceived(String message);
        void onConnectionStatusChanged(boolean connected);
        void onError(String error);
    }
}