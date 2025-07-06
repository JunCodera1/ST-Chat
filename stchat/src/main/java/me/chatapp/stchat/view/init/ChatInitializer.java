package me.chatapp.stchat.view.init;

import javafx.scene.Scene;
import me.chatapp.stchat.AppContext;
import me.chatapp.stchat.api.SocketClient;
import me.chatapp.stchat.controller.MessageController;
import me.chatapp.stchat.model.User;
import me.chatapp.stchat.view.components.organisms.Bar.NavigationSidebar;
import me.chatapp.stchat.view.components.organisms.Header.ChatHeader;
import me.chatapp.stchat.view.components.organisms.Panel.ChatPanel;
import me.chatapp.stchat.view.components.organisms.Panel.ConnectionPanel;
import me.chatapp.stchat.view.components.organisms.Panel.MessageInputPanel;
import me.chatapp.stchat.view.components.organisms.Bar.StatusBar;
import me.chatapp.stchat.view.config.ChatViewConfig;
import me.chatapp.stchat.view.handlers.ChatViewEventHandler;
import me.chatapp.stchat.view.state.ChatViewStateManager;

import javafx.stage.Stage;
import java.io.IOException;
import java.util.function.Consumer;

public class ChatInitializer {

    public record ChatInitResult(
            SocketClient socketClient,
            MessageController messageController,
            ChatViewStateManager stateManager,
            ChatViewEventHandler eventHandler,
            NavigationSidebar navigationSidebar
    ) {}

    public static ChatInitResult initialize(
            ChatViewConfig config,
            User currentUser,
            Stage currentStage,
            Scene scene,
            ConnectionPanel connectionPanel,
            ChatPanel chatPanel,
            MessageInputPanel messageInputPanel,
            StatusBar statusBar,
            ChatHeader chatHeader,
            Runnable logoutCallback,
            Consumer<String> showInfo,
            Consumer<String> showError
    ) throws IOException {

        // Khởi tạo SocketClient
        SocketClient socketClient = new SocketClient("localhost", config.getPort());
        AppContext.getInstance().setSocketClient(socketClient);

        // Khởi tạo MessageController
        MessageController messageController = new MessageController();

        // State Manager
        ChatViewStateManager stateManager = new ChatViewStateManager(
                connectionPanel, chatPanel, messageInputPanel, statusBar, scene
        );

        // Event Handler
        ChatViewEventHandler eventHandler = new ChatViewEventHandler(
                config.getPort(), connectionPanel, chatPanel, messageInputPanel, stateManager
        );
        eventHandler.setSocketClient(socketClient);

        // Sidebar + gán callback
        NavigationSidebar navigationSidebar = new NavigationSidebar(
                currentUser, currentStage, logoutCallback
        );

        // Gán xử lý header
        chatHeader.setOnCallAction(() -> {
            String name = currentUser != null ? currentUser.getUsername() : "Unknown";
            new me.chatapp.stchat.view.components.organisms.Window.CallWindow(name).show();
        });

        chatHeader.setOnVideoCallAction(() -> {
            String userName = currentUser != null ? currentUser.getUsername() : "You";
            String contactName = "Unknown";
            new me.chatapp.stchat.view.components.organisms.Window.VideoCallWindow(userName, contactName).show();
        });

        chatHeader.setOnInfoAction(() -> showInfo.accept("Conversation details coming soon!"));

        // Typing indicator
        messageInputPanel.getMessageField().textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.isEmpty() && (oldText == null || oldText.isEmpty())) {
                if (currentUser != null) {
                    chatPanel.showTypingIndicator(currentUser.getUsername());
                }
            } else if (newText.isEmpty() && oldText != null && !oldText.isEmpty()) {
                chatPanel.hideTypingIndicator();
            }
        });

        return new ChatInitResult(
                socketClient, messageController, stateManager, eventHandler, navigationSidebar
        );
    }
}
