package me.chatapp.stchat.view.handlers;

import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import me.chatapp.stchat.api.FavouriteApiClient;
import me.chatapp.stchat.api.UserApiClient;
import me.chatapp.stchat.controller.MessageController;
import me.chatapp.stchat.model.User;
import me.chatapp.stchat.view.components.organisms.Bar.NavigationSidebar;
import me.chatapp.stchat.view.components.organisms.Header.ChatHeader;
import me.chatapp.stchat.view.components.organisms.Panel.ChatPanel;
import me.chatapp.stchat.view.components.pages.ChatView;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.feather.Feather;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class NavigationSidebarHandlerBinder {

    public static void bindHandlers(ChatView chatView) {
        NavigationSidebar navigationSidebar = chatView.getNavigationSidebar();
        ChatPanel chatPanel = chatView.getChatPanel();
        ChatHeader chatHeader = chatView.getChatHeader();
        MessageController messageController = chatView.getMessageController();
        User currentUser = chatView.getCurrentUser();

        navigationSidebar.setOnAddFavoriteClicked(() -> {
            UserApiClient userApiClient = new UserApiClient();
            FavouriteApiClient favouriteApiClient = new FavouriteApiClient();

            userApiClient.getAllUsers().thenAccept(users -> {
                System.out.println("🔍 All users fetched from server: " + users.size());

                List<String> allUsernames = users.stream()
                        .map(User::getUsername)
                        .filter(name -> !name.equals(currentUser.getUsername()))
                        .toList();

                System.out.println("✅ Filtered usernames (excluding self): " + allUsernames);

                List<String> alreadyAdded = navigationSidebar.getFavoriteUsernames();
                System.out.println("📌 Already added to favorites: " + alreadyAdded);

                List<String> selectableUsernames = allUsernames.stream()
                        .filter(name -> !alreadyAdded.contains(name))
                        .toList();

                System.out.println("🟢 Selectable usernames: " + selectableUsernames);

                Platform.runLater(() -> {
                    if (selectableUsernames.isEmpty()) {
                        chatView.showInfo("No more users", "All users are already in favorites.");
                        System.out.println("⚠️ No selectable users left to add.");
                        return;
                    }

                    List<String> emojiNames = selectableUsernames.stream()
                            .map(name -> "😊 " + name)
                            .toList();

                    ChoiceDialog<String> dialog = new ChoiceDialog<>(emojiNames.get(0), emojiNames);
                    dialog.setTitle("Add Favorite");
                    dialog.setHeaderText("👤 Select a user to add to favorites");
                    dialog.setContentText("Username:");

                    FontIcon icon = new FontIcon(Feather.USER_PLUS);
                    icon.setIconSize(30);
                    icon.setIconColor(Color.web("#5865F2"));
                    dialog.getDialogPane().setGraphic(icon);

                    dialog.getDialogPane().setStyle("-fx-background-color: #2f3136; -fx-padding: 20;");
                    dialog.getDialogPane().lookupButton(ButtonType.OK).setStyle("-fx-background-color: #5865f2; -fx-text-fill: white;");
                    dialog.getDialogPane().lookupButton(ButtonType.CANCEL).setStyle("-fx-background-color: #99aab5; -fx-text-fill: black;");

                    dialog.showAndWait().ifPresent(selectedUsername -> {
                        String username = selectedUsername.replace("😊 ", "").trim();
                        System.out.println("👉 Selected username: " + username);

                        // Tìm thông tin user từ username
                        userApiClient.findUserByUsername(username).ifPresentOrElse(targetUser -> {
                            System.out.println("✅ Found user in system: " + targetUser.getUsername() + " (ID: " + targetUser.getId() + ")");

                            // Gọi API lưu vào database
                            favouriteApiClient.addFavorite(currentUser.getId(), targetUser.getId()).thenAccept(success -> {
                                System.out.println("🛠 Sending favorite add request: from=" + currentUser.getId() + ", to=" + targetUser.getId());

                                if (success) {
                                    Platform.runLater(() -> {
                                        navigationSidebar.addFavorite(username, "😊", true);
                                        chatView.showInfo("✅ Success", username + " has been added to favorites.");
                                        System.out.println("🎉 Added to favorites: " + username);
                                    });
                                } else {
                                    Platform.runLater(() -> {
                                        chatView.showError("❌ Failed to add " + username + " to favorites.");
                                        System.out.println("❌ Server rejected addFavorite request.");
                                    });
                                }
                            }).exceptionally(ex -> {
                                ex.printStackTrace();
                                Platform.runLater(() -> chatView.showError("❌ Network error while adding favorite."));
                                return null;
                            });

                        }, () -> {
                            Platform.runLater(() -> chatView.showError("❌ User not found in system."));
                            System.out.println("❌ User not found in system: " + username);
                        });
                    });
                });
            }).exceptionally(ex -> {
                ex.printStackTrace();
                return null;
            });
        });




        navigationSidebar.setOnAddDirectMessageClicked(() -> {
            UserApiClient userApiClient = new UserApiClient();
            userApiClient.getAllUsers().thenAccept(users -> {
                List<String> availableUsernames = users.stream()
                        .map(User::getUsername)
                        .filter(name -> !name.equals(currentUser.getUsername())) // loại bỏ bản thân
                        .toList();

                Platform.runLater(() -> {
                    if (availableUsernames.isEmpty()) {
                        chatView.showInfo("No Users", "No other users found to message.");
                        return;
                    }

                    ChoiceDialog<String> dialog = new ChoiceDialog<>(availableUsernames.get(0), availableUsernames);
                    dialog.setTitle("Start Direct Message");
                    dialog.setHeaderText("💬 Select a user to message");
                    dialog.setContentText("Username:");

                    // Ikonli icon
                    FontIcon icon = new FontIcon(Feather.MESSAGE_CIRCLE);
                    icon.setIconSize(30);
                    icon.setIconColor(Color.web("#5865F2"));
                    dialog.getDialogPane().setGraphic(icon);

                    // Styling
                    dialog.getDialogPane().setStyle("-fx-background-color: #2f3136; -fx-padding: 20;");
                    dialog.getDialogPane().lookupButton(ButtonType.OK).setStyle("-fx-background-color: #5865f2; -fx-text-fill: white;");
                    dialog.getDialogPane().lookupButton(ButtonType.CANCEL).setStyle("-fx-background-color: #99aab5; -fx-text-fill: black;");

                    dialog.showAndWait().ifPresent(selectedUsername -> {
                        navigationSidebar.addDirectMessage(selectedUsername, "💬", true, "1");
                        System.out.println("✅ Started DM with: " + selectedUsername);
                    });
                });
            }).exceptionally(ex -> {
                ex.printStackTrace();
                Platform.runLater(() -> chatView.showError("Failed to load users for Direct Message."));
                return null;
            });
        });


        navigationSidebar.setOnAddChannelClicked(() -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("➕ Create Channel");
            dialog.setHeaderText("Enter new channel name");
            dialog.setContentText("Channel name:");

            // Giao diện nâng cấp
            FontIcon icon = new FontIcon(Feather.HASH);
            icon.setIconSize(30);
            icon.setIconColor(Color.web("#FBBF24")); // vàng

            dialog.getDialogPane().setGraphic(icon);
            dialog.getDialogPane().setStyle("-fx-background-color: #2f3136; -fx-padding: 20;");
            dialog.getDialogPane().lookupButton(ButtonType.OK).setStyle("-fx-background-color: #5865f2; -fx-text-fill: white;");
            dialog.getDialogPane().lookupButton(ButtonType.CANCEL).setStyle("-fx-background-color: #99aab5; -fx-text-fill: black;");

            dialog.showAndWait().ifPresent(channelName -> {
                channelName = channelName.trim();

                boolean invalidName = channelName.isEmpty() || channelName.contains(" ");
                @org.jetbrains.annotations.NotNull String finalChannelName = channelName;
                boolean alreadyExists = navigationSidebar.getComponent().getChildren().stream()
                        .flatMap(node -> {
                            if (node instanceof VBox section) {
                                return section.getChildren().stream();
                            }
                            return Stream.empty();
                        })
                        .filter(node -> node instanceof HBox || node instanceof Label)
                        .anyMatch(node -> node.toString().contains(finalChannelName));

                if (invalidName) {
                    Platform.runLater(() -> chatView.showError("❌ Invalid channel name. No spaces allowed."));
                    return;
                }

                if (alreadyExists) {
                    Platform.runLater(() -> chatView.showInfo("⚠️ Duplicate Channel", "Channel already exists."));
                    return;
                }

                // Thêm channel
                navigationSidebar.addChannel(channelName, "#", false, false);
                System.out.println("✅ Created new channel: #" + channelName);
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

    public static void fetchFavoritesForUser(User user, NavigationSidebar navigationSidebar, ChatView chatView) {
        FavouriteApiClient favouriteApiClient = new FavouriteApiClient();
        UserApiClient userApiClient = new UserApiClient(); // 👈 bạn cần có class này

        favouriteApiClient.getFavouritesByUserId(user.getId()).thenCompose(favouriteList -> {
            List<CompletableFuture<User>> userFutures = favouriteList.stream()
                    .map(fav -> userApiClient.getUserById(fav.getFavoriteUserId()))
                    .toList();

            // Gộp tất cả futures lại
            return CompletableFuture.allOf(userFutures.toArray(new CompletableFuture[0]))
                    .thenApply(v -> userFutures.stream()
                            .map(CompletableFuture::join)
                            .toList());
        }).thenAccept(favoriteUsers -> {
            Platform.runLater(() -> {
                for (User favUser : favoriteUsers) {
                    navigationSidebar.addFavorite(favUser.getUsername(), "😊", true);
                }
                System.out.println("✅ Loaded " + favoriteUsers.size() + " favorites for user: " + user.getUsername());
            });
        }).exceptionally(ex -> {
            ex.printStackTrace();
            Platform.runLater(() -> chatView.showError("❌ Failed to load favorite users"));
            return null;
        });
    }

}
