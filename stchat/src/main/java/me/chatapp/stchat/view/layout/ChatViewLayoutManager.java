package me.chatapp.stchat.view.layout;

import javafx.scene.layout.*;
import me.chatapp.stchat.view.components.organisms.Bar.StatusBar;
import me.chatapp.stchat.view.components.organisms.Header.HeaderComponent;
import me.chatapp.stchat.view.components.organisms.Panel.ChatPanel;
import me.chatapp.stchat.view.components.organisms.Panel.ConnectionPanel;
import me.chatapp.stchat.view.components.organisms.Panel.MessageInputPanel;


public class ChatViewLayoutManager {

    private final BorderPane root;
    private final HeaderComponent headerComponent;
    private final ConnectionPanel connectionPanel;
    private final ChatPanel chatPanel;
    private final MessageInputPanel messageInputPanel;
    private final StatusBar statusBar;

    public ChatViewLayoutManager(BorderPane root,
                                 HeaderComponent headerComponent,
                                 ConnectionPanel connectionPanel,
                                 ChatPanel chatPanel,
                                 MessageInputPanel messageInputPanel,
                                 StatusBar statusBar) {
        this.root = root;
        this.headerComponent = headerComponent;
        this.connectionPanel = connectionPanel;
        this.chatPanel = chatPanel;
        this.messageInputPanel = messageInputPanel;
        this.statusBar = statusBar;

        setupLayout();
    }


    private void setupLayout() {
        setupHeader();
        setupCenterContent();
        setupBottomContent();

        root.getStyleClass().add("root");
    }


    private void setupHeader() {
        root.setTop(headerComponent.getComponent());
    }


    private void setupCenterContent() {
        VBox centerContent = new VBox();
        centerContent.getStyleClass().add("center-content");

        centerContent.getChildren().addAll(
                connectionPanel.getComponent(),
                chatPanel.getComponent()
        );

        VBox.setVgrow(chatPanel.getComponent(), Priority.ALWAYS);

        root.setCenter(centerContent);
    }


    private void setupBottomContent() {
        VBox bottomContent = new VBox();
        bottomContent.getStyleClass().add("bottom-content");

        bottomContent.getChildren().addAll(
                messageInputPanel.getComponent(),
                statusBar.getComponent()
        );

        root.setBottom(bottomContent);
    }


    public void refreshLayout() {
        root.requestLayout();
    }

    public void setupResponsiveLayout(double width, double height) {
        if (width < 600) {
            setupCompactLayout();
        } else {
            setupNormalLayout();
        }
    }

    private void setupCompactLayout() {

    }

    private void setupNormalLayout() {
    }
}