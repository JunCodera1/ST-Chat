package me.chatapp.stchat.view.components.pages;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import me.chatapp.stchat.controller.MessageController;
import me.chatapp.stchat.model.Message;
import me.chatapp.stchat.model.User;
import me.chatapp.stchat.api.SocketClient;
import me.chatapp.stchat.view.components.organisms.Bar.NavigationSidebar;
import me.chatapp.stchat.view.components.organisms.Bar.StatusBar;
import me.chatapp.stchat.view.components.organisms.Header.ChatHeader;
import me.chatapp.stchat.view.components.organisms.Panel.ChatPanel;
import me.chatapp.stchat.view.components.organisms.Panel.ConnectionPanel;
import me.chatapp.stchat.view.components.organisms.Panel.MessageInputPanel;
import me.chatapp.stchat.view.config.ChatViewConfig;
import me.chatapp.stchat.view.handlers.*;
import me.chatapp.stchat.view.init.ChatInitializer;
import me.chatapp.stchat.view.state.ChatViewStateManager;

import java.io.IOException;
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

    // API
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


    private void setupChatAreaLayout() {
        chatAreaContainer.getChildren().add(chatHeader.getComponent());
        VBox.setVgrow(chatPanel.getComponent(), Priority.ALWAYS);
        chatAreaContainer.getChildren().add(chatPanel.getComponent());
        chatAreaContainer.getChildren().add(messageInputPanel.getComponent());
    }

    private void initializeManagers() {
        try {
            var result = ChatInitializer.initialize(
                    config,
                    currentUser,
                    currentStage,
                    scene,
                    connectionPanel,
                    chatPanel,
                    messageInputPanel,
                    statusBar,
                    chatHeader,
                    this::logout,
                    msg -> showInfo("Conversation Info", msg),
                    this::showError
            );

            this.socketClient = result.socketClient();
            this.messageController = result.messageController();
            this.stateManager = result.stateManager();
            this.eventHandler = result.eventHandler();
            this.navigationSidebar = result.navigationSidebar();

            NavigationSidebarHandlerBinder.bindHandlers(this);

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
        MessageSender.sendMessage(
                currentUser,
                content,
                currentConversationId,
                currentContactName,
                messageController,
                chatPanel,
                this::showError
        );
    }


    public int getOrCreateConversationId(String contactName) {
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
        ChatAutoConnector.connect(currentUser, stateManager, () -> updateConnectionStatus(true));
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
        ChatShutdownHandler.shutdown(currentUser, messageController, eventHandler, socketClient);
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
    public NavigationSidebar getNavigationSidebar() {
        return navigationSidebar;
    }
    public ChatPanel getChatPanel() {
        return chatPanel;
    }
    public ChatHeader getChatHeader() {
        return chatHeader;
    }
    public MessageController getMessageController() {
        return messageController;
    }
    public User getCurrentUser() {
        return currentUser;
    }
    public void setCurrentConversationId(int id) {
        this.currentConversationId = id;
    }
    public void setCurrentContactName(String name) {
        this.currentContactName = name;
    }

    public Scene getScene() { return scene; }
}