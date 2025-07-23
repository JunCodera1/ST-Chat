package me.chatapp.stchat.view.components.pages;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import me.chatapp.stchat.controller.ConversationController;
import me.chatapp.stchat.controller.MessageController;
import me.chatapp.stchat.model.AttachmentMessage;
import me.chatapp.stchat.model.Message;
import me.chatapp.stchat.model.User;
import me.chatapp.stchat.api.SocketClient;
import me.chatapp.stchat.view.components.organisms.Bar.IconSidebar;
import me.chatapp.stchat.view.components.organisms.Bar.NavigationSidebar;
import me.chatapp.stchat.view.components.organisms.Bar.StatusBar;
import me.chatapp.stchat.view.components.organisms.Header.ChatHeader;
import me.chatapp.stchat.view.components.organisms.Panel.ChatPanel;
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

    private ConversationController conversationController;
    private User currentUser;
    private int currentConversationId = -1;
    private String currentContactName = null;

    private final ChatViewConfig config;

    private final BorderPane root;
    private final Scene scene;

    private HostServices hostServices;

    private NavigationSidebar navigationSidebar;
    private IconSidebar iconSidebar;
    private ChatPanel chatPanel;
    private MessageInputPanel messageInputPanel;
    private StatusBar statusBar;

    private ChatHeader chatHeader;
    private VBox chatAreaContainer;

    private ChatViewStateManager stateManager;
    private ChatViewEventHandler eventHandler;

    private MessageController messageController;

    private SocketClient socketClient;

    private Stage currentStage;

    public ChatView(ChatViewConfig config) {
        this.config = config;
        this.root = new BorderPane();
        this.scene = new Scene(root, config.getWidth(), config.getHeight());
        initializeUI();
    }

    public ChatView(ChatViewConfig config, User user, Stage stage) {
        this.config = config;
        this.hostServices = getHostServices();
        this.root = new BorderPane();
        this.scene = new Scene(root, config.getWidth(), config.getHeight());
        this.currentUser = user;
        this.currentStage = stage;

        initializeUI();

        Platform.runLater(() -> {
            navigationSidebar.setUser(user);
            if (currentStage != null) {
                currentStage.setTitle(config.getTitle() + " - " + user.getUsername());
            }
            NavigationSidebarHandlerBinder.fetchFavoritesForUser(user, navigationSidebar, this);
        });
    }

    private void initializeUI() {
        loadStylesheets();
        initializeComponents();
        initializeManagers();
        initializeLayout();
        setupSidebarIconActions();
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
        iconSidebar = new IconSidebar();
        messageInputPanel = new MessageInputPanel(this::handleSendAttachment,this::handleSendMessage);
        chatPanel = new ChatPanel(currentUser, messageInputPanel, hostServices);
        statusBar = new StatusBar();
        chatHeader = new ChatHeader();
    }




    private void restoreMainChatLayout(User user) {
        chatAreaContainer.getChildren().clear();

        chatAreaContainer.getChildren().add(chatHeader.getComponent());

        VBox.setVgrow(chatPanel.getComponent(), Priority.ALWAYS);
        chatAreaContainer.getChildren().add(chatPanel.getComponent());

        chatAreaContainer.getChildren().add(messageInputPanel.getComponent());
    }


    private void setupSidebarIconActions() {
        iconSidebar.setIconAction("Chats", () -> {
            navigationSidebar.switchContent("chats");
            restoreMainChatLayout(currentUser);
            navigationSidebar.refreshFavorites(this);
        });

        iconSidebar.setIconAction("Profile", () -> {
            navigationSidebar.switchContent("profile");
        });

        iconSidebar.setIconAction("Messages", () -> {
            navigationSidebar.switchContent("messages");
        });

        iconSidebar.setIconAction("Groups", () -> {
            navigationSidebar.switchContent("groups");
        });

        iconSidebar.setIconAction("Calls", () -> {
            navigationSidebar.switchContent("calls");
        });

        iconSidebar.setIconAction("Bookmarks", () -> {
            navigationSidebar.switchContent("bookmarks");
        });

        iconSidebar.setIconAction("Settings", () -> {
            navigationSidebar.switchContent("settings");
        });
    }

    private void initializeLayout() {
        // Layout containers
        HBox mainContainer = new HBox();
        mainContainer.setSpacing(0);

        VBox navSidebarContainer = navigationSidebar.getComponent();
        Node iconSidebarContainer = iconSidebar.getComponent();

        chatAreaContainer = new VBox();
        chatAreaContainer.setStyle("-fx-background-color: white;");
        HBox.setHgrow(chatAreaContainer, Priority.ALWAYS);
        VBox.setVgrow(chatAreaContainer, Priority.ALWAYS);

        setupChatAreaLayout();

        mainContainer.getChildren().addAll(iconSidebarContainer,navSidebarContainer, chatAreaContainer);
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
                    chatPanel,
                    messageInputPanel,
                    statusBar,
                    chatHeader,
                    this::logout,
                    msg -> showInfo("Conversation Info", msg)
            );

            this.conversationController = result.conversationController();
            this.socketClient = result.socketClient();
            this.messageController = result.messageController();
            this.stateManager = result.stateManager();
            this.navigationSidebar = result.navigationSidebar();
            messageController.setActiveChatPanel(chatPanel, currentConversationId);

            NavigationSidebarHandlerBinder.bindHandlers(this);
            NavigationSidebarHandlerBinder.setupDirectMessageSelectionHandler(this,chatPanel,
                    chatHeader, messageController, currentUser,conversationController);


            setupSocketMessageListener();
            setupAutoRefresh();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to initialize chat components: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void setupSocketMessageListener() {
        if (socketClient != null) {
            socketClient.setMessageListener(message -> {
                Platform.runLater(() -> {
                    System.out.println("Received message: " + message);
                    if (message.getConversationId() == currentConversationId) {
                        chatPanel.addMessage(message);
                    } else {
                        System.out.println("Message for different conversation: " + message.getConversationId());
                    }
                });
            });
        }
    }

    private void handleSendAttachment(User sender, User receiver, AttachmentMessage attachment) {
        if (receiver == null || currentConversationId == -1) {
            System.out.println("⚠️ Không thể gửi file: thiếu thông tin receiver hoặc conversationId");
            return;
        }
        Message message = new Message();
        message.setSenderId(currentUser.getId());
        message.setReceiverId(receiver.getId());
        message.setConversationId(currentConversationId);
        message.setType(Message.MessageType.FILE);
        message.setAttachment(attachment);
        message.setContent("📎 " + attachment.getFileName());

        messageController.sendAttachmentMessage(message, chatPanel);
    }

    private void handleSendMessage(User sender, User receiver, String content) {
        System.out.println("DEBUG: handleSendMessage(...) — sender=" + sender + ", receiver=" + receiver +
                ", convId=" + currentConversationId + ", content=\"" + content + "\"");

        if (receiver == null || currentConversationId == -1) {
            System.out.println("⚠️ Không thể gửi: thiếu thông tin receiver hoặc conversationId");
            return;
        }

        messageController.setActiveChatPanel(chatPanel, currentConversationId);

        messageController.sendDirectMessage(sender, receiver, content, currentConversationId);
    }


    private void setupSystemMessage() {
        stateManager.addMessage(new Message("System",
                "Welcome back, " + currentUser.getUsername() + "! 🎉",
                Message.MessageType.SYSTEM));
        stateManager.addMessage(new Message("System",
                "Welcome back, " + currentUser.getUsername() + "! 🎉",
                Message.MessageType.SYSTEM));
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
            updateConnectionStatus(false);

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

    public MessageInputPanel getMessageInputPanel() {
        return messageInputPanel;
    }

    private void setupAutoRefresh() {
        Timeline autoRefreshTimeline = new Timeline(
                new KeyFrame(Duration.millis(500), event -> {
                    Platform.runLater(() -> {
                        chatPanel.getChatHeader().updateMessageCount(chatPanel.getMessageContainer());
                        chatPanel.getScrollPane().requestLayout();
                    });
                })
        );
        autoRefreshTimeline.setCycleCount(Timeline.INDEFINITE);
        autoRefreshTimeline.play();
    }
}