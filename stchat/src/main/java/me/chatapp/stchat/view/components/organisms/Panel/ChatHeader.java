package me.chatapp.stchat.view.components.organisms.Panel;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import me.chatapp.stchat.model.Message;

import static me.chatapp.stchat.util.CSSUtil.*;

public class ChatHeader {
    private final Label chatTitleLabel;
    private final Label messageCountLabel;
    private final HBox headerBox;

    public ChatHeader() {
        headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPadding(new Insets(15, 20, 15, 20));
        headerBox.setStyle("-fx-background-color: #ffffff;");

        chatTitleLabel = new Label("Messages");
        chatTitleLabel.setStyle(STYLE_CHAT_TITLE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        messageCountLabel = new Label("0 messages");
        messageCountLabel.setStyle(STYLE_MESSAGE_COUNT);

        headerBox.getChildren().addAll(chatTitleLabel, spacer, messageCountLabel);
    }

    public void setCurrentContact(String name, String type) {
        Platform.runLater(() -> {
            if (type.equalsIgnoreCase("user")) {
                chatTitleLabel.setText("Chat with " + name);
            } else if (type.equalsIgnoreCase("channel")) {
                chatTitleLabel.setText("#" + name);
            } else {
                chatTitleLabel.setText(name);
            }
        });
    }

    public void setChatTitle(String title) {
        Platform.runLater(() -> chatTitleLabel.setText(title));
    }

    public void updateMessageCount(VBox messageContainer) {
        int count = 0;
        for (javafx.scene.Node node : messageContainer.getChildren()) {
            if (node instanceof VBox vbox && vbox.getUserData() instanceof Message) {
                count++;
            }
        }
        messageCountLabel.setText(count + " message" + (count != 1 ? "s" : ""));
    }

    public HBox getHeaderBox() {
        return headerBox;
    }
}