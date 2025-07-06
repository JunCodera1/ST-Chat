package me.chatapp.stchat.view.components.pages;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import me.chatapp.stchat.AppContext;
import me.chatapp.stchat.api.ConversationApiClient;
import me.chatapp.stchat.api.UserApiClient;
import me.chatapp.stchat.controller.ChatController;
import me.chatapp.stchat.controller.MessageController;
import me.chatapp.stchat.model.Message;
import me.chatapp.stchat.model.User;
import me.chatapp.stchat.api.SocketClient;
import me.chatapp.stchat.view.components.organisms.Bar.NavigationSidebar;
import me.chatapp.stchat.view.components.organisms.Bar.StatusBar;
import me.chatapp.stchat.view.components.organisms.Window.CallWindow;
import me.chatapp.stchat.view.components.organisms.Header.ChatHeader;
import me.chatapp.stchat.view.components.organisms.Header.HeaderComponent;
import me.chatapp.stchat.view.components.organisms.Panel.ChatPanel;
import me.chatapp.stchat.view.components.organisms.Panel.ConnectionPanel;
import me.chatapp.stchat.view.components.organisms.Panel.MessageInputPanel;
import me.chatapp.stchat.view.components.organisms.Window.VideoCallWindow;
import me.chatapp.stchat.view.config.ChatViewConfig;
import me.chatapp.stchat.view.handlers.*;
import me.chatapp.stchat.view.state.ChatViewStateManager;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class ChatView extends Application {

    private static final Logger LOGGER = Logger.getLogger(ChatView.class.getName());

    private User currentUser;
    private int currentConversationId = -1;
    private String currentContactName = null;

    // Configuration
    private final ChatViewConfig config;

    // Core JavaFX components
    private final BorderPane root;
    private final Scene scene;

    private NavigationSidebar navigationSidebar;
    private HeaderComponent headerComponent;
    private ConnectionPanel connectionPanel;
    private ChatPanel chatPanel;
    private MessageInputPanel messageInputPanel;
    private StatusBar statusBar;

    private ChatHeader chatHeader;
    private VBox chatAreaContainer;

    // Managers
    private ChatViewStateManager stateManager;
    private ChatViewEventHandler eventHandler;

    // Controllers
    private MessageController messageController;
    private ChatController chatController;

    // API
    private ConversationApiClient conversationApiClient;
    private SocketClient socketClient;

    // Stage reference
    private Stage currentStage;

    public ChatView() {
        this(new ChatViewConfig());
    }

    public ChatView(ChatViewConfig config) {
        this.config = config;
        this.root = new BorderPane();
        this.scene = new Scene(root, config.getWidth(), config.getHeight());
        initializeUI();
    }

    public ChatView(ChatViewConfig config, User user, Stage stage) {
        this.config = config;
        this.root = new BorderPane();
        this.scene = new Scene(root, config.getWidth(), config.getHeight());
        this.currentUser = user;
        this.currentStage = stage;

        initializeUI();

        if (user != null) {
            Platform.runLater(() -> {
                navigationSidebar.setUser(user);
                if (currentStage != null) {
                    currentStage.setTitle(config.getTitle() + " - " + user.getUsername());
                }
                autoConnectToChat();
            });
        }
    }

    private void initializeUI() {
        loadStylesheets();
        initializeComponents();
        initializeManagers();
        initializeLayout();
        setupSystemMessage();
    }

    private void loadStylesheets() {
        for (String cssFile : ChatViewConfig.CSS_FILES) {
            try {
                String css = Objects.requireNonNull(getClass().getResource(cssFile)).toExternalForm();
                scene.getStylesheets().add(css);
            } catch (Exception e) {
                System.err.println("Could not load CSS file: " + cssFile);
            }
        }
    }

    private void initializeComponents() {
        headerComponent = new HeaderComponent();
        connectionPanel = new ConnectionPanel();

        // Khởi tạo ChatPanel và MessageInputPanel với callback
        chatPanel = new ChatPanel(currentUser);
        messageInputPanel = new MessageInputPanel(this::handleSendMessage);

        statusBar = new StatusBar();
        chatHeader = new ChatHeader();
    }

    private void initializeLayout() {
        // Layout containers
        HBox mainContainer = new HBox();
        mainContainer.setSpacing(0);

        VBox navSidebarContainer = navigationSidebar.getComponent();

        chatAreaContainer = new VBox();
        chatAreaContainer.setStyle("-fx-background-color: white;");
        HBox.setHgrow(chatAreaContainer, Priority.ALWAYS);
        VBox.setVgrow(chatAreaContainer, Priority.ALWAYS);

        setupChatAreaLayout();

        mainContainer.getChildren().addAll(navSidebarContainer, chatAreaContainer);
        root.setCenter(mainContainer);
        root.setBottom(statusBar.getComponent());
    }

    private void setupNavigationSidebarHandlers() {
        navigationSidebar.setOnAddFavoriteClicked(() -> {
            UserApiClient userApiClient = new UserApiClient();

            userApiClient.getAllUsers().thenAccept(users -> {
                List<String> usernames = users.stream()
                        .map(User::getUsername)
                        .filter(name -> !name.equals(currentUser.getUsername()))
                        .toList();

                Platform.runLater(() -> {
                    ChoiceDialog<String> dialog = new ChoiceDialog<>(usernames.isEmpty() ? null : usernames.get(0), usernames);
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

        // Gắn sự kiện điều hướng
        navigationSidebar.setOnNavigationItemSelected(item -> {
            switch (item) {
                case "chats" -> showInfo("Navigation", "Switched to Chats view");
                case "threads" -> showInfo("Navigation", "Switched to Threads view");
                case "calls" -> showInfo("Navigation", "Switched to Calls view");
                case "bookmarks" -> showInfo("Navigation", "Switched to Bookmarks view");
            }
        });

        // Xử lý khi chọn channel hoặc direct message
        navigationSidebar.setOnChannelSelected(channelName -> {
            currentContactName = channelName;
            currentConversationId = getOrCreateConversationId(channelName);
            chatHeader.setActiveConversation(channelName);
            chatPanel.setCurrentContact(channelName, "channel");
            chatPanel.clearMessages();

            if (messageController != null) {
                messageController.loadMessagesForConversation(currentConversationId, chatPanel);
            }
        });

        navigationSidebar.setOnDirectMessageSelected(userName -> {
            currentContactName = userName;
            currentConversationId = getOrCreateConversationId(userName);
            chatHeader.setActiveConversation(userName);
            chatPanel.setCurrentContact(userName, "user");
            chatPanel.clearMessages();

            if (messageController != null) {
                messageController.loadMessagesForConversation(currentConversationId, chatPanel);
            }
        });

        navigationSidebar.setOnSettingsClicked(() ->
                showInfo("Settings", "Settings panel will be implemented soon!"));
    }


    private void setupChatAreaLayout() {
        chatAreaContainer.getChildren().add(chatHeader.getComponent());
        VBox.setVgrow(chatPanel.getComponent(), Priority.ALWAYS);
        chatAreaContainer.getChildren().add(chatPanel.getComponent());
        chatAreaContainer.getChildren().add(messageInputPanel.getComponent());
    }

    private void initializeManagers() {
        try {
            // Khởi tạo SocketClient
            socketClient = new SocketClient("localhost", config.getPort());
            AppContext.getInstance().setSocketClient(socketClient);

            // Khởi tạo MessageController
            messageController = new MessageController();

            stateManager = new ChatViewStateManager(
                    connectionPanel, chatPanel, messageInputPanel, statusBar, scene
            );

            eventHandler = new ChatViewEventHandler(
                    config.getPort(), connectionPanel, chatPanel,
                    messageInputPanel, stateManager
            );

            eventHandler.setSocketClient(socketClient);

            navigationSidebar = new NavigationSidebar(
                    currentUser,
                    currentStage,
                    this::logout
            );

            setupNavigationSidebarHandlers();

            // Setup chat header actions
            chatHeader.setOnCallAction(() -> {
                String name = currentUser != null ? currentUser.getUsername() : "Unknown";
                new CallWindow(name).show();
            });

            chatHeader.setOnVideoCallAction(() -> {
                String userName = currentUser != null ? currentUser.getUsername() : "You";
                String contactName = currentContactName != null ? currentContactName : "Unknown";
                VideoCallWindow videoCallWindow = new VideoCallWindow(userName, contactName);
                videoCallWindow.show();
            });

            chatHeader.setOnInfoAction(() -> showInfo("Conversation Info", "Conversation details coming soon!"));

            // Setup typing indicator
            messageInputPanel.getMessageField().textProperty().addListener((obs, oldText, newText) -> {
                if (!newText.isEmpty() && (oldText == null || oldText.isEmpty())) {
                    if (currentUser != null) {
                        chatPanel.showTypingIndicator(currentUser.getUsername());
                    }
                } else if (newText.isEmpty() && oldText != null && !oldText.isEmpty()) {
                    chatPanel.hideTypingIndicator();
                }
            });

            // Setup socket message listener
            setupSocketMessageListener();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to initialize chat components: " + e.getMessage());
        }
    }

    private void setupSocketMessageListener() {
        if (socketClient != null) {
            socketClient.setMessageListener(message -> {
                Platform.runLater(() -> {
                    if (message.getConversationId() == currentConversationId) {
                        chatPanel.addMessage(message);
                    }
                });
            });
        }
    }

    private void handleSendMessage(String content) {
        if (currentUser == null) {
            showError("Please login first");
            return;
        }

        if (currentConversationId == -1 || currentContactName == null) {
            showError("Please select a conversation first");
            return;
        }

        if (content.trim().isEmpty()) {
            return;
        }

        System.out.println("Sending message: " + content);
        System.out.println("To conversation: " + currentConversationId);
        System.out.println("Contact: " + currentContactName);

        // Gửi tin nhắn qua MessageController
        if (messageController != null) {
            messageController.sendMessage(currentUser, content, currentConversationId, chatPanel);
        }
    }

    private int getOrCreateConversationId(String contactName) {
        // For now, return a simple hash-based ID
        return Math.abs(contactName.hashCode()) % 1000 + 1;
    }

    private void setupSystemMessage() {
        if (currentUser == null) {
            stateManager.addMessage("Welcome to ST Chat! Please login to start chatting.");
        } else {
            stateManager.addMessage(new Message("System",
                    "Welcome back, " + currentUser.getUsername() + "! 🎉",
                    Message.MessageType.SYSTEM));
        }
    }

    private void autoConnectToChat() {
        if (currentUser != null) {
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    Platform.runLater(() -> {
                        stateManager.addMessage(new Message("System",
                                "🔄 Connecting to chat server...",
                                Message.MessageType.SYSTEM));
                        updateConnectionStatus(true);
                        stateManager.addMessage(new Message("System",
                                "✅ Connected successfully! You can now start chatting.",
                                Message.MessageType.SYSTEM));
                        LOGGER.info("Auto-connected user: " + currentUser.getUsername());
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOGGER.warning("Auto-connect interrupted: " + e.getMessage());
                }
            }).start();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        this.currentStage = primaryStage;
        setupStage(primaryStage);
        primaryStage.show();
        if (currentUser != null) {
            primaryStage.setTitle(config.getTitle() + " - " + currentUser.getUsername());
        }
    }

    private void setupStage(Stage primaryStage) {
        primaryStage.setTitle(config.getTitle());
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(650);
        primaryStage.setOnCloseRequest(e -> {
            handleApplicationClose();
            Platform.exit();
        });
    }

    private void handleApplicationClose() {
        if (currentUser != null) {
            LOGGER.info("User " + currentUser.getUsername() + " is closing the application");
        }

        // Cleanup controllers
        if (messageController != null) {
            messageController.close();
        }

        if (eventHandler != null && eventHandler.getDisconnectAction() != null) {
            eventHandler.getDisconnectAction().execute();
        }

        // Close socket connection
        if (socketClient != null) {
            try {
                socketClient.close();
            } catch (Exception e) {
                LOGGER.warning("Error closing socket: " + e.getMessage());
            }
        }
    }

    public void updateConnectionStatus(boolean connected) {
        stateManager.updateConnectionStatus(connected);
        statusBar.setStatus(connected ? "🟢 Connected" : "🔴 Disconnected", connected);
    }

    public void showError(String error) {
        stateManager.showError(error);
    }

    public void showInfo(String title, String message) {
        stateManager.showInfo(title, message);
    }

    public void logout() {
        if (currentUser != null) {
            LOGGER.info("User " + currentUser.getUsername() + " logged out");
            stateManager.addMessage(new Message("System",
                    "👋 Logged out successfully",
                    Message.MessageType.SYSTEM));
            currentUser = null;
            currentConversationId = -1;
            currentContactName = null;

            if (connectionPanel != null) {
                connectionPanel.getUsernameField().setText("");
                connectionPanel.getUsernameField().setEditable(true);
            }
            updateConnectionStatus(false);

            // Close current stage and show login
            if (currentStage != null) {
                currentStage.close();
            }
            showLoginStage();
        }
    }

    private void showLoginStage() {
        Platform.runLater(() -> {
            Stage loginStage = new Stage();
            Login login = createLoginStage(loginStage);
            login.show();
        });
    }

    private Login createLoginStage(Stage loginStage) {
        return new Login(
                () -> openSignUpStage(loginStage),
                user -> handleLoginSuccess(user, loginStage)
        );
    }

    private void openSignUpStage(Stage previousStage) {
        previousStage.close();
        Stage signUpStage = new Stage();

        SignUp signUp = new SignUp(() -> {
            signUpStage.close();
            showLoginStage();
        });

        signUp.show();
    }

    private void handleLoginSuccess(User user, Stage loginStage) {
        try {
            loginStage.close();
            ChatViewConfig config = new ChatViewConfig();
            ChatView view = new ChatView(config, user, loginStage);

            Stage chatStage = new Stage();
            view.start(chatStage);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to open chat: " + e.getMessage());
        }
    }

    // Getters
    public HeaderComponent getHeaderComponent() { return headerComponent; }
    public ConnectionPanel getConnectionPanel() { return connectionPanel; }
    public MessageInputPanel getMessageInputPanel() { return messageInputPanel; }
    public StatusBar getStatusBar() { return statusBar; }
    public Scene getScene() { return scene; }

}