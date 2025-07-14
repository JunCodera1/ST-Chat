package me.chatapp.stchat.view.components.organisms.Bar;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import me.chatapp.stchat.AppContext;
import me.chatapp.stchat.model.User;
import me.chatapp.stchat.api.SocketClient;
import me.chatapp.stchat.view.components.molecules.Item.ChannelItem;
import me.chatapp.stchat.view.components.molecules.Item.DirectMessageItem;
import me.chatapp.stchat.view.components.organisms.Footer.SidebarFooter;
import me.chatapp.stchat.view.factories.*;
import me.chatapp.stchat.view.init.SceneManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class NavigationSidebar {

    private final VBox root;
    private final VBox favoritesContainer;
    private final VBox directMessagesContainer;
    private final VBox channelsContainer;
    private final ScrollPane scrollPane;
    private User currentUser;
    private SidebarFooter footer;

    private Runnable onAddFavoriteClicked;
    private Runnable onAddDirectMessageClicked;
    private Runnable onAddChannelClicked;

    private VBox currentMainContent; // Lưu trữ nội dung chính hiện tại
    private VBox originalContent;


    // Event handlers
    private Consumer<String> onNavigationItemSelected;
    private Consumer<String> onChannelSelected;
    private Consumer<String> onDirectMessageSelected;
    private Runnable onSettingsClicked;
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
                newContent = originalContent;
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
        }
    }

    private void updateScrollContent() {
        scrollPane.setContent(currentMainContent);
    }

    // Tạo nội dung cho từng loại
    private VBox createProfileContent() {
        return ProfileViewFactory.create(currentUser, () -> switchContent("chats"));
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

    public void updateUser(User newUser) {
        if (footer != null) {
            footer.updateUser(newUser);
        }
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
        return favoritesContainer.getChildren().stream()
                .filter(node -> node instanceof HBox)
                .map(node -> ((Label) ((HBox) node).getChildren().get(1)).getText()) // giả sử label chứa username nằm ở index 1
                .toList();
    }


    private void setupDefaultItems() {

    }

    public void addFavorite(String name, String icon, boolean isOnline) {
        DirectMessageItem item = new DirectMessageItem(name, icon, isOnline, null);
        item.setOnAction(() -> {
            if (onDirectMessageSelected != null) {
                onDirectMessageSelected.accept(name);
            }
        });
        favoritesContainer.getChildren().add(item.getComponent());
    }

    public void addDirectMessage(String name, String icon, boolean isOnline, String unreadCount) {
        DirectMessageItem item = new DirectMessageItem(name, icon, isOnline, unreadCount);
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

    public void clearDirectMessages() {
        directMessagesContainer.getChildren().clear();
    }
    public void clearChannels() {
        channelsContainer.getChildren().clear();
    }

    // Event handlers setters
    public void setOnNavigationItemSelected(Consumer<String> handler) {
        this.onNavigationItemSelected = handler;
    }

    public void setOnChannelSelected(Consumer<String> handler) {
        this.onChannelSelected = handler;
    }

    public void setOnDirectMessageSelected(Consumer<String> handler) {
        this.onDirectMessageSelected = handler;
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

    // Giả sử bạn có một callback để truyền ra ngoài
    private Consumer<Node> onContentChange;

    public void setOnContentChange(Consumer<Node> handler) {
        this.onContentChange = handler;
    }

    public void showUserProfile(User user) {
        VBox profileView = new VBox();
        profileView.setSpacing(10);
        profileView.setPadding(new Insets(20));
        profileView.setStyle("-fx-background-color: white;");

        Label titleLabel = new Label("👤 User Profile");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label usernameLabel = new Label("Username: " + user.getUsername());
        usernameLabel.setStyle("-fx-font-size: 14px;");

        // Thêm button quay lại chat
        Button backButton = new Button("← Back to Chat");
        backButton.setOnAction(e -> {
            // Gọi callback để quay lại chat chính
            if (onContentChange != null) {
                // Tạo lại layout chat chính
                VBox chatLayout = createMainChatLayout();
                onContentChange.accept(chatLayout);
            }
        });

        profileView.getChildren().addAll(titleLabel, usernameLabel, backButton);

        if (onContentChange != null) {
            onContentChange.accept(profileView);
        }
    }

    private VBox createMainChatLayout() {
        // Recreate the main chat layout structure
        // You'll need to access chatHeader, chatPanel, messageInputPanel from ChatView
        // This might require passing references or using a different approach
        return new VBox();
    }

    public void showDirectMessages() {
        VBox messagesView = new VBox(new Label("📨 Direct Messages List"));
        if (onContentChange != null) onContentChange.accept(messagesView);
    }

    public void showChannels() {
        VBox channelView = new VBox(new Label("📢 Channels List"));
        if (onContentChange != null) onContentChange.accept(channelView);
    }

    public void showCallsPanel() {
        VBox callsView = new VBox(new Label("📞 Call History"));
        if (onContentChange != null) onContentChange.accept(callsView);
    }

    public void showBookmarks() {
        VBox bookmarkView = new VBox(new Label("🔖 Your Bookmarks"));
        if (onContentChange != null) onContentChange.accept(bookmarkView);
    }

    public void showSettings() {
        VBox settingsView = new VBox(new Label("⚙️ Settings"));
        if (onContentChange != null) onContentChange.accept(settingsView);
    }

}