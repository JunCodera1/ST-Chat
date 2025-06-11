package me.chatapp.stchat.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import me.chatapp.stchat.model.Message;
import me.chatapp.stchat.model.MessageType;
import me.chatapp.stchat.view.components.*;
import me.chatapp.stchat.view.config.ChatViewConfig;
import me.chatapp.stchat.view.handlers.*;
import me.chatapp.stchat.view.layout.ChatViewLayoutManager;
import me.chatapp.stchat.view.state.ChatViewStateManager;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Main view class cho chat application
 * Đã được refactor để dễ quản lý và mở rộng
 */
public class ChatView extends Application {

    // Configuration
    private final ChatViewConfig config;

    // Core JavaFX components
    private final BorderPane root;
    private final Scene scene;

    // UI Components
    private HeaderComponent headerComponent;
    private ConnectionPanel connectionPanel;
    private ChatPanel chatPanel;
    private MessageInputPanel messageInputPanel;
    private StatusBar statusBar;

    // Managers
    private ChatViewLayoutManager layoutManager;
    private ChatViewStateManager stateManager;
    private ChatViewEventHandler eventHandler;

    // Legacy handlers (for backward compatibility)
    private ConnectionHandler connectionHandler;
    private MessageHandler messageHandler;
    private UIStateHandler uiStateHandler;

    public ChatView() {
        this(new ChatViewConfig());
    }

    public ChatView(ChatViewConfig config) {
        this.config = config;
        this.root = new BorderPane();
        this.scene = new Scene(root, config.getWidth(), config.getHeight());

        initializeUI();
    }

    /**
     * Khởi tạo toàn bộ UI
     */
    private void initializeUI() {
        loadStylesheets();
        initializeComponents();
        initializeManagers();
        initializeLegacyHandlers();
        setupTestData();
    }

    /**
     * Load CSS stylesheets
     */
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

    /**
     * Khởi tạo các UI components
     */
    private void initializeComponents() {
        headerComponent = new HeaderComponent();
        connectionPanel = new ConnectionPanel();
        chatPanel = new ChatPanel();
        messageInputPanel = new MessageInputPanel();
        statusBar = new StatusBar();
    }

    /**
     * Khởi tạo các manager
     */
    private void initializeManagers() {
        // Layout manager
        layoutManager = new ChatViewLayoutManager(
                root, headerComponent, connectionPanel,
                chatPanel, messageInputPanel, statusBar
        );

        // State manager
        stateManager = new ChatViewStateManager(
                connectionPanel, chatPanel, messageInputPanel, statusBar, scene
        );

        // Event handler
        eventHandler = new ChatViewEventHandler(
                config.getPort(), connectionPanel, chatPanel,
                messageInputPanel, stateManager
        );
    }

    /**
     * Khởi tạo legacy handlers để backward compatibility
     */
    private void initializeLegacyHandlers() {
        connectionHandler = new ConnectionHandler(this);
        messageHandler = new MessageHandler(this);
        uiStateHandler = new UIStateHandler(this);
    }

    /**
     * Thêm test data để demo
     */
    private void setupTestData() {
        // Test với chuỗi thông thường (TextArea)
        stateManager.addMessage("This is a plain message using TextArea.");

        // Test với đối tượng Message (ListView)
        stateManager.addMessage(new Message("Alice", "Hello, how are you?", MessageType.USER, LocalDateTime.now()));
        stateManager.addMessage(new Message("Bot", "I'm just a bot 🤖", MessageType.BOT, LocalDateTime.now()));
        stateManager.addMessage(new Message("System", "You joined the room", MessageType.SYSTEM, LocalDateTime.now()));
        stateManager.addMessage(new Message("Bot", "(Private) This is a private reply.", MessageType.BOT, LocalDateTime.now()));
    }

    @Override
    public void start(Stage primaryStage) {
        setupStage(primaryStage);
        primaryStage.show();
        stateManager.updateConnectionStatus(false);
    }

    /**
     * Thiết lập Stage properties
     */
    private void setupStage(Stage primaryStage) {
        primaryStage.setTitle(config.getTitle());
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(config.getMinWidth());
        primaryStage.setMinHeight(config.getMinHeight());

        primaryStage.setOnCloseRequest(e -> {
            handleApplicationClose();
            Platform.exit();
        });
    }


    private void handleApplicationClose() {
        if (eventHandler != null && eventHandler.getDisconnectAction() != null) {
            eventHandler.getDisconnectAction().execute();
        }
    }


    // ========== PUBLIC API ==========

    /**
     * Cập nhật trạng thái kết nối
     */
    public void updateConnectionStatus(boolean connected) {
        stateManager.updateConnectionStatus(connected);
    }

    /**
     * Thêm tin nhắn dạng String
     */
    public void addMessage(String message) {
        stateManager.addMessage(message);
    }

    /**
     * Thêm tin nhắn dạng Message object
     */
    public void addMessage(Message message) {
        stateManager.addMessage(message);
    }

    /**
     * Hiển thị lỗi
     */
    public void showError(String error) {
        stateManager.showError(error);
    }

    /**
     * Hiển thị thông tin
     */
    public void showInfo(String title, String message) {
        stateManager.showInfo(title, message);
    }

    // ========== SETTERS FOR ACTIONS ==========

    public void setOnConnectAction(ChatEventActions.ConnectAction action) {
        eventHandler.setOnConnectAction(action);
    }

    public void setOnDisconnectAction(ChatEventActions.DisconnectAction action) {
        eventHandler.setOnDisconnectAction(action);
    }

    public void setOnSendMessageAction(ChatEventActions.SendMessageAction action) {
        eventHandler.setOnSendMessageAction(action);
    }

    // ========== GETTERS FOR COMPONENTS ==========

    public HeaderComponent getHeaderComponent() { return headerComponent; }
    public ConnectionPanel getConnectionPanel() { return connectionPanel; }
    public ChatPanel getChatPanel() { return chatPanel; }
    public MessageInputPanel getMessageInputPanel() { return messageInputPanel; }
    public StatusBar getStatusBar() { return statusBar; }
    public Scene getScene() { return scene; }

    // ========== LEGACY SUPPORT ==========

    // Legacy handlers
    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public ConnectionHandler getConnectionHandler() {
        return connectionHandler;
    }

    public void setConnectionHandler(ConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    // Legacy getters for backward compatibility
    public ListView<Message> getMessageListView() { return chatPanel.getMessageListView(); }
    public TextField getMessageField() { return messageInputPanel.getMessageField(); }
    public Button getSendButton() { return messageInputPanel.getSendButton(); }
    public Button getClearButton() { return messageInputPanel.getClearButton(); }
    public Label getStatusLabel() { return statusBar.getStatusLabel(); }
    public TextField getUserNameField() { return connectionPanel.getUsernameField(); }
}