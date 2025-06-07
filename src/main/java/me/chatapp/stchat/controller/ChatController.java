package me.chatapp.stchat.controller;

import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import me.chatapp.stchat.model.ChatModel;
import me.chatapp.stchat.service.ChatService;
import me.chatapp.stchat.view.ChatView;

public class ChatController implements ChatModel.ChatModelListener {
    private final ChatModel model;
    private final ChatView view;
    private final ChatService chatService;

    public ChatController(ChatModel model, ChatView view) {
        this.model = model;
        this.view = view;
        this.chatService = new ChatService();

        // Register as listener
        model.addListener(this);

        setupEventHandlers();
        bindProperties();
    }

    public void initialize() {
        view.getStatusLabel().setText("Ứng dụng đã sẵn sàng");
        view.getInputField().requestFocus();
    }

    private void setupEventHandlers() {
        // Send button click
        view.getSendButton().setOnAction(e -> sendMessage());

        // Enter key press in input field
        view.getInputField().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                sendMessage();
            }
        });

        // Clear button click
        view.getClearButton().setOnAction(e -> clearChat());

        // Username field change
        view.getUserNameField().textProperty().addListener((obs, oldVal, newVal) -> {
            model.userNameProperty().set(newVal.trim().isEmpty() ? "User" : newVal.trim());
        });
    }

    private void bindProperties() {
        // Bind message list
        view.getMessageListView().setItems(model.getMessages());

        // Bind current message
        view.getInputField().textProperty().bindBidirectional(model.currentMessageProperty());
    }

    private void sendMessage() {
        String message = model.currentMessageProperty().get().trim();
        if (message.isEmpty()) {
            return;
        }

        // Add user message
        model.addUserMessage(message);

        // Clear input
        model.currentMessageProperty().set("");

        // Update status
        view.getStatusLabel().setText("Đang xử lý...");

        // Disable send button temporarily
        view.getSendButton().setDisable(true);

        // Process message asynchronously
        chatService.processMessage(message, response -> {
            Platform.runLater(() -> {
                model.addBotMessage(response);
                view.getStatusLabel().setText("Sẵn sàng");
                view.getSendButton().setDisable(false);
                view.getInputField().requestFocus();
            });
        });

        // Scroll to bottom
        scrollToBottom();
    }

    private void clearChat() {
        model.getMessages().clear();
        model.addSystemMessage("Đã xóa tất cả tin nhắn");
        view.getStatusLabel().setText("Đã xóa chat");
    }

    private void scrollToBottom() {
        Platform.runLater(() -> {
            int lastIndex = model.getMessages().size() - 1;
            if (lastIndex >= 0) {
                view.getMessageListView().scrollTo(lastIndex);
            }
        });
    }

    @Override
    public void onMessageAdded() {
        scrollToBottom();
    }
}
