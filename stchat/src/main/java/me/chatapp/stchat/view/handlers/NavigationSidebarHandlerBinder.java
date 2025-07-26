package me.chatapp.stchat.view.handlers;

import javafx.application.Platform;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import me.chatapp.stchat.api.ConversationApiClient;
import me.chatapp.stchat.api.FavouriteApiClient;
import me.chatapp.stchat.api.UserApiClient;
import me.chatapp.stchat.controller.ConversationController;
import me.chatapp.stchat.controller.MessageController;
import me.chatapp.stchat.model.Favourite;
import me.chatapp.stchat.model.User;
import me.chatapp.stchat.view.components.organisms.Bar.NavigationSidebar;
import me.chatapp.stchat.view.components.organisms.Header.ChatHeader;
import me.chatapp.stchat.view.components.organisms.Panel.ChatPanel;
import me.chatapp.stchat.view.components.pages.ChatView;
import org.kordamp.ikonli.feather.Feather;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static me.chatapp.stchat.util.DisplayUtil.createStyledChoiceDialog;
import static me.chatapp.stchat.util.DisplayUtil.styleDialog;

public class NavigationSidebarHandlerBinder {

    public static void bindHandlers(ChatView chatView) {
        NavigationSidebar navigationSidebar = chatView.getNavigationSidebar();
        ChatPanel chatPanel = chatView.getChatPanel();
        ChatHeader chatHeader = chatView.getChatHeader();
        MessageController messageController = chatView.getMessageController();
        User currentUser = chatView.getCurrentUser();
        ConversationController conversationController = new ConversationController(new ConversationApiClient());

        setupAddFavoriteHandler(chatView, navigationSidebar, currentUser);
        setupAddDirectMessageHandler(chatView, navigationSidebar, currentUser);
        setupAddChannelHandler(chatView, navigationSidebar);
        setupChannelSelectionHandler(chatView, chatPanel, chatHeader, messageController, conversationController);
        setupDirectMessageSelectionHandler(chatView, chatPanel, chatHeader, messageController, currentUser, conversationController);
        setupRemoveFavoriteHandler(chatView,navigationSidebar,currentUser);

        navigationSidebar.setOnSettingsClicked(() ->
                chatView.showInfo("Settings", "Settings panel will be implemented soon!"));
    }

    private static void setupAddFavoriteHandler(ChatView chatView, NavigationSidebar navigationSidebar, User currentUser) {
        navigationSidebar.setOnAddFavoriteClicked(() -> {
            UserApiClient userApiClient = new UserApiClient();
            FavouriteApiClient favouriteApiClient = new FavouriteApiClient();

            userApiClient.getAllUsers().thenAccept(users -> {
                List<String> allUsernames = users.stream()
                        .map(User::getUsername)
                        .filter(name -> !name.equals(currentUser.getUsername()))
                        .toList();

                favouriteApiClient.getFavouritesByUserId(currentUser.getId()).thenCompose(favoriteList -> {
                    List<Integer> favoriteIds = favoriteList.stream()
                            .map(Favourite::getFavoriteUserId)
                            .toList();

                    List<String> alreadyAddedUsernames = users.stream()
                            .filter(u -> favoriteIds.contains(u.getId()))
                            .map(User::getUsername)
                            .toList();

                    List<String> selectableUsernames = allUsernames.stream()
                            .filter(name -> !alreadyAddedUsernames.contains(name))
                            .toList();

                    return CompletableFuture.completedFuture(selectableUsernames);
                }).thenAccept(selectableUsernames -> {
                    Platform.runLater(() -> {
                        if (selectableUsernames.isEmpty()) {
                            chatView.showInfo("No more users", "All users are already in favorites.");
                            System.out.println("All usernames: " + allUsernames);
                            System.out.println("Already added favorites: " + users);
                            System.out.println("Selectable usernames: " + selectableUsernames);

                            return;
                        }

                        List<String> emojiNames = selectableUsernames.stream()
                                .map(name -> "😊 " + name)
                                .toList();

                        ChoiceDialog<String> dialog = createStyledChoiceDialog("Add Favorite",
                                "👤 Select a user to add to favorites", emojiNames, Feather.USER_PLUS);

                        dialog.showAndWait().ifPresent(selectedUsername -> {
                            String username = selectedUsername.replace("😊 ", "").trim();

                            userApiClient.findUserByUsername(username).ifPresentOrElse(targetUser -> {
                                favouriteApiClient.addFavorite(currentUser.getId(), targetUser.getId()).thenAccept(success -> {
                                    if (success) {
                                        Platform.runLater(() -> {
                                            navigationSidebar.addFavorite(targetUser);
                                            chatView.showInfo("✅ Success", username + " has been added to favorites.");
                                        });
                                    } else {
                                        Platform.runLater(() -> chatView.showError("❌ Failed to add " + username + " to favorites."));
                                    }
                                }).exceptionally(ex -> {
                                    ex.printStackTrace();
                                    Platform.runLater(() -> chatView.showError("❌ Network error while adding favorite."));
                                    return null;
                                });
                            }, () -> Platform.runLater(() -> chatView.showError("❌ User not found in system.")));
                        });
                    });
                }).exceptionally(ex -> {
                    ex.printStackTrace();
                    Platform.runLater(() -> chatView.showError("❌ Failed to fetch current favorites."));
                    return null;
                });
            });
        });
    }


    private static void setupAddDirectMessageHandler(ChatView chatView, NavigationSidebar navigationSidebar, User currentUser) {
        navigationSidebar.setOnAddDirectMessageClicked(() -> {
            UserApiClient userApiClient = new UserApiClient();
            userApiClient.getAllUsers().thenAccept(users -> {
                List<String> availableUsernames = users.stream()
                        .map(User::getUsername)
                        .filter(name -> !name.equals(currentUser.getUsername()))
                        .toList();

                Platform.runLater(() -> {
                    if (availableUsernames.isEmpty()) {
                        chatView.showInfo("No Users", "No other users found to message.");
                        return;
                    }

                    ChoiceDialog<String> dialog = createStyledChoiceDialog("Start Direct Message",
                            "Select a user to message", availableUsernames, Feather.MESSAGE_CIRCLE);

                    dialog.showAndWait().ifPresent(selectedUsername -> {
                        navigationSidebar.addDirectMessage(selectedUsername, currentUser.getAvatarUrl(), currentUser.isActive(), "1");
                    });
                });
            });
        });
    }

    private static void setupAddChannelHandler(ChatView chatView, NavigationSidebar navigationSidebar) {
        navigationSidebar.setOnAddChannelClicked(() -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("➕ Create Channel");
            dialog.setHeaderText("Enter new channel name");
            dialog.setContentText("Channel name:");

            styleDialog(dialog, Color.web("#FBBF24"));

            dialog.showAndWait().ifPresent(channelName -> {
                channelName = channelName.trim();
                boolean invalidName = channelName.isEmpty() || channelName.contains(" ");
                @org.jetbrains.annotations.NotNull String finalChannelName = channelName;
                boolean alreadyExists = navigationSidebar.getComponent().getChildren().stream()
                        .flatMap(node -> node instanceof VBox section ? section.getChildren().stream() : Stream.empty())
                        .anyMatch(node -> node.toString().contains(finalChannelName));

                if (invalidName) {
                    Platform.runLater(() -> chatView.showError("❌ Invalid channel name. No spaces allowed."));
                } else if (alreadyExists) {
                    Platform.runLater(() -> chatView.showInfo("Duplicate Channel", "Channel already exists."));
                } else {
                    navigationSidebar.addChannel(channelName, "#", false, false);
                }
            });
        });
    }

    private static void setupChannelSelectionHandler(ChatView chatView, ChatPanel chatPanel, ChatHeader chatHeader, MessageController messageController, ConversationController conversationController) {
        chatView.getNavigationSidebar().setOnChannelSelected(channelName -> {
            conversationController.createChannelConversation(channelName)
                    .thenAccept(convId -> {
                        chatView.setCurrentConversationId(convId);
                        Platform.runLater(() -> {
                            chatView.setCurrentContactName(channelName);
                            chatHeader.setActiveConversation(channelName);
                            chatPanel.setCurrentContact(channelName, "channel");
                            chatPanel.clearMessages();
                            if (messageController != null) {
                                messageController.loadMessagesForConversation(convId, chatPanel);
                            }
                        });
                    })
                    .exceptionally(ex -> {
                        ex.printStackTrace();
                        Platform.runLater(() -> chatView.showError("❌ Failed to create or fetch conversation for channel: " + channelName));
                        return null;
                    });
        });
    }

    public static void setupDirectMessageSelectionHandler(ChatView chatView, ChatPanel chatPanel,
                                                           ChatHeader chatHeader,
                                                           MessageController messageController,
                                                           User currentUser,
                                                           ConversationController conversationController) {
        chatView.getNavigationSidebar().setOnDirectMessageSelected(userName -> {
            chatView.setCurrentContactName(userName);
            UserApiClient userApiClient = new UserApiClient();
            System.out.println("[DEBUG] Clicked on user in sidebar: " + userName);
            userApiClient.findUserByUsername(userName).ifPresentOrElse(targetUser -> {
                System.out.println("[DEBUG] Found user: " + targetUser.getUsername() + " (id=" + targetUser.getId() + ")");
                int currentUserId = currentUser.getId();
                int targetUserId = targetUser.getId();

                conversationController.createConversationId(currentUserId, targetUserId)
                        .thenAccept(convId -> {
                            System.out.println("[DEBUG] Got conversationId: " + convId);
                            System.out.println("[DEBUG] Got targetUserId: " + targetUserId);
                            System.out.println("[DEBUG] Got currentUserId: " + currentUserId);

                            chatView.setCurrentConversationId(convId);
                            chatPanel.setConversationId(convId);
                            messageController.setActiveChatPanel(chatPanel, convId);

                            Platform.runLater(() -> {
                                chatHeader.setActiveUser(targetUser);
                                chatPanel.setCurrentContact(userName, "user");
                                chatPanel.clearMessages();
                                chatView.getMessageInputPanel().setSender(currentUser);
                                chatView.getMessageInputPanel().setReceiver(targetUser);
                                messageController.loadMessagesForConversation(convId, chatPanel);
                            });
                        });

            }, () -> Platform.runLater(() -> chatView.showError("❌ Cannot find user: " + userName)));
        });
    }

    public static void fetchFavoritesForUser(User user, NavigationSidebar navigationSidebar, ChatView chatView) {
        FavouriteApiClient favouriteApiClient = new FavouriteApiClient();
        UserApiClient userApiClient = new UserApiClient();

        favouriteApiClient.getFavouritesByUserId(user.getId()).thenCompose(favouriteList -> {
            List<CompletableFuture<Optional<User>>> userFutures = favouriteList.stream()
                    .map(fav -> userApiClient.getUserById(fav.getFavoriteUserId()))
                    .toList();

            return CompletableFuture.allOf(userFutures.toArray(new CompletableFuture[0]))
                    .thenApply(v -> userFutures.stream()
                            .map(CompletableFuture::join)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .toList());
        }).thenAccept(favoriteUsers -> {
            Platform.runLater(() -> {
                favoriteUsers.forEach(navigationSidebar::addFavorite);
            });
        }).exceptionally(ex -> {
            ex.printStackTrace();
            Platform.runLater(() -> chatView.showError("❌ Failed to load favorite users"));
            return null;
        });
    }

    private static void setupRemoveFavoriteHandler(ChatView chatView, NavigationSidebar navigationSidebar, User currentUser) {
        FavouriteApiClient favouriteApiClient = new FavouriteApiClient();
        UserApiClient userApiClient = new UserApiClient();

        navigationSidebar.setOnRemoveFavoriteClicked(username -> {
            Platform.runLater(() -> {
                userApiClient.findUserByUsername(username).ifPresentOrElse(targetUser -> {
                    favouriteApiClient.removeFavorite(currentUser.getId(), targetUser.getId())
                            .thenAccept(success -> {
                                if (success) {
                                    Platform.runLater(() -> {
                                        navigationSidebar.removeFavorite(username); // Cập nhật giao diện
                                        chatView.showInfo("✅ Removed", username + " has been removed from favorites.");
                                    });
                                } else {
                                    Platform.runLater(() -> chatView.showError("❌ Failed to remove " + username + " from favorites."));
                                }
                            }).exceptionally(ex -> {
                                ex.printStackTrace();
                                Platform.runLater(() -> chatView.showError("❌ Network error while removing favorite."));
                                return null;
                            });
                }, () -> Platform.runLater(() -> chatView.showError("❌ User not found: " + username)));
            });
        });
    }


}
