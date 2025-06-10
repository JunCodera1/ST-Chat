package me.chatapp.stchat.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import me.chatapp.stchat.model.Message;
import me.chatapp.stchat.view.components.*;
import me.chatapp.stchat.view.handlers.ConnectionHandler;
import me.chatapp.stchat.view.handlers.MessageHandler;
import me.chatapp.stchat.view.handlers.UIStateHandler;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ChatView extends Application {
    private static final String CHAT_CSS = """
        .root {
            -fx-background-color: linear-gradient(to bottom, #f8f9fa, #e9ecef);
            -fx-font-family: 'Segoe UI', 'Arial', sans-serif;
        }
        
        .main-container {
            -fx-background-color: white;
            -fx-background-radius: 15;
            -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 2);
        }
        
        .header-panel {
            -fx-background-color: linear-gradient(to right, #667eea, #764ba2);
            -fx-background-radius: 15 15 0 0;
            -fx-padding: 20;
        }
        
        .app-title {
            -fx-text-fill: white;
            -fx-font-size: 28px;
            -fx-font-weight: bold;
        }
        
        .connection-panel {
            -fx-background-color: #f8f9fa;
            -fx-padding: 20;
            -fx-spacing: 15;
        }
        
        .input-group {
            -fx-spacing: 8;
        }
        
        .input-label {
            -fx-font-weight: bold;
            -fx-text-fill: #495057;
            -fx-font-size: 14px;
        }
        
        .modern-text-field {
            -fx-background-color: white;
            -fx-border-color: #dee2e6;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
            -fx-padding: 10 15;
            -fx-font-size: 14px;
            -fx-effect: dropshadow(one-pass-box, rgba(0,0,0,0.05), 2, 0, 0, 1);
        }
        
        .modern-text-field:focused {
            -fx-border-color: #667eea;
            -fx-effect: dropshadow(one-pass-box, rgba(102,126,234,0.3), 5, 0, 0, 0);
        }
        
        .primary-button {
            -fx-background-color: linear-gradient(to bottom, #667eea, #764ba2);
            -fx-text-fill: white;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
            -fx-padding: 12 25;
            -fx-font-size: 14px;
            -fx-font-weight: bold;
            -fx-cursor: hand;
        }
        
        .primary-button:hover {
            -fx-background-color: linear-gradient(to bottom, #5a67d8, #6b46a8);
            -fx-effect: dropshadow(one-pass-box, rgba(102,126,234,0.4), 8, 0, 0, 2);
        }
        
        .secondary-button {
            -fx-background-color: #6c757d;
            -fx-text-fill: white;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
            -fx-padding: 12 25;
            -fx-font-size: 14px;
            -fx-font-weight: bold;
            -fx-cursor: hand;
        }
        
        .secondary-button:hover {
            -fx-background-color: #5a6268;
        }
        
        .danger-button {
            -fx-background-color: #dc3545;
            -fx-text-fill: white;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
            -fx-padding: 10 20;
            -fx-font-size: 14px;
            -fx-cursor: hand;
        }
        
        .danger-button:hover {
            -fx-background-color: #c82333;
        }
        
        .status-connected {
            -fx-text-fill: #28a745;
            -fx-font-weight: bold;
            -fx-font-size: 14px;
        }
        
        .status-disconnected {
            -fx-text-fill: #dc3545;
            -fx-font-weight: bold;
            -fx-font-size: 14px;
        }
        
        .chat-area {
            -fx-background-color: white;
            -fx-padding: 20;
        }
        
        .message-input-area {
            -fx-background-color: #f8f9fa;
            -fx-padding: 15;
            -fx-spacing: 10;
            -fx-border-color: #dee2e6;
            -fx-border-width: 1 0 0 0;
        }
        
        .user-message {
            -fx-background-color: #e3f2fd;
            -fx-background-radius: 15 15 5 15;
            -fx-padding: 12 15;
            -fx-border-color: #2196f3;
            -fx-border-width: 0 0 0 3;
        }
        
        .bot-message {
            -fx-background-color: #f1f8e9;
            -fx-background-radius: 15 15 15 5;
            -fx-padding: 12 15;
            -fx-border-color: #4caf50;
            -fx-border-width: 0 0 0 3;
        }
        
        .system-message {
            -fx-background-color: #fff3e0;
            -fx-background-radius: 10;
            -fx-padding: 10 15;
            -fx-border-color: #ff9800;
            -fx-border-width: 0 0 0 3;
        }
        
        .private-message {
            -fx-background-color: #fce4ec;
            -fx-background-radius: 15 15 15 5;
            -fx-padding: 12 15;
            -fx-border-color: #e91e63;
            -fx-border-width: 0 0 0 3;
        }
        """;

    // Core components
    private final BorderPane root;
    private final Scene scene;

    // UI Components - now using modular approach
    private HeaderComponent headerComponent;
    private ConnectionPanel connectionPanel;
    private ChatPanel chatPanel;
    private MessageInputPanel messageInputPanel;
    private StatusBar statusBar;

    // Handlers
    private ConnectionHandler connectionHandler;
    private MessageHandler messageHandler;
    private UIStateHandler uiStateHandler;

    // Functional interfaces for controller communication
    public interface ConnectAction {
        void execute(String host, String port, String username);
    }

    public HeaderComponent getHeaderComponent(){
        return headerComponent;
    }

    public interface DisconnectAction {
        void execute();
    }

    public interface SendMessageAction {
        void execute(String message);
    }

    private ConnectAction onConnectAction;
    private DisconnectAction onDisconnectAction;
    private SendMessageAction onSendMessageAction;

    public ChatView() {
        root = new BorderPane();
        scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add("data:text/css;base64," +
                java.util.Base64.getEncoder().encodeToString(CHAT_CSS.getBytes()));

        initializeComponents();
        initializeHandlers();
        setupLayout();
        setupEventHandlers();

        root.getStyleClass().add("root");
    }

    private void initializeComponents() {
        headerComponent = new HeaderComponent();
        connectionPanel = new ConnectionPanel();
        chatPanel = new ChatPanel();
        messageInputPanel = new MessageInputPanel();
        statusBar = new StatusBar();
    }

    private void initializeHandlers() {
        connectionHandler = new ConnectionHandler(this);
        messageHandler = new MessageHandler(this);
        uiStateHandler = new UIStateHandler(this);
    }

    private void setupLayout() {
        VBox mainContainer = new VBox();
        mainContainer.getStyleClass().add("main-container");

        // Header
        root.setTop(headerComponent.getComponent());

        // Center content
        VBox centerContent = new VBox();
        centerContent.getChildren().addAll(
                connectionPanel.getComponent(),
                chatPanel.getComponent()
        );
        VBox.setVgrow(chatPanel.getComponent(), Priority.ALWAYS);

        root.setCenter(centerContent);

        // Bottom
        VBox bottomContent = new VBox();
        bottomContent.getChildren().addAll(
                messageInputPanel.getComponent(),
                statusBar.getComponent()
        );

        root.setBottom(bottomContent);
    }

    private void setupEventHandlers() {
        // Connection events
        connectionPanel.getConnectButton().setOnAction(e -> {
            if (onConnectAction != null) {
                onConnectAction.execute(
                        connectionPanel.getHostField().getText(),
                        connectionPanel.getPortField().getText(),
                        connectionPanel.getUsernameField().getText()
                );
            }
        });

        connectionPanel.getDisconnectButton().setOnAction(e -> {
            if (onDisconnectAction != null) {
                onDisconnectAction.execute();
            }
        });

        // Message events
        messageInputPanel.getSendButton().setOnAction(e -> sendMessage());
        messageInputPanel.getMessageField().setOnAction(e -> sendMessage());
        messageInputPanel.getClearButton().setOnAction(e -> chatPanel.clearMessages());

        // Enter key support
        connectionPanel.getUsernameField().setOnAction(e ->
                connectionPanel.getConnectButton().fire());
    }

    private void sendMessage() {
        String message = messageInputPanel.getMessageField().getText().trim();
        if (!message.isEmpty() && onSendMessageAction != null) {
            onSendMessageAction.execute(message);
            messageInputPanel.getMessageField().clear();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("ST Chat - Modern Interface");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);

        primaryStage.setOnCloseRequest(e -> {
            if (onDisconnectAction != null) {
                onDisconnectAction.execute();
            }
            Platform.exit();
        });

        primaryStage.show();
        updateConnectionStatus(false);
    }

    public void updateConnectionStatus(boolean connected) {
        Platform.runLater(() -> {
            uiStateHandler.updateConnectionState(connected);

            if (connected) {
                statusBar.setStatus("Connected", true);
                messageInputPanel.getMessageField().requestFocus();
            } else {
                statusBar.setStatus("Disconnected", false);
            }
        });
    }

    public void addMessage(String message) {
        Platform.runLater(() -> {
            String timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            chatPanel.addMessage("[" + timestamp + "] " + message);
        });
    }

    public void addMessage(Message message) {
        Platform.runLater(() -> {
            chatPanel.addMessage(message);
        });
    }

    public void showError(String error) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connection Error");
            alert.setHeaderText("Unable to connect");
            alert.setContentText(error);
            alert.getDialogPane().getStylesheets().add(scene.getStylesheets().get(0));
            alert.showAndWait();
        });
    }

    public void showInfo(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.getDialogPane().getStylesheets().add(scene.getStylesheets().get(0));
            alert.showAndWait();
        });
    }

    // Setters for actions
    public void setOnConnectAction(ConnectAction action) {
        this.onConnectAction = action;
    }

    public void setOnDisconnectAction(DisconnectAction action) {
        this.onDisconnectAction = action;
    }

    public void setOnSendMessageAction(SendMessageAction action) {
        this.onSendMessageAction = action;
    }

    // Getters for components (for external access if needed)
    public ConnectionPanel getConnectionPanel() { return connectionPanel; }
    public ChatPanel getChatPanel() { return chatPanel; }
    public MessageInputPanel getMessageInputPanel() { return messageInputPanel; }
    public StatusBar getStatusBar() { return statusBar; }
    public Scene getScene() { return scene; }

    // Legacy getters for backward compatibility
    public ListView<Message> getMessageListView() { return chatPanel.getMessageListView(); }
    public TextField getMessageField() { return messageInputPanel.getMessageField(); }
    public Button getSendButton() { return messageInputPanel.getSendButton(); }
    public Button getClearButton() { return messageInputPanel.getClearButton(); }
    public Label getStatusLabel() { return statusBar.getStatusLabel(); }
    public TextField getUserNameField() { return connectionPanel.getUsernameField(); }
}