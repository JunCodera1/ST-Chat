package me.chatapp.stchat.view.components.molecules;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import me.chatapp.stchat.view.components.atom.AvatarCircle;
import me.chatapp.stchat.view.components.organisms.ConversationSidebar;

public class ConversationPreviewItem extends HBox {
    private final Label nameLabel, messageLabel, timeLabel;

    public ConversationPreviewItem(ConversationSidebar.ConversationItem item) {
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(12);
        setPrefHeight(72);

        AvatarCircle avatar = new AvatarCircle(item.getName());

        // Name, Time
        nameLabel = new Label(item.getName());
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 15));
        nameLabel.setTextFill(Color.web("#050505"));

        timeLabel = new Label(item.getTime());
        timeLabel.setFont(Font.font("System", 13));
        timeLabel.setTextFill(Color.web("#65676b"));

        HBox topRow = new HBox(nameLabel, new Region(), timeLabel);
        HBox.setHgrow(topRow.getChildren().get(1), Priority.ALWAYS);

        // Message
        String msg = item.getLastMessage();
        messageLabel = new Label(msg.length() > 30 ? msg.substring(0, 27) + "..." : msg);
        messageLabel.setFont(Font.font("System", 14));
        messageLabel.setTextFill(Color.web("#65676b"));

        HBox bottomRow = new HBox(messageLabel);
        bottomRow.setAlignment(Pos.CENTER_LEFT);

        if (item.hasUnread()) {
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            Circle dot = new Circle(6, Color.web("#1877f2"));
            bottomRow.getChildren().addAll(spacer, dot);
        }

        VBox content = new VBox(4, topRow, bottomRow);
        HBox.setHgrow(content, Priority.ALWAYS);

        getChildren().addAll(avatar, content);
    }

    public void setSelected(boolean selected) {
        nameLabel.setTextFill(selected ? Color.WHITE : Color.web("#050505"));
        timeLabel.setTextFill(selected ? Color.WHITE : Color.web("#65676b"));
        messageLabel.setTextFill(selected ? Color.WHITE : Color.web("#65676b"));
    }
}

