package me.chatapp.stchat.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import me.chatapp.stchat.model.Message;

public class ChatView {
    private final BorderPane root;
    private final Scene scene;
    private ListView<Message> messageListView;
    private TextField inputField;
    private Button sendButton;
    private Button clearButton;
    private Label statusLabel;
    private TextField userNameField;

    public ChatView() {
        root = new BorderPane();
        scene = new Scene(root, 800, 600);

        // Apply CSS styling
        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());

        setupHeader();
        setupChatArea();
        setupInputArea();
        setupStatusBar();

        root.getStyleClass().add("root");
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

        userNameField = new TextField("User");
        userNameField.getStyleClass().add("user-field");
        userNameField.setPrefWidth(150);

        userBox.getChildren().addAll(userLabel, userNameField);
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

        inputField = new TextField();
        inputField.getStyleClass().add("input-field");
        inputField.setPromptText("Nhập tin nhắn của bạn...");
        HBox.setHgrow(inputField, Priority.ALWAYS);

        sendButton = new Button("Gửi");
        sendButton.getStyleClass().addAll("button", "send-button");
        sendButton.setPrefWidth(80);

        clearButton = new Button("Xóa");
        clearButton.getStyleClass().addAll("button", "clear-button");
        clearButton.setPrefWidth(80);

        inputBox.getChildren().addAll(inputField, sendButton, clearButton);
        inputContainer.getChildren().addAll(inputLabel, inputBox);

        root.setBottom(inputContainer);
    }

    private void setupStatusBar() {
        HBox statusBar = new HBox();
        statusBar.getStyleClass().add("status-bar");
        statusBar.setPadding(new Insets(5, 15, 5, 15));

        statusLabel = new Label("Sẵn sàng");
        statusLabel.getStyleClass().add("status-label");

        statusBar.getChildren().add(statusLabel);

        VBox bottomContainer = new VBox();
        bottomContainer.getChildren().addAll(root.getBottom(), statusBar);
        root.setBottom(bottomContainer);
    }

    // Getters
    public Scene getScene() { return scene; }
    public ListView<Message> getMessageListView() { return messageListView; }
    public TextField getInputField() { return inputField; }
    public Button getSendButton() { return sendButton; }
    public Button getClearButton() { return clearButton; }
    public Label getStatusLabel() { return statusLabel; }
    public TextField getUserNameField() { return userNameField; }

    // Custom ListCell for messages
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

                // Style based on message type
                switch (message.getType()) {
                    case USER:
                        messageBox.getStyleClass().add("user-message");
                        senderLabel.setTextFill(Color.BLUE);
                        break;
                    case BOT:
                        messageBox.getStyleClass().add("bot-message");
                        senderLabel.setTextFill(Color.GREEN);
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
