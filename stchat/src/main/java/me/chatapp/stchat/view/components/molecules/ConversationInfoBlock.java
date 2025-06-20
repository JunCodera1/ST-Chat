package me.chatapp.stchat.view.components.molecules;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ConversationInfoBlock extends VBox {
    private final Label nameLabel;
    private final Label statusLabel;

    public ConversationInfoBlock() {
        super(2);
        nameLabel = new Label("Select a conversation");
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        nameLabel.setTextFill(Color.web("#050505"));

        statusLabel = new Label("Click on a conversation to start chatting");
        statusLabel.setFont(Font.font("System", 13));
        statusLabel.setTextFill(Color.web("#65676b"));

        getChildren().addAll(nameLabel, statusLabel);
    }

    public void setName(String name) {
        nameLabel.setText(name);
    }

    public void setStatus(String status) {
        statusLabel.setText(status);
    }

    public String getName() {
        return nameLabel.getText();
    }

    public String getStatus() {
        return statusLabel.getText();
    }
}
