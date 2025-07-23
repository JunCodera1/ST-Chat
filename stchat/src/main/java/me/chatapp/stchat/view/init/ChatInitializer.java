package me.chatapp.stchat.view.init;

import javafx.scene.Scene;
import me.chatapp.stchat.AppContext;
import me.chatapp.stchat.api.ConversationApiClient;
import me.chatapp.stchat.api.SocketClient;
import me.chatapp.stchat.controller.ConversationController;
import me.chatapp.stchat.controller.MessageController;
import me.chatapp.stchat.model.User;
import me.chatapp.stchat.view.components.organisms.Bar.NavigationSidebar;
import me.chatapp.stchat.view.components.organisms.Header.ChatHeader;
import me.chatapp.stchat.view.components.organisms.Panel.ChatPanel;
import me.chatapp.stchat.view.components.organisms.Panel.MessageInputPanel;
import me.chatapp.stchat.view.components.organisms.Bar.StatusBar;
import me.chatapp.stchat.view.config.ChatViewConfig;
import me.chatapp.stchat.view.state.ChatViewStateManager;

import javafx.stage.Stage;
import java.io.IOException;
import java.util.function.Consumer;

public class ChatInitializer {

    public record ChatInitResult(
            SocketClient socketClient,
            ConversationController conversationController,
            MessageController messageController,
            ChatViewStateManager stateManager,
            NavigationSidebar navigationSidebar
    ) {}

    public static ChatInitResult initialize(
            ChatViewConfig config,
            User currentUser,
            Stage currentStage,
            Scene scene,
            ChatPanel chatPanel,
            MessageInputPanel messageInputPanel,
            StatusBar statusBar,
            ChatHeader chatHeader,
            Runnable logoutCallback,
            Consumer<String> showInfo
    ) throws Exception {

        SocketClient socketClient = new SocketClient("localhost", config.getPort());
        AppContext.getInstance().setSocketClient(socketClient);

        ConversationApiClient conversationApiClient = new ConversationApiClient();
        ConversationController conversationController = new ConversationController(conversationApiClient);


        MessageController messageController = new MessageController();

        ChatViewStateManager stateManager = new ChatViewStateManager(
                chatPanel, messageInputPanel, statusBar, scene
        );



        NavigationSidebar navigationSidebar = new NavigationSidebar(
                currentUser, currentStage, logoutCallback
        );

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
                socketClient,
                conversationController,
                messageController,
                stateManager,
                navigationSidebar
        );

    }
}
