package me.chatapp.stchat.controller;

import javafx.application.Platform;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import me.chatapp.stchat.api.SocketClient;
import me.chatapp.stchat.api.UserApiClient;
import me.chatapp.stchat.model.User;
import me.chatapp.stchat.view.components.organisms.Bar.NavigationSidebar;
import me.chatapp.stchat.view.components.organisms.Panel.ChatPanel;

public class ChatController {
    private final NavigationSidebar navigationSidebar;
    private final ChatPanel chatPanel;
    private final SocketClient socketClient;
    private final User currentUser;
    private final UserApiClient userApiClient = new UserApiClient();
    private final HBox mainLayout;

    private final ConversationController conversationController;
    private final MessageController messageController;

    private String currentSelectedContact = null;
    private int currentConversationId = -1;

    public ChatController(User currentUser, SocketClient socketClient, Stage stage) {
        this.currentUser = currentUser;
        this.socketClient = socketClient;

        this.conversationController = new ConversationController();
        this.messageController = new MessageController();

        this.navigationSidebar = new NavigationSidebar(currentUser, socketClient, stage, this::onSettingsClicked);
        this.chatPanel = new ChatPanel(currentUser, this::onSendMessage);

        this.mainLayout = new HBox();
        this.mainLayout.getChildren().addAll(navigationSidebar.getComponent(), chatPanel.getComponent());
    }

    public void initialize() {
        setupEventHandlers();
        setupSocketListeners();
    }

    private void setupEventHandlers() {
        navigationSidebar.setOnDirectMessageSelected(this::onDirectMessageSelected);
        navigationSidebar.setOnChannelSelected(this::onChannelSelected);
        navigationSidebar.setOnNavigationItemSelected(this::onNavigationItemSelected);
    }

    private void onDirectMessageSelected(String userName) {
        currentSelectedContact = userName;
        currentConversationId = conversationController.getOrCreateConversationId(userName);
        chatPanel.setCurrentContact(userName, "user");
        messageController.loadMessagesForConversation(currentConversationId, chatPanel);
    }

    private void onChannelSelected(String channelName) {
        currentSelectedContact = channelName;
        currentConversationId = conversationController.getOrCreateConversationId(channelName);
        chatPanel.setCurrentContact(channelName, "channel");
        messageController.loadMessagesForConversation(currentConversationId, chatPanel);
    }

    private void onSendMessage(String content) {
        if (currentConversationId == -1 || currentSelectedContact == null) {
            System.out.println("No contact selected");
            return;
        }

        messageController.sendMessage(currentUser, content, currentConversationId, chatPanel, socketClient);
    }

    private void setupSocketListeners() {
        socketClient.setMessageListener(message -> {
            Platform.runLater(() -> {
                if (message.getConversationId() == currentConversationId) {
                    chatPanel.addMessage(message);
                }
            });
        });
    }

    private void loadDirectMessages() {
        userApiClient.getAllUsers()
                .thenAccept(users -> {
                    Platform.runLater(() -> {
                        navigationSidebar.clearDirectMessages(); // bạn cần viết hàm này
                        for (User u : users) {
                            navigationSidebar.addDirectMessage(u.getUsername(), "👤", true, null);
                        }
                    });
                })
                .exceptionally(throwable -> {
                    System.err.println("Error loading users: " + throwable.getMessage());
                    return null;
                });
    }

    private void onNavigationItemSelected(String item) {
        System.out.println("Selected navigation item: " + item);
    }

    private void onSettingsClicked() {
        System.out.println("Settings clicked");
    }

    public HBox getMainLayout() {
        return mainLayout;
    }

    public void cleanup() {
        messageController.close();
    }

}
