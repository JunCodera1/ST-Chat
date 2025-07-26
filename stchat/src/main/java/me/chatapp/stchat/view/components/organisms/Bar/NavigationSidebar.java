package me.chatapp.stchat.view.components.organisms.Bar;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import me.chatapp.stchat.AppContext;
import me.chatapp.stchat.api.UserApiClient;
import me.chatapp.stchat.model.User;
import me.chatapp.stchat.api.SocketClient;
import me.chatapp.stchat.view.components.molecules.Item.ChannelItem;
import me.chatapp.stchat.view.components.molecules.Item.DirectMessageItem;
import me.chatapp.stchat.view.components.organisms.Footer.SidebarFooter;
import me.chatapp.stchat.view.components.pages.ChatView;
import me.chatapp.stchat.view.factories.*;
import me.chatapp.stchat.view.handlers.NavigationSidebarHandlerBinder;
import me.chatapp.stchat.view.init.SceneManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class NavigationSidebar {

    private final VBox root;
    private final VBox favoritesContainer;
    private final VBox directMessagesContainer;
    private final VBox channelsContainer;
    private final ScrollPane scrollPane;
    private User currentUser;
    private final Set<String> favoriteUsernameSet = new HashSet<>();
    private SidebarFooter footer;

    private Runnable onAddFavoriteClicked;
    private Runnable onAddDirectMessageClicked;
    private Runnable onAddChannelClicked;

    private VBox currentMainContent;
    private VBox originalContent;

    private ChatView chatView;


    private Consumer<String> onChannelSelected;
    private Consumer<String> onDirectMessageSelected;
    private Runnable onSettingsClicked;
    private Consumer<String> onRemoveFavoriteClicked;

    private final Stage stage;
    Stage currentStage = SceneManager.getStage();
    private final SocketClient socketClient = AppContext.getInstance().getSocketClient();

    public NavigationSidebar(User currentUser , Stage stage, Runnable onSettingsClicked) {
        this.currentUser = currentUser;
        this.stage = stage;
        this.onSettingsClicked = onSettingsClicked;
        this.root = new VBox();
        this.favoritesContainer = new VBox();
        this.directMessagesContainer = new VBox();
        this.channelsContainer = new VBox();
        this.scrollPane = new ScrollPane();

        this.footer = new SidebarFooter(currentUser, socketClient, currentStage, onSettingsClicked);
        setupDefaultItems();
        this.originalContent = createMainContent();
        initializeComponent();

        this.currentMainContent = this.originalContent;
    }

    public void switchContent(String contentType) {
        VBox newContent = null;

        switch (contentType.toLowerCase()) {
            case "chats":
            case "default":
                newContent = createMainContent();
                originalContent = newContent;
                break;
            case "profile":
                newContent = createProfileContent();
                break;
            case "messages":
                newContent = createDirectMessagesContent();
                break;
            case "calls":
                newContent = createCallsContent();
                break;
            case "bookmarks":
                newContent = createBookmarksContent();
                break;
            case "settings":
                newContent = createSettingsContent();
                break;
            default:
                newContent = originalContent;
        }

        if (newContent != null) {
            currentMainContent = newContent;
            updateScrollContent();

            if ("chats".equalsIgnoreCase(contentType) && currentUser != null) {
                Platform.runLater(() -> {
                    NavigationSidebarHandlerBinder.fetchFavoritesForUser(currentUser, this, chatView);
                });
            }
        }
    }
    public void refreshFavorites(ChatView chatView) {
        if (currentUser != null) {
            NavigationSidebarHandlerBinder.fetchFavoritesForUser(currentUser, this, chatView);
        }
    }
    private void updateScrollContent() {
        scrollPane.setContent(currentMainContent);
    }

    private VBox createProfileContent() {
        return ProfileViewFactory.create(currentUser, currentUser, () -> switchContent("chats"));
    }

    private VBox createOtherUserProfile(User profileUser) {
        return ProfileViewFactory.create(currentUser, profileUser, () -> switchContent("chats"));
    }


    private VBox createDirectMessagesContent() {
        return ContactViewFactory.create(() -> switchContent("chats"));
    }

    private VBox createCallsContent() {
        return CallsViewFactory.create(() -> switchContent("chats"));
    }


    private VBox createBookmarksContent() {
        return BookmarksViewFactory.create(() -> switchContent("chats"));
    }


    private VBox createSettingsContent() {
        return SettingsViewFactory.create(() -> switchContent("chats"));
    }
    public void clearFavorites() {
        System.out.println("🧹 Clearing favorites. Current count: " + favoritesContainer.getChildren().size());
        favoritesContainer.getChildren().clear();
        System.out.println("✅ Favorites cleared. New count: " + favoritesContainer.getChildren().size());
    }

    private void initializeComponent() {
        root.setPrefWidth(280);
        root.setMinWidth(500);
        root.setMaxWidth(570);
        root.setStyle("-fx-background-color: #1a1d21; -fx-padding: 0;");

        // Header with workspace name and search
        VBox header = createHeader();

        // Main content with scroll
        VBox mainContent = createMainContent();
        scrollPane.setContent(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: #1a1d21; -fx-background-color: #1a1d21;");


        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        root.getChildren().addAll(header, scrollPane, footer);
    }

    public void setUser(User user) {
        this.currentUser = user;

        if (footer == null) {
            this.footer = new SidebarFooter(currentUser, socketClient, stage, onSettingsClicked);
            root.getChildren().removeIf(n -> n instanceof SidebarFooter);
            root.getChildren().add(footer);
        } else {
            footer.updateUser(currentUser);
        }
    }

    private VBox createHeader() {
        VBox header = new VBox();
        header.setSpacing(10);
        header.setPadding(new Insets(15, 15, 10, 15));
        header.setStyle("-fx-background-color: #1a1d21;");

        // Workspace name
        Label workspaceLabel = new Label("ST Chat");
        workspaceLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        // Search bar
        TextField searchField = new TextField();
        searchField.setPromptText("Search here...");
        searchField.setStyle(
                "-fx-background-color: #2f3136; " +
                        "-fx-text-fill: white; " +
                        "-fx-prompt-text-fill: #72767d; " +
                        "-fx-background-radius: 6px; " +
                        "-fx-border-radius: 6px; " +
                        "-fx-padding: 8px 12px;"
        );

        header.getChildren().addAll(workspaceLabel, searchField);
        return header;
    }

    private VBox createMainContent() {
        VBox content = new VBox();
        content.setSpacing(5);
        content.setPadding(new Insets(0, 10, 0, 10));
        content.setStyle("-fx-background-color: #1a1d21;");

        // Main navigation items

        // Favorites section
        VBox favoritesSection = createSection("FAVOURITES", favoritesContainer);

        // Direct Messages section
        VBox directMessagesSection = createSection("DIRECT MESSAGES", directMessagesContainer);

        // Channels section
        VBox channelsSection = createSection("CHANNELS", channelsContainer);

        content.getChildren().addAll(
                createSeparator(),
                favoritesSection,
                createSeparator(),
                directMessagesSection,
                createSeparator(),
                createSeparator(),
                channelsSection
        );

        return content;
    }

    private VBox createSection(String title, VBox container) {
        VBox section = new VBox();
        section.setSpacing(3);

        // Section header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(10);
        header.setPadding(new Insets(10, 5, 5, 5));

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: #72767d; -fx-font-size: 12px; -fx-font-weight: bold;");

        Button addButton = getButton(title);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(titleLabel, spacer, addButton);

        section.getChildren().addAll(header, container);
        return section;
    }

    @NotNull
    private Button getButton(String title) {
        Button addButton = new Button("+");
        addButton.setStyle("..."); // giữ nguyên style hover

        // Gắn hành động tương ứng theo tên section
        switch (title) {
            case "FAVOURITES" -> addButton.setOnAction(e -> {
                if (onAddFavoriteClicked != null) onAddFavoriteClicked.run();
            });
            case "DIRECT MESSAGES" -> addButton.setOnAction(e -> {
                if (onAddDirectMessageClicked != null) onAddDirectMessageClicked.run();
            });
            case "CHANNELS" -> addButton.setOnAction(e -> {
                if (onAddChannelClicked != null) onAddChannelClicked.run();
            });

        }
        return addButton;
    }


    private Region createSeparator() {
        Region separator = new Region();
        separator.setPrefHeight(1);
        separator.setStyle("-fx-background-color: #40444b;");
        VBox.setMargin(separator, new Insets(10, 0, 10, 0));
        return separator;
    }

    public List<String> getFavoriteUsernames() {
        return new ArrayList<>(favoriteUsernameSet);
    }



    private void setupDefaultItems() {

    }

    public void addFavorite(User targetUser) {
        if (favoriteUsernameSet.contains(targetUser.getUsername())) return;
        favoriteUsernameSet.add(targetUser.getUsername());
        DirectMessageItem item = new DirectMessageItem(targetUser.getUsername(), targetUser.getAvatarUrl(), targetUser.isActive(), null);
        item.setOnline(targetUser.isActive());
        item.setOnViewProfileClicked(username -> {
            System.out.println("👤 View profile of: " + username);
            VBox profileView = createOtherUserProfile(targetUser);
            currentMainContent = profileView;
            updateScrollContent();
        });



        item.setOnAction(() -> {
            if (onDirectMessageSelected != null) {
                onDirectMessageSelected.accept(targetUser.getUsername());
            }
        });

        item.setOnRemoveClicked(username -> {
            if (onRemoveFavoriteClicked != null) onRemoveFavoriteClicked.accept(username);
            favoritesContainer.getChildren().remove(item.getComponent());
            favoriteUsernameSet.remove(username);
        });

        favoritesContainer.getChildren().add(item.getComponent());
    }


    public void removeFavorite(String username) {
        for (var node : favoritesContainer.getChildren()) {
            if (node instanceof HBox hbox) {
                for (var child : hbox.getChildren()) {
                    if (child instanceof Label label && label.getText().equals(username)) {
                        favoritesContainer.getChildren().remove(hbox);
                        return;
                    }
                }
            }
        }
    }


    public void addDirectMessage(String name, String avatarUrl, boolean isOnline, String unreadCount) {
        DirectMessageItem item = new DirectMessageItem(name, avatarUrl, isOnline, unreadCount);
        item.setOnAction(() -> {
            if (onDirectMessageSelected != null) {
                onDirectMessageSelected.accept(name);

            }
        });
        directMessagesContainer.getChildren().add(item.getComponent());
    }

    public void addChannel(String name, String prefix, boolean isActive, boolean hasNotification) {
        addChannel(name, prefix, isActive, hasNotification, null);
    }

    public void addChannel(String name, String prefix, boolean isActive, boolean hasNotification, String unreadCount) {
        ChannelItem item = new ChannelItem(name, prefix, isActive, hasNotification, unreadCount);
        item.setOnAction(() -> {
            if (onChannelSelected != null) {
                onChannelSelected.accept(name);
            }
        });
        channelsContainer.getChildren().add(item.getComponent());
    }

    public void setOnChannelSelected(Consumer<String> handler) {
        this.onChannelSelected = handler;
    }

    public void setOnDirectMessageSelected(Consumer<String> handler) {
        this.onDirectMessageSelected = handler;
    }

    public void setOnRemoveFavoriteClicked(Consumer<String> handler){
        this.onRemoveFavoriteClicked = handler;
    }
    public void setOnSettingsClicked(Runnable handler) {
        this.onSettingsClicked = handler;
    }

    public VBox getComponent() {
        return root;
    }

    public void setOnAddFavoriteClicked(Runnable handler) {
        this.onAddFavoriteClicked = handler;
    }
    public void setOnAddDirectMessageClicked(Runnable handler) {
        this.onAddDirectMessageClicked = handler;
    }
    public void setOnAddChannelClicked(Runnable handler) {
        this.onAddChannelClicked = handler;
    }
}