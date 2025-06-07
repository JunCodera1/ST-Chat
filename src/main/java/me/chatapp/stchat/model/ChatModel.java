package me.chatapp.stchat.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ChatModel {
    private final ObservableList<Message> messages;
    private final StringProperty currentMessage;
    private final StringProperty userName;
    private final List<ChatModelListener> listeners;

    public ChatModel() {
        this.messages = FXCollections.observableArrayList();
        this.currentMessage = new SimpleStringProperty("");
        this.userName = new SimpleStringProperty("User");
        this.listeners = new ArrayList<>();

        // Add welcome message
        addSystemMessage("Chào mừng bạn đến với ST Chat!");
    }

    public void addMessage(String sender, String content, MessageType type) {
        Message message = new Message(sender, content, type, LocalDateTime.now());
        messages.add(message);
        notifyListeners();
    }

    public void addSystemMessage(String content) {
        addMessage("System", content, MessageType.SYSTEM);
    }

    public void addUserMessage(String content) {
        addMessage(userName.get(), content, MessageType.USER);
    }

    public void addBotMessage(String content) {
        addMessage("AI Assistant", content, MessageType.BOT);
    }

    public ObservableList<Message> getMessages() {
        return messages;
    }

    public StringProperty currentMessageProperty() {
        return currentMessage;
    }

    public StringProperty userNameProperty() {
        return userName;
    }

    public void addListener(ChatModelListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners() {
        for (ChatModelListener listener : listeners) {
            listener.onMessageAdded();
        }
    }

    public interface ChatModelListener {
        void onMessageAdded();
    }
}
