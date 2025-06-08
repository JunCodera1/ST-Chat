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

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ChatView extends Application {
    private final BorderPane root;
    private final Scene scene;
    private ListView<Message> messageListView;
    private Button clearButton;
    private TextField usernameField;
    private TextField hostField;
    private TextField portField;
    private Button connectButton;
    private Button disconnectButton;
    private Label statusLabel;

    // Chat components
    private TextArea chatArea;
    private TextField messageField;
    private Button sendButton;

    // Functional interfaces for controller communication
    public interface ConnectAction {
        void execute(String host, String port, String username);
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
        scene = new Scene(root, 800, 600);

        setupHeader();
        setupChatArea();
        setupInputArea();
        setupStatusBar();

        root.getStyleClass().add("root");
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Chat Client");
        primaryStage.setScene(new Scene(createMainLayout(), 500, 600));
        primaryStage.setOnCloseRequest(e -> {
            if (onDisconnectAction != null) {
                onDisconnectAction.execute();
            }
            Platform.exit();
        });
        primaryStage.show();

        // Set initial state
        updateConnectionStatus(false);
    }

    public void updateConnectionStatus(boolean connected) {
        Platform.runLater(() -> {
            if (connected) {
                statusLabel.setText("Đã kết nối");
                statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                connectButton.setDisable(true);
                disconnectButton.setDisable(false);
                hostField.setDisable(true);
                portField.setDisable(true);
                usernameField.setDisable(true);
                messageField.setDisable(false);
                sendButton.setDisable(false);
                messageField.requestFocus();
            } else {
                statusLabel.setText("Chưa kết nối");
                statusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                connectButton.setDisable(false);
                disconnectButton.setDisable(true);
                hostField.setDisable(false);
                portField.setDisable(false);
                usernameField.setDisable(false);
                messageField.setDisable(true);
                sendButton.setDisable(true);
            }
        });
    }

    public void addMessage(String message) {
        Platform.runLater(() -> {
            if (chatArea != null) {
                String timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                chatArea.appendText("[" + timestamp + "] " + message + "\n");
            }
        });
    }

    private VBox createMainLayout() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        // Connection panel
        TitledPane connectionPane = new TitledPane("Kết nối", createConnectionPanel());
        connectionPane.setCollapsible(false);

        // Chat panel
        TitledPane chatPane = new TitledPane("Chat", createChatPanel());
        chatPane.setCollapsible(false);

        root.getChildren().addAll(connectionPane, chatPane);
        VBox.setVgrow(chatPane, Priority.ALWAYS);

        return root;
    }

    private VBox createConnectionPanel() {
        VBox connectionBox = new VBox(5);

        // Host input
        HBox hostBox = new HBox(5);
        hostBox.setAlignment(Pos.CENTER_LEFT);
        Label hostLabel = new Label("Server:");
        hostLabel.setMinWidth(80);
        hostField = new TextField("localhost");
        hostField.setPromptText("localhost");
        hostBox.getChildren().addAll(hostLabel, hostField);

        // Port input
        HBox portBox = new HBox(5);
        portBox.setAlignment(Pos.CENTER_LEFT);
        Label portLabel = new Label("Port:");
        portLabel.setMinWidth(80);
        portField = new TextField("12345");
        portField.setPromptText("12345");
        portBox.getChildren().addAll(portLabel, portField);

        // Username input
        HBox usernameBox = new HBox(5);
        usernameBox.setAlignment(Pos.CENTER_LEFT);
        Label usernameLabel = new Label("Tên:");
        usernameLabel.setMinWidth(80);
        usernameField = new TextField();
        usernameField.setPromptText("Nhập tên của bạn");
        usernameBox.getChildren().addAll(usernameLabel, usernameField);

        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        connectButton = new Button("Kết nối");
        disconnectButton = new Button("Ngắt kết nối");

        connectButton.setOnAction(e -> {
            if (onConnectAction != null) {
                onConnectAction.execute(hostField.getText(), portField.getText(), usernameField.getText());
            }
        });

        disconnectButton.setOnAction(e -> {
            if (onDisconnectAction != null) {
                onDisconnectAction.execute();
            }
        });

        buttonBox.getChildren().addAll(connectButton, disconnectButton);

        // Status
        statusLabel = new Label("Chưa kết nối");
        statusLabel.setStyle("-fx-font-weight: bold;");

        connectionBox.getChildren().addAll(hostBox, portBox, usernameBox, buttonBox, statusLabel);

        // Enter key support
        usernameField.setOnAction(e -> connectButton.fire());

        return connectionBox;
    }

    private VBox createChatPanel() {
        VBox chatBox = new VBox(5);

        // Chat area
        chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setWrapText(true);
        chatArea.setStyle("-fx-font-family: 'Courier New', monospace;");
        VBox.setVgrow(chatArea, Priority.ALWAYS);

        // Message input
        HBox messageBox = new HBox(5);
        messageField = new TextField();
        messageField.setPromptText("Nhập tin nhắn...");
        sendButton = new Button("Gửi");

        messageField.setOnAction(e -> sendButton.fire());
        sendButton.setOnAction(e -> {
            String message = messageField.getText().trim();
            if (!message.isEmpty() && onSendMessageAction != null) {
                onSendMessageAction.execute(message);
                messageField.clear();
            }
        });

        HBox.setHgrow(messageField, Priority.ALWAYS);
        messageBox.getChildren().addAll(messageField, sendButton);

        chatBox.getChildren().addAll(chatArea, messageBox);

        return chatBox;
    }

    private void setupHeader() {
        VBox header = new VBox();
        header.getStyleClass().add("header");
        header.setPadding(new Insets(15));
        header.setSpacing(10);

        Label titleLabel = new Label("ST Chat Application");
        titleLabel.getStyleClass().add("title");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));

        HBox userBox = new HBox(10);
        userBox.setAlignment(Pos.CENTER_LEFT);
        Label userLabel = new Label("Tên người dùng:");
        userLabel.getStyleClass().add("user-label");

        // Initialize usernameField if not already done
        if (usernameField == null) {
            usernameField = new TextField("User");
        }
        usernameField.getStyleClass().add("user-field");
        usernameField.setPrefWidth(150);

        userBox.getChildren().addAll(userLabel, usernameField);
        header.getChildren().addAll(titleLabel, userBox);

        root.setTop(header);
    }

    private void setupChatArea() {
        VBox chatContainer = new VBox();
        chatContainer.getStyleClass().add("chat-container");
        chatContainer.setPadding(new Insets(10));

        Label chatLabel = new Label("Tin nhắn:");
        chatLabel.getStyleClass().add("section-label");

        messageListView = new ListView<>();
        messageListView.getStyleClass().add("message-list");
        messageListView.setCellFactory(listView -> new MessageListCell());

        VBox.setVgrow(messageListView, Priority.ALWAYS);
        chatContainer.getChildren().addAll(chatLabel, messageListView);

        root.setCenter(chatContainer);
    }

    private void setupInputArea() {
        VBox inputContainer = new VBox();
        inputContainer.getStyleClass().add("input-container");
        inputContainer.setPadding(new Insets(15));
        inputContainer.setSpacing(10);

        Label inputLabel = new Label("Nhập tin nhắn:");
        inputLabel.getStyleClass().add("section-label");

        HBox inputBox = new HBox(10);
        inputBox.setAlignment(Pos.CENTER);

        // Initialize messageField if not already done
        if (messageField == null) {
            messageField = new TextField();
        }
        messageField.getStyleClass().add("input-field");
        messageField.setPromptText("Nhập tin nhắn của bạn...");
        HBox.setHgrow(messageField, Priority.ALWAYS);

        // Initialize sendButton if not already done
        if (sendButton == null) {
            sendButton = new Button("Gửi");
        }
        sendButton.getStyleClass().addAll("button", "send-button");
        sendButton.setPrefWidth(80);

        clearButton = new Button("Xóa");
        clearButton.getStyleClass().addAll("button", "clear-button");
        clearButton.setPrefWidth(80);

        inputBox.getChildren().addAll(messageField, sendButton, clearButton);
        inputContainer.getChildren().addAll(inputLabel, inputBox);

        root.setBottom(inputContainer);
    }

    private void setupStatusBar() {
        HBox statusBar = new HBox();
        statusBar.getStyleClass().add("status-bar");
        statusBar.setPadding(new Insets(5, 15, 5, 15));

        // Initialize statusLabel if not already done
        if (statusLabel == null) {
            statusLabel = new Label("Sẵn sàng");
        }
        statusLabel.getStyleClass().add("status-label");

        statusBar.getChildren().add(statusLabel);

        VBox bottomContainer = new VBox();
        bottomContainer.getChildren().addAll(root.getBottom(), statusBar);
        root.setBottom(bottomContainer);
    }

    public void showError(String error) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText(null);
            alert.setContentText(error);
            alert.showAndWait();
        });
    }

    public void setOnConnectAction(ConnectAction action) {
        this.onConnectAction = action;
    }

    public void setOnDisconnectAction(DisconnectAction action) {
        this.onDisconnectAction = action;
    }

    public void setOnSendMessageAction(SendMessageAction action) {
        this.onSendMessageAction = action;
    }

    // Getters
    public Scene getScene() {
        return scene;
    }

    public ListView<Message> getMessageListView() {
        return messageListView;
    }

    public TextField getMessageField() {
        return messageField;
    }

    public Button getSendButton() {
        return sendButton;
    }

    public Button getClearButton() {
        return clearButton;
    }

    public Label getStatusLabel() {
        return statusLabel;
    }

    public TextField getUserNameField() {
        return usernameField;
    }

    private static class MessageListCell extends ListCell<Message> {
        @Override
        protected void updateItem(Message message, boolean empty) {
            super.updateItem(message, empty);

            if (empty || message == null) {
                setGraphic(null);
                setText(null);
            } else {
                VBox messageBox = new VBox(5);
                messageBox.setPadding(new Insets(8));

                HBox headerBox = new HBox();
                headerBox.setSpacing(10);

                Label senderLabel = new Label(message.getSender());
                senderLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

                Label timeLabel = new Label(message.getFormattedTime());
                timeLabel.setFont(Font.font("System", 10));
                timeLabel.setTextFill(Color.GRAY);

                headerBox.getChildren().addAll(senderLabel, timeLabel);

                Label contentLabel = new Label(message.getContent());
                contentLabel.setWrapText(true);
                contentLabel.setFont(Font.font("System", 13));

                messageBox.getChildren().addAll(headerBox, contentLabel);

                // Style dựa trên loại tin nhắn
                switch (message.getType()) {
                    case USER:
                        messageBox.getStyleClass().add("user-message");
                        senderLabel.setTextFill(Color.BLUE);
                        break;
                    case BOT:
                        messageBox.getStyleClass().add("bot-message");
                        senderLabel.setTextFill(message.getContent().startsWith("(Private)") ? Color.PURPLE : Color.GREEN);
                        if (message.getContent().startsWith("(Private)")) {
                            contentLabel.setText("(Private) " + message.getContent());
                        }
                        break;
                    case SYSTEM:
                        messageBox.getStyleClass().add("system-message");
                        senderLabel.setTextFill(Color.ORANGE);
                        break;
                }

                setGraphic(messageBox);
                setText(null);
            }
        }
    }
}

