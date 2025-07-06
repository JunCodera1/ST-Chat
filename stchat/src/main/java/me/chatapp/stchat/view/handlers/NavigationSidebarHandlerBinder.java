package me.chatapp.stchat.view.handlers;

import javafx.application.Platform;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import me.chatapp.stchat.api.UserApiClient;
import me.chatapp.stchat.controller.MessageController;
import me.chatapp.stchat.model.User;
import me.chatapp.stchat.view.components.organisms.Bar.NavigationSidebar;
import me.chatapp.stchat.view.components.organisms.Header.ChatHeader;
import me.chatapp.stchat.view.components.organisms.Panel.ChatPanel;
import me.chatapp.stchat.view.components.pages.ChatView;

import java.util.List;

public class NavigationSidebarHandlerBinder {

    public static void bindHandlers(ChatView chatView) {
        NavigationSidebar navigationSidebar = chatView.getNavigationSidebar();
        ChatPanel chatPanel = chatView.getChatPanel();
        ChatHeader chatHeader = chatView.getChatHeader();
        MessageController messageController = chatView.getMessageController();
        User currentUser = chatView.getCurrentUser();

        // Thêm yêu thích
        navigationSidebar.setOnAddFavoriteClicked(() -> {
            UserApiClient userApiClient = new UserApiClient();
            userApiClient.getAllUsers().thenAccept(users -> {
                List<String> usernames = users.stream()
                        .map(User::getUsername)
                        .filter(name -> !name.equals(currentUser.getUsername()))
                        .toList();

                Platform.runLater(() -> {
                    ChoiceDialog<String> dialog = new ChoiceDialog<>(
                            usernames.isEmpty() ? null : usernames.get(0), usernames);
                    dialog.setTitle("Add Favorite");
                    dialog.setHeaderText("Select a user to add to favorites");
                    dialog.setContentText("Username:");

                    dialog.showAndWait().ifPresent(selectedUsername -> {
                        navigationSidebar.addFavorite(selectedUsername, "😊", true);
                        System.out.println("Added to favorites: " + selectedUsername);
                    });
                });
            }).exceptionally(ex -> {
                ex.printStackTrace();
                return null;
            });
        });

        // Thêm Direct Message
        navigationSidebar.setOnAddDirectMessageClicked(() -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("New Direct Message");
            dialog.setHeaderText("Enter username");
            dialog.setContentText("Username:");

            dialog.showAndWait().ifPresent(username -> {
                if (!username.trim().isEmpty()) {
                    navigationSidebar.addDirectMessage(username, "💬", true, "1");
                    System.out.println("Started direct message with: " + username);
                }
            });
        });

        // Thêm Channel
        navigationSidebar.setOnAddChannelClicked(() -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Create Channel");
            dialog.setHeaderText("Enter channel name");
            dialog.setContentText("Channel name:");

            dialog.showAndWait().ifPresent(channelName -> {
                if (!channelName.trim().isEmpty()) {
                    navigationSidebar.addChannel(channelName, "#", false, false);
                    System.out.println("Created new channel: " + channelName);
                }
            });
        });

        // Điều hướng
        navigationSidebar.setOnNavigationItemSelected(item -> {
            switch (item) {
                case "chats" -> chatView.showInfo("Navigation", "Switched to Chats view");
                case "threads" -> chatView.showInfo("Navigation", "Switched to Threads view");
                case "calls" -> chatView.showInfo("Navigation", "Switched to Calls view");
                case "bookmarks" -> chatView.showInfo("Navigation", "Switched to Bookmarks view");
            }
        });

        // Channel selected
        navigationSidebar.setOnChannelSelected(channelName -> {
            chatView.setCurrentContactName(channelName);
            int convId = chatView.getOrCreateConversationId(channelName);
            chatView.setCurrentConversationId(convId);
            chatHeader.setActiveConversation(channelName);
            chatPanel.setCurrentContact(channelName, "channel");
            chatPanel.clearMessages();

            if (messageController != null) {
                messageController.loadMessagesForConversation(convId, chatPanel);
            }
        });

        // Direct message selected
        navigationSidebar.setOnDirectMessageSelected(userName -> {
            chatView.setCurrentContactName(userName);
            int convId = chatView.getOrCreateConversationId(userName);
            chatView.setCurrentConversationId(convId);
            chatHeader.setActiveConversation(userName);
            chatPanel.setCurrentContact(userName, "user");
            chatPanel.clearMessages();

            if (messageController != null) {
                messageController.loadMessagesForConversation(convId, chatPanel);
            }
        });

        // Settings
        navigationSidebar.setOnSettingsClicked(() ->
                chatView.showInfo("Settings", "Settings panel will be implemented soon!"));
    }
}
