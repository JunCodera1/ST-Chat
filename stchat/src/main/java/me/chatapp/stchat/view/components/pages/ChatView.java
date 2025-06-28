package me.chatapp.stchat.view.components.pages;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import me.chatapp.stchat.controller.ChatController;
import me.chatapp.stchat.model.ChatModel;
import me.chatapp.stchat.model.Message;
import me.chatapp.stchat.model.MessageType;
import me.chatapp.stchat.model.User;
import me.chatapp.stchat.network.SocketClient;
import me.chatapp.stchat.view.components.organisms.Bar.NavigationSidebar;
import me.chatapp.stchat.view.components.organisms.Bar.StatusBar;
import me.chatapp.stchat.view.components.organisms.Header.ChatHeader;
import me.chatapp.stchat.view.components.organisms.Header.HeaderComponent;
import me.chatapp.stchat.view.components.organisms.Panel.ChatPanel;
import me.chatapp.stchat.view.components.organisms.Panel.ConnectionPanel;
import me.chatapp.stchat.view.components.organisms.Panel.MessageInputPanel;
import me.chatapp.stchat.view.config.ChatViewConfig;
import me.chatapp.stchat.view.handlers.*;
import me.chatapp.stchat.view.state.ChatViewStateManager;

import java.io.IOException;
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
    private ChatViewStateManager stateManager;
    private ChatViewEventHandler eventHandler;



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

        navigationSidebar.setOnChannelSelected(channelName -> chatHeader.setActiveConversation(channelName));

        navigationSidebar.setOnDirectMessageSelected(userName -> chatHeader.setActiveConversation(userName));

        navigationSidebar.setOnSettingsClicked(() -> showInfo("Settings", "Settings panel will be implemented soon!"));
    }

    private void setupChatAreaLayout() {
        chatAreaContainer.getChildren().add(chatHeader.getComponent());
        VBox.setVgrow(chatPanel.getComponent(), Priority.ALWAYS);
        chatAreaContainer.getChildren().add(chatPanel.getComponent());
        chatAreaContainer.getChildren().add(messageInputPanel.getComponent());
    }

    private void initializeManagers() {
        try{
            SocketClient socketClient = new SocketClient("localhost", config.getPort());

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
                    socketClient,
                    currentStage,
                    this::logout
            );

            setupNavigationSidebarHandlers();

            // Tiếp tục set các sự kiện
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
        }catch (IOException e){
            e.printStackTrace();
        }
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
                try {
                    loginStage.close();
                    ChatModel model = new ChatModel();
                    ChatViewConfig config = new ChatViewConfig();
                    ChatView view = new ChatView(config, user, loginStage);
                    String host = "localhost";
                    int portAsInt = 8080;
                    SocketClient client = new SocketClient(host, portAsInt);
                    ChatController controller = new ChatController(model, view, client);
                    Stage newStage = new Stage();
                    view.start(newStage);
                    controller.initialize();
                }catch (IOException e){
                    e.printStackTrace();
                }

            });
            login.show();
        });
    }

    public ChatViewEventHandler getEventHandler(){
        return eventHandler;
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
    public MessageInputPanel getMessageInputPanel() { return messageInputPanel; }
    public StatusBar getStatusBar() { return statusBar; }
    public Scene getScene() { return scene; }

    public TextField getMessageField() { return messageInputPanel.getMessageField(); }
    public Button getSendButton() { return messageInputPanel.getSendButton(); }
    public Label getStatusLabel() { return statusBar.getStatusLabel(); }
    public TextField getUserNameField() { return connectionPanel.getUsernameField(); }
}