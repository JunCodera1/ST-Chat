package me.chatapp.stchat.view.components.organisms.Panel;

import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import me.chatapp.stchat.controller.MessageController;
import me.chatapp.stchat.model.Message;
import me.chatapp.stchat.model.User;
import me.chatapp.stchat.util.ChatUtils;
import me.chatapp.stchat.util.MessageActions;
import me.chatapp.stchat.util.MessageRenderer;

import java.io.File;
import java.time.LocalDateTime;
import java.util.function.Consumer;

import static me.chatapp.stchat.util.CSSUtil.*;

public class ChatPanel {
    private final VBox chatContainer;
    private final ScrollPane scrollPane;
    private final VBox messageContainer;
    private final User currentUser;
    private final ChatHeader chatHeader;
    private final MessageRenderer messageRenderer;
    private final MessageActions messageActions;
    private final ChatUtils chatUtils;
    private final MessageInputPanel messageInputPanel;
    private Consumer<String> sendMessageCallback;
    private final MessageController messageController = new MessageController();


    public ChatPanel(User currentUser, MessageInputPanel messageInputPanel) {
        this.currentUser = currentUser;
        chatContainer = new VBox();
        chatContainer.setSpacing(0);
        chatContainer.setStyle(STYLE_CHAT_CONTAINER);

        chatHeader = new ChatHeader();
        messageRenderer = new MessageRenderer(currentUser);
        messageActions = new MessageActions(this, messageRenderer);
        chatUtils = new ChatUtils();

        this.messageInputPanel = messageInputPanel;

        // Create message container
        messageContainer = new VBox();
        messageContainer.setSpacing(2);
        messageContainer.setPadding(new Insets(10, 15, 10, 15));
        messageContainer.setStyle(STYLE_MESSAGE_CONTAINER);

        // Add empty state
        chatUtils.addEmptyState(messageContainer);

        // Scroll pane for messages
        scrollPane = new ScrollPane(messageContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle(STYLE_SCROLL_PANE);

        chatContainer.getChildren().addAll(
                chatHeader.getHeaderBox(),
                chatUtils.createSeparator(),
                scrollPane,
                messageInputPanel.getComponent()
        );

        VBox.setVgrow(scrollPane, Priority.ALWAYS);
    }

    public void setCurrentContact(String name, String type) {
        chatHeader.setCurrentContact(name, type);
    }

    public void addMessage(Message message) {
        messageRenderer.addMessage(messageContainer, message, messageActions);
        chatUtils.scrollToBottom(scrollPane, messageContainer);
        chatHeader.updateMessageCount(messageContainer);
    }

    public void clearMessages() {
        chatUtils.clearMessages(messageContainer);
        chatHeader.updateMessageCount(messageContainer);
    }

    public void showTypingIndicator(String senderName) {
        chatUtils.showTypingIndicator(messageContainer, senderName, scrollPane);
    }

    public void hideTypingIndicator() {
        chatUtils.hideTypingIndicator(messageContainer);
    }

    public void searchMessages(String query) {
        chatUtils.searchMessages(messageContainer, query);
    }

    public void exportChatHistory(File file) {
        chatUtils.exportChatHistory(file, getAllMessages());
    }

    public void scrollToMessage(Message targetMessage) {
        chatUtils.scrollToMessage(scrollPane, messageContainer, targetMessage);
    }

    public VBox getComponent() {
        return chatContainer;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public ScrollPane getScrollPane() {
        return scrollPane;
    }

    public VBox getMessageContainer() {
        return messageContainer;
    }

    public java.util.List<Message> getAllMessages() {
        return chatUtils.getAllMessages(messageContainer);
    }

    public int getMessageCount() {
        return chatUtils.getMessageCount(messageContainer);
    }

    public void setMessageContainerBackground(String cssStyle) {
        messageContainer.setStyle(cssStyle);
    }

    public void setMessageContainerPadding(Insets padding) {
        messageContainer.setPadding(padding);
    }

    public void setChatTitle(String title) {
        chatHeader.setChatTitle(title);
    }

    public ChatHeader getChatHeader() {
        return chatHeader;
    }
}