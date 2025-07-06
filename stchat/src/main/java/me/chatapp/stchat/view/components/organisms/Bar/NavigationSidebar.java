package me.chatapp.stchat.view.components.organisms.Bar;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import me.chatapp.stchat.AppContext;
import me.chatapp.stchat.model.User;
import me.chatapp.stchat.api.SocketClient;
import me.chatapp.stchat.view.components.molecules.Item.NavigationItem;
import me.chatapp.stchat.view.components.molecules.Item.ChannelItem;
import me.chatapp.stchat.view.components.molecules.Item.DirectMessageItem;
import me.chatapp.stchat.view.components.organisms.Footer.SidebarFooter;
import me.chatapp.stchat.view.core.SceneManager;
import org.jetbrains.annotations.NotNull;

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

        initializeComponent();
        setupDefaultItems();
    }


    private void initializeComponent() {
        root.setPrefWidth(280);
        root.setMinWidth(260);
        root.setMaxWidth(320);
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
        VBox mainNav = createMainNavigation();

        // Favorites section
        VBox favoritesSection = createSection("FAVOURITES", favoritesContainer);

        // Direct Messages section
        VBox directMessagesSection = createSection("DIRECT MESSAGES", directMessagesContainer);

        // Channels section
        VBox channelsSection = createSection("CHANNELS", channelsContainer);

        content.getChildren().addAll(
                mainNav,
                createSeparator(),
                favoritesSection,
                createSeparator(),
                directMessagesSection,
                createSeparator(),
                channelsSection
        );

        return content;
    }

    private VBox createMainNavigation() {
        VBox mainNav = new VBox();
        mainNav.setSpacing(2);

        NavigationItem chatsItem = new NavigationItem("💬", "Chats", true);
        NavigationItem threadsItem = new NavigationItem("🧵", "Threads", false);
        NavigationItem callsItem = new NavigationItem("📞", "Calls", false);
        NavigationItem bookmarksItem = new NavigationItem("🔖", "Bookmarks", false);
        NavigationItem settingsItem = new NavigationItem("⚙️", "Settings", false);

        // Set click handlers
        chatsItem.setOnAction(() -> {
            if (onNavigationItemSelected != null) {
                onNavigationItemSelected.accept("chats");
            }
        });

        threadsItem.setOnAction(() -> {
            if (onNavigationItemSelected != null) {
                onNavigationItemSelected.accept("threads");
            }
        });

        callsItem.setOnAction(() -> {
            if (onNavigationItemSelected != null) {
                onNavigationItemSelected.accept("calls");
            }
        });

        bookmarksItem.setOnAction(() -> {
            if (onNavigationItemSelected != null) {
                onNavigationItemSelected.accept("bookmarks");
            }
        });

        settingsItem.setOnAction(() -> {
            if (onSettingsClicked != null) {
                onSettingsClicked.run();
            }
        });

        mainNav.getChildren().addAll(chatsItem.getComponent(), threadsItem.getComponent(),
                callsItem.getComponent(), bookmarksItem.getComponent(),
                settingsItem.getComponent());

        return mainNav;
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

}