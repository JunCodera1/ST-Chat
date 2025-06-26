package me.chatapp.stchat.view.components.organisms.Header;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import me.chatapp.stchat.view.components.atoms.Circle.AvatarCircleHeader;
import me.chatapp.stchat.view.components.atoms.Button.IconButton;
import me.chatapp.stchat.view.components.molecules.Conversation.ConversationInfoBlock;

public class ChatHeader {
    private final HBox container;
    private final AvatarCircleHeader avatarCircle;
    private final ConversationInfoBlock infoBlock;
    private final IconButton callBtn, videoBtn, infoBtn;

    private Runnable onCallAction, onVideoCallAction, onInfoAction;

    public ChatHeader() {
        container = new HBox(12);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(12));
        container.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #e4e6ea;
            -fx-border-width: 0 0 1 0;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 3, 0, 0, 1);
            """);

        avatarCircle = new AvatarCircleHeader("?", Color.web("#42b883"));

        infoBlock = new ConversationInfoBlock();
        HBox.setHgrow(infoBlock, Priority.ALWAYS);

        callBtn = new IconButton("fas-phone");
        videoBtn = new IconButton("fas-video");
        infoBtn = new IconButton("fas-info-circle");


        callBtn.setOnAction(e -> { if (onCallAction != null) onCallAction.run(); });
        videoBtn.setOnAction(e -> { if (onVideoCallAction != null) onVideoCallAction.run(); });
        infoBtn.setOnAction(e -> { if (onInfoAction != null) onInfoAction.run(); });

        HBox actionButtons = new HBox(8, callBtn, videoBtn, infoBtn);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);

        container.getChildren().addAll(avatarCircle, infoBlock, actionButtons);
        setButtonsEnabled(false);
    }

    public void setActiveConversation(String name) {
        infoBlock.setName(name);
        infoBlock.setStatus("Active now");

        avatarCircle.setInitials(name);
        avatarCircle.setColor(getColorFromName(name));
        setButtonsEnabled(true);
    }

    public void setOnlineStatus(String status) {
        infoBlock.setStatus(status);
    }

    public void setButtonsEnabled(boolean enabled) {
        callBtn.setEnabled(enabled);
        videoBtn.setEnabled(enabled);
        infoBtn.setEnabled(enabled);
    }

    public void setOnCallAction(Runnable r) { this.onCallAction = r; }
    public void setOnVideoCallAction(Runnable r) { this.onVideoCallAction = r; }
    public void setOnInfoAction(Runnable r) { this.onInfoAction = r; }

    public HBox getComponent() {
        return container;
    }

    private Color getColorFromName(String name) {
        String[] palette = {
                "#42b883", "#1877f2", "#fd79a8", "#6c5ce7", "#a29bfe",
                "#fd63c3", "#ee5a24", "#00b894", "#0984e3", "#e17055",
                "#81ecec", "#fab1a0"
        };
        return Color.web(palette[Math.abs(name.hashCode()) % palette.length]);
    }
}
