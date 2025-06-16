package me.chatapp.stchat.controller;

import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import me.chatapp.stchat.model.ChatModel;
import me.chatapp.stchat.model.MessageType;
import me.chatapp.stchat.view.pages.ChatView;

public class ChatController implements ChatModel.ChatModelListener {
    private final ChatModel model;
    private final ChatView view;

    public ChatController(ChatModel model, ChatView view) {
        this.model = model;
        this.view = view;

        // Register this controller as listener
        model.addListener(this);

        // Set up view action handlers
        view.setOnConnectAction(this::handleConnect);
        view.setOnDisconnectAction(this::handleDisconnect);
        view.setOnSendMessageAction(this::handleSendMessage);

        setupEventHandlers();
    }

    private void handleConnect(String host, String port, String username) {
        try {
            int portNum = Integer.parseInt(port);
            if (username.trim().isEmpty()) {
                view.showError("Vui lòng nhập tên người dùng");
                return;
            }
            model.connect(host, portNum, username);
        } catch (NumberFormatException e) {
            view.showError("Port phải là số");
        }
    }

    private void handleDisconnect() {
        model.disconnect();
    }

    private void handleSendMessage(String message) {
        if (!message.trim().isEmpty()) {
            if (message.startsWith("/msg")) {
                // Đánh dấu tin nhắn riêng trên giao diện
                model.addMessage(model.getUserName(), "(Private) " + message, MessageType.USER);
            } else {
                model.addMessage(model.getUserName(), message, MessageType.USER);
            }
            model.sendMessage(message); // Gửi đến server
            Platform.runLater(() -> {
                view.getMessageField().clear();
                view.getSendButton().setDisable(false);
            });
        }
    }

    public void initialize() {
        view.getStatusLabel().setText("Ứng dụng đã sẵn sàng");
        if (view.getMessageField() != null) {
            view.getMessageField().requestFocus();
        }
    }

    private void setupEventHandlers() {
        // Send button click
        if (view.getSendButton() != null) {
            view.getSendButton().setOnAction(e -> sendMessage());
        }

        // Enter key press in input field
        if (view.getMessageField() != null) {
            view.getMessageField().setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ENTER) {
                    sendMessage();
                }
            });
        }

        // Clear button click
        if (view.getClearButton() != null) {
            view.getClearButton().setOnAction(e -> clearChat());
        }

        // Username field change listener
        if (view.getUserNameField() != null) {
            view.getUserNameField().textProperty().addListener((obs, oldVal, newVal) -> {
                // Handle username change if needed
            });
        }
    }


    private void sendMessage() {
        if (view.getMessageField() != null && view.getSendButton() != null) {
            String message = view.getMessageField().getText().trim();
            if (!message.isEmpty()) {
                view.getStatusLabel().setText("Đang gửi...");

                // Disable send button temporarily
                view.getSendButton().setDisable(true);

                // Handle send message
                handleSendMessage(message);

                // Scroll to bottom
            }
        }
    }

    private void clearChat() {
        model.getMessages().clear();
        model.addSystemMessage("Đã xóa tất cả tin nhắn");
        view.getStatusLabel().setText("Đã xóa chat");
    }



    // ChatModelListener implementation
    @Override
    public void onMessageAdded() {
        Platform.runLater(() -> {
            view.getStatusLabel().setText("New message");
        });
    }

    @Override
    public void onMessageReceived(String message) {
        Platform.runLater(() -> {
            if (message.startsWith("Private from ")) {
                // Xử lý tin nhắn riêng
                String[] parts = message.split(":", 2);
                if (parts.length == 2) {
                    String senderInfo = parts[0].replace("Private from ", "").trim();
                    String content = parts[1].trim();
                    model.addMessage(senderInfo, content, MessageType.BOT); // Có thể tạo MessageType.PRIVATE nếu muốn
                    view.getStatusLabel().setText("Receive new private message");
                }
            } else {
                // Xử lý tin nhắn broadcast hoặc hệ thống
                String[] parts = message.split(":", 2);
                if (parts.length == 2) {
                    String sender = parts[0].trim();
                    String content = parts[1].trim();
                    model.addMessage(sender, content, MessageType.BOT);
                } else {
                    model.addSystemMessage(message);
                }
                view.getStatusLabel().setText("Nhận tin nhắn mới");
            }
        });
    }

    @Override
    public void onConnectionStatusChanged(boolean connected) {
        Platform.runLater(() -> {
            view.updateConnectionStatus(connected);
            if (connected) {
                view.getStatusLabel().setText("Đã kết nối thành công");
                model.addSystemMessage("Đã kết nối tới server");
            } else {
                view.getStatusLabel().setText("Đã ngắt kết nối");
                model.addSystemMessage("Đã ngắt kết nối khỏi server");
            }
        });
    }

    @Override
    public void onError(String error) {
        Platform.runLater(() -> {
            view.showError(error);
            view.getStatusLabel().setText("Lỗi: " + error);
            model.addSystemMessage("Lỗi: " + error);
        });
    }
}