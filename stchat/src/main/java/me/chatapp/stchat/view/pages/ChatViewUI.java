package me.chatapp.stchat.view.pages;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import me.chatapp.stchat.view.components.*;
import me.chatapp.stchat.view.config.ChatViewConfig;
import me.chatapp.stchat.view.layout.ChatViewLayoutManager;
import me.chatapp.stchat.view.state.ChatViewStateManager;

public class ChatViewUI {
    private final ChatViewConfig config;
    private final BorderPane root;
    private final ChatViewStateManager stateManager;

    public ChatViewUI() {
        this.config = new ChatViewConfig();
        this.root = new BorderPane();

        HeaderComponent header = new HeaderComponent();
        ConnectionPanel connection = new ConnectionPanel();
        ChatPanel chat = new ChatPanel();
        MessageInputPanel input = new MessageInputPanel();
        StatusBar status = new StatusBar();

        new ChatViewLayoutManager(root, header, connection, chat, input, status);
        this.stateManager = new ChatViewStateManager(connection, chat, input, status, new Scene(root));
    }

    public Parent getView() {
        return root;
    }
}
