package me.chatapp.stchat.view.components.pages;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import me.chatapp.stchat.controller.ChatController;
import me.chatapp.stchat.model.ChatModel;
import me.chatapp.stchat.model.Message;
import me.chatapp.stchat.model.MessageType;
import me.chatapp.stchat.model.User;
import me.chatapp.stchat.view.components.organisms.Bar.NavigationSidebar;
import me.chatapp.stchat.view.components.organisms.Bar.StatusBar;
import me.chatapp.stchat.view.components.organisms.Header.ChatHeader;
import me.chatapp.stchat.view.components.organisms.Header.HeaderComponent;
import me.chatapp.stchat.view.components.organisms.Panel.ChatPanel;
import me.chatapp.stchat.view.components.organisms.Panel.ConnectionPanel;
import me.chatapp.stchat.view.components.organisms.Panel.MessageInputPanel;
import me.chatapp.stchat.view.config.ChatViewConfig;
import me.chatapp.stchat.view.handlers.*;
import me.chatapp.stchat.view.layout.ChatViewLayoutManager;
import me.chatapp.stchat.view.state.ChatViewStateManager;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.logging.Logger;

public class ChatView extends Application {

    private static final Logger LOGGER = Logger.getLogger(ChatView.class.getName());

    private User currentUser;

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
    private ChatViewLayoutManager layoutManager;
    private ChatViewStateManager stateManager;
    private ChatViewEventHandler eventHandler;

    // Legacy handlers
    private ConnectionHandler connectionHandler;
    private MessageHandler messageHandler;

    // Stage reference
    private Stage currentStage;

    public ChatView() {
        this(new ChatViewConfig());
    }

    public ChatView(ChatViewConfig config) {
        this.config = config;
        this.root = new BorderPane();
        this.scene = new Scene(root, config.getWidth(), config.getHeight());
        // User
        initializeUI();
    }

    public ChatView(ChatViewConfig config, User user) {
        this(config);
        this.currentUser = user;
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
        initializeLayout();
        initializeManagers();
        initializeLegacyHandlers();
        setupTestData();
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
        chatPanel = new ChatPanel();
        messageInputPanel = new MessageInputPanel();
        statusBar = new StatusBar();
        chatHeader = new ChatHeader();
        navigationSidebar = new NavigationSidebar();
    }

    private void initializeLayout() {
        // Layout containers
        HBox mainContainer = new HBox();
        mainContainer.setSpacing(0);

        VBox navSidebarContainer = navigationSidebar.getComponent();


        chatAreaContainer = new VBox();
        chatAreaContainer.setStyle("-fx-background-color: white;");
        HBox.setHgrow(chatAreaContainer, Priority.ALWAYS);

        setupChatAreaLayout();

        mainContainer.getChildren().addAll(navSidebarContainer, chatAreaContainer);
        root.setCenter(mainContainer);
        root.setBottom(statusBar.getComponent());
    }

    private void setupNavigationSidebarHandlers() {
        navigationSidebar.setOnNavigationItemSelected(item -> {
            switch (item) {
                case "chats":
                    // Handle chats navigation
                    showInfo("Navigation", "Switched to Chats view");
                    break;
                case "threads":
                    // Handle threads navigation
                    showInfo("Navigation", "Switched to Threads view");
                    break;
                case "calls":
                    // Handle calls navigation
                    showInfo("Navigation", "Switched to Calls view");
                    break;
                case "bookmarks":
                    // Handle bookmarks navigation
                    showInfo("Navigation", "Switched to Bookmarks view");
                    break;
            }
        });

        navigationSidebar.setOnChannelSelected(channelName -> {
            chatHeader.setActiveConversation(channelName);
            loadConversationMessages(channelName);
        });

        navigationSidebar.setOnDirectMessageSelected(userName -> {
            chatHeader.setActiveConversation(userName);
            loadConversationMessages(userName);
        });

        navigationSidebar.setOnSettingsClicked(() -> {
            showInfo("Settings", "Settings panel will be implemented soon!");
        });
    }

    private void setupChatAreaLayout() {
        chatAreaContainer.getChildren().add(chatHeader.getComponent());
        VBox.setVgrow(chatPanel.getComponent(), Priority.ALWAYS);
        chatAreaContainer.getChildren().add(chatPanel.getComponent());
        chatAreaContainer.getChildren().add(messageInputPanel.getComponent());
    }

    private void initializeManagers() {
        stateManager = new ChatViewStateManager(
                connectionPanel, chatPanel, messageInputPanel, statusBar, scene
        );
        eventHandler = new ChatViewEventHandler(
                config.getPort(), connectionPanel, chatPanel,
                messageInputPanel, stateManager
        );

        // Setup navigation sidebar handlers
        setupNavigationSidebarHandlers();


        chatHeader.setOnCallAction(() -> showInfo("Voice Call", "Voice call feature coming soon!"));
        chatHeader.setOnVideoCallAction(() -> showInfo("Video Call", "Video call feature coming soon!"));
        chatHeader.setOnInfoAction(() -> showInfo("Conversation Info", "Conversation details coming soon!"));

        messageInputPanel.getMessageField().textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.isEmpty() && (oldText == null || oldText.isEmpty())) {
                chatPanel.showTypingIndicator("You");
            } else if (newText.isEmpty() && oldText != null && !oldText.isEmpty()) {
                chatPanel.hideTypingIndicator();
            }
        });
    }

    private void initializeLegacyHandlers() {
        connectionHandler = new ConnectionHandler(this);
        messageHandler = new MessageHandler(this);
        UIStateHandler uiStateHandler = new UIStateHandler(this);
    }

    private void setupTestData() {
        if (currentUser == null) {
            stateManager.addMessage("Welcome to ST Chat! Select a conversation to start chatting.");
            stateManager.addMessage(new Message("System", "Please connect to start chatting", MessageType.SYSTEM, LocalDateTime.now()));
        } else {
            stateManager.addMessage(new Message("System",
                    "Welcome back, " + currentUser.getUsername() + "! 🎉",
                    MessageType.SYSTEM, LocalDateTime.now()));
        }
    }

    private void loadConversationMessages(String conversationName) {
        chatPanel.clearMessages();
        chatPanel.showTypingIndicator(conversationName);
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                Platform.runLater(() -> {
                    chatPanel.hideTypingIndicator();
                    if ("Alice Johnson".equals(conversationName)) {
                        stateManager.addMessage(new Message("Alice", "Hey! How are you doing? 😊", MessageType.USER, LocalDateTime.now().minusMinutes(5)));
                        stateManager.addMessage(new Message("You", "Hi Alice! I'm doing great, thanks for asking!", MessageType.BOT, LocalDateTime.now().minusMinutes(3)));
                        stateManager.addMessage(new Message("Alice", "That's awesome! Want to grab coffee later? ☕", MessageType.USER, LocalDateTime.now().minusMinutes(2)));
                        stateManager.addMessage(new Message("You", "Sure! How about 3 PM at the usual place?", MessageType.BOT, LocalDateTime.now().minusMinutes(1)));
                        stateManager.addMessage(new Message("Alice", "Perfect! See you there 👍", MessageType.USER, LocalDateTime.now()));
                    } else if ("Bob Smith".equals(conversationName)) {
                        stateManager.addMessage(new Message("Bob", "Thanks for the help earlier with the project", MessageType.USER, LocalDateTime.now().minusHours(1)));
                        stateManager.addMessage(new Message("You", "No problem! Happy to help anytime.", MessageType.BOT, LocalDateTime.now().minusHours(1)));
                        stateManager.addMessage(new Message("Bob", "The client loved the final presentation 🎉", MessageType.USER, LocalDateTime.now().minusMinutes(30)));
                        stateManager.addMessage(new Message("You", "That's fantastic news! Great teamwork 💪", MessageType.BOT, LocalDateTime.now().minusMinutes(25)));
                    } else if ("Team Chat".equals(conversationName)) {
                        stateManager.addMessage(new Message("John", "Meeting at 3 PM in conference room A", MessageType.USER, LocalDateTime.now().minusHours(3)));
                        stateManager.addMessage(new Message("Sarah", "Got it, I'll be there", MessageType.USER, LocalDateTime.now().minusHours(2)));
                        stateManager.addMessage(new Message("You", "Same here, see you all at 3", MessageType.BOT, LocalDateTime.now().minusHours(2)));
                        stateManager.addMessage(new Message("Mike", "Can we push it to 3:30? Running a bit late", MessageType.USER, LocalDateTime.now().minusMinutes(30)));
                        stateManager.addMessage(new Message("John", "Sure, 3:30 works for everyone", MessageType.USER, LocalDateTime.now().minusMinutes(25)));
                    } else if ("Mom".equals(conversationName)) {
                        stateManager.addMessage(new Message("Mom", "Don't forget to call! 📞", MessageType.USER, LocalDateTime.now().minusDays(1)));
                        stateManager.addMessage(new Message("You", "I'll call you tonight, promise! ❤️", MessageType.BOT, LocalDateTime.now().minusHours(20)));
                        stateManager.addMessage(new Message("Mom", "Looking forward to it sweetie", MessageType.USER, LocalDateTime.now().minusHours(19)));
                    } else {
                        stateManager.addMessage(new Message("System", "Conversation with " + conversationName + " loaded", MessageType.SYSTEM, LocalDateTime.now()));
                    }
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private void autoConnectToChat() {
        if (currentUser != null) {
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    Platform.runLater(() -> {
                        stateManager.addMessage(new Message("System",
                                "🔄 Connecting to chat server...",
                                MessageType.SYSTEM, LocalDateTime.now()));
                        updateConnectionStatus(true);
                        stateManager.addMessage(new Message("System",
                                "✅ Connected successfully! You can now start chatting.",
                                MessageType.SYSTEM, LocalDateTime.now()));
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
        if (eventHandler != null && eventHandler.getDisconnectAction() != null) {
            eventHandler.getDisconnectAction().execute();
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
                    MessageType.SYSTEM, LocalDateTime.now()));
            currentUser = null;
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
            Login login = new Login(() -> {
                loginStage.close();
                Stage signUpStage = new Stage();
                SignUp signUp = new SignUp(() -> {
                    signUpStage.close();
                    showLoginStage();
                });
                signUp.show();
            }, user -> {
                loginStage.close();
                ChatModel model = new ChatModel();
                ChatViewConfig config = new ChatViewConfig();
                ChatView view = new ChatView(config, user);
                ChatController controller = new ChatController(model, view);
                Stage newStage = new Stage();
                view.start(newStage);
                controller.initialize();
            });
            login.show();
        });
    }

    public void setOnConnectAction(ChatEventActions.ConnectAction action) {
        eventHandler.setOnConnectAction(action);
    }

    public void setOnDisconnectAction(ChatEventActions.DisconnectAction action) {
        eventHandler.setOnDisconnectAction(action);
    }

    public void setOnSendMessageAction(ChatEventActions.SendMessageAction action) {
        eventHandler.setOnSendMessageAction(action);
    }

    public HeaderComponent getHeaderComponent() { return headerComponent; }
    public ConnectionPanel getConnectionPanel() { return connectionPanel; }
    public ChatPanel getChatPanel() { return chatPanel; }
    public MessageInputPanel getMessageInputPanel() { return messageInputPanel; }
    public StatusBar getStatusBar() { return statusBar; }
    public ChatHeader getChatHeader() { return chatHeader; }
    public Scene getScene() { return scene; }

    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public ConnectionHandler getConnectionHandler() {
        return connectionHandler;
    }

    public void setConnectionHandler(ConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    public TextField getMessageField() { return messageInputPanel.getMessageField(); }
    public Button getSendButton() { return messageInputPanel.getSendButton(); }
    public Label getStatusLabel() { return statusBar.getStatusLabel(); }
    public TextField getUserNameField() { return connectionPanel.getUsernameField(); }
    public NavigationSidebar getNavigationSidebar() {
        return navigationSidebar;
    }
    public Parent getPage() {
        return root;
    }
}