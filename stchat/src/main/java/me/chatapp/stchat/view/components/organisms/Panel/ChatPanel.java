package me.chatapp.stchat.view.components.organisms.Panel;

import javafx.application.HostServices;
import javafx.application.Platform;
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

import static me.chatapp.stchat.util.CSSUtil.*;

public class ChatPanel {
    private final VBox chatContainer;
    private final ScrollPane scrollPane;
    private final VBox messageContainer;
    private final User currentUser;
    private final ChatHeader chatHeader;
    private int conversationId = -1;
    private final MessageRenderer messageRenderer;
    private final MessageActions messageActions;
    private final ChatUtils chatUtils;

    // Thêm các biến để kiểm soát scroll
    private boolean isAutoScrolling = false;
    private boolean shouldAutoScroll = true;
    private double lastUserScrollPosition = 1.0;

    static {
        new MessageController();
    }

    public ChatPanel(User currentUser, MessageInputPanel messageInputPanel, HostServices hostServices) {
        this.currentUser = currentUser;
        chatContainer = new VBox();
        chatContainer.setSpacing(0);
        chatContainer.setStyle(STYLE_CHAT_CONTAINER);

        chatHeader = new ChatHeader();
        messageRenderer = new MessageRenderer(currentUser, hostServices);
        messageActions = new MessageActions(this, messageRenderer);
        chatUtils = new ChatUtils();

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
        scrollPane.setFitToHeight(true); // Thay đổi từ false thành true

        // Thêm listener để theo dõi user scroll
        setupScrollListener();

        chatContainer.getChildren().addAll(
                chatHeader.getHeaderBox(),
                chatUtils.createSeparator(),
                scrollPane,
                messageInputPanel.getComponent()
        );

        VBox.setVgrow(scrollPane, Priority.ALWAYS);
    }

    private void setupScrollListener() {
        scrollPane.vvalueProperty().addListener((obs, oldVal, newVal) -> {
            if (!isAutoScrolling) {
                lastUserScrollPosition = newVal.doubleValue();
                // Chỉ tắt auto-scroll nếu user scroll lên khá xa
                shouldAutoScroll = newVal.doubleValue() > 0.9;
            }
        });

        // Loại bỏ listener phức tạp cho heightProperty
        // Thay vào đó chỉ scroll khi cần thiết
    }

    private void scrollToBottom() {
        isAutoScrolling = true;
        Platform.runLater(() -> {
            scrollPane.setVvalue(1.0);
            Platform.runLater(() -> {
                isAutoScrolling = false;
            });
        });
    }

    public void setCurrentContact(String name, String type) {
        chatHeader.setCurrentContact(name, type);
    }

    public void addMessage(Message message) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> addMessage(message));
            return;
        }

        // Render message ngay lập tức
        messageRenderer.addMessage(messageContainer, message, messageActions);
        chatHeader.updateMessageCount(messageContainer);

        // Force layout update để đảm bảo UI được refresh
        messageContainer.applyCss();
        messageContainer.layout();

        // Scroll đến bottom nếu cần (với cách đơn giản hơn)
        if (shouldAutoScroll) {
            scrollToBottomImmediate();
        }
    }

    private void scrollToBottomImmediate() {
        // Đơn giản hóa scroll logic
        Platform.runLater(() -> {
            scrollPane.setVvalue(1.0);
            isAutoScrolling = false; // Reset flag
        });
    }


    public void clearMessages() {
        chatUtils.clearMessages(messageContainer);
        chatHeader.updateMessageCount(messageContainer);
        shouldAutoScroll = true; // Reset auto-scroll khi clear
    }

    public void showTypingIndicator(String senderName) {
        chatUtils.showTypingIndicator(messageContainer, senderName, scrollPane);
        if (shouldAutoScroll) {
            scrollToBottom();
        }
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
        shouldAutoScroll = false; // Tắt auto-scroll khi user search/scroll đến message cụ thể
        chatUtils.scrollToMessage(scrollPane, messageContainer, targetMessage);
    }

    // Thêm method để user có thể scroll lên trên mà không bị auto-scroll
    public void enableManualScroll() {
        shouldAutoScroll = false;
    }

    // Thêm method để bật lại auto-scroll
    public void enableAutoScroll() {
        shouldAutoScroll = true;
        scrollToBottom();
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

    public void setConversationId(int conversationId) {
        this.conversationId = conversationId;
    }

    public int getConversationId() {
        return conversationId;
    }
}