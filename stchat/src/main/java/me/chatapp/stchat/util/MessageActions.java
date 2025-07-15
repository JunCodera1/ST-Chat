package me.chatapp.stchat.util;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import me.chatapp.stchat.model.AttachmentMessage;
import me.chatapp.stchat.model.Message;
import me.chatapp.stchat.view.components.organisms.Panel.ChatPanel;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static me.chatapp.stchat.util.CSSUtil.STYLE_CONTENT_LABEL;

public class MessageActions {
    private final ChatPanel chatPanel;
    private final MessageRenderer messageRenderer;

    public MessageActions(ChatPanel chatPanel, MessageRenderer messageRenderer) {
        this.chatPanel = chatPanel;
        this.messageRenderer = messageRenderer;
    }

    public HBox createActionBar(Message message) {
        HBox actionBar = new HBox();
        actionBar.setSpacing(8);
        actionBar.setAlignment(Pos.CENTER_RIGHT);

        Button replyBtn = new Button();
        replyBtn.setGraphic(new FontIcon(FontAwesome.REPLY));
        replyBtn.setOnAction(e -> handleReply(message));

        if (!message.hasAttachment()) {
            Button editBtn = new Button();
            editBtn.setGraphic(new FontIcon(FontAwesome.EDIT));
            editBtn.setOnAction(e -> handleEdit(message));
            actionBar.getChildren().add(editBtn);
        }

        Button deleteBtn = new Button();
        deleteBtn.setGraphic(new FontIcon(FontAwesome.TRASH));
        deleteBtn.setOnAction(e -> handleDelete(message));

        actionBar.getChildren().addAll(replyBtn, deleteBtn);
        return actionBar;
    }

    public void handleReply(Message original) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Reply to " + original.getSender());
        dialog.setHeaderText("Enter your reply:");
        dialog.setContentText("Reply:");

        dialog.showAndWait().ifPresent(reply -> {
            if (!reply.trim().isEmpty()) {
                String replyContent = original.hasAttachment() ?
                        "↩️ Reply to " + original.getSender() + "'s file: " + reply.trim() :
                        "↩️ Reply to " + original.getSender() + ": " + reply.trim();

                Message replyMsg = new Message(
                        "You",
                        replyContent,
                        Message.MessageType.USER
                );
                chatPanel.addMessage(replyMsg);
            }
        });
    }

    public void handleEdit(Message message) {
        if (message.hasAttachment()) return;

        TextInputDialog dialog = new TextInputDialog(message.getContent());
        dialog.setTitle("Edit Message");
        dialog.setHeaderText("Update your message:");
        dialog.setContentText("Message:");

        dialog.showAndWait().ifPresent(newContent -> {
            if (!newContent.trim().isEmpty()) {
                message.setContent(newContent.trim());
                refreshMessageNode(message);
            }
        });
    }

    public void handleDelete(Message message) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Message");
        confirm.setHeaderText("Do you want to delete this message?");

        String contentText = message.hasAttachment() ?
                "Attachment: " + message.getAttachment().getFileName() :
                "\"" + message.getContent() + "\"";
        confirm.setContentText(contentText);

        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                chatPanel.getMessageContainer().getChildren().removeIf(node -> {
                    if (node instanceof VBox messageBox) {
                        return messageBox.getUserData() == message;
                    }
                    return false;
                });
                chatPanel.getChatHeader().updateMessageCount(chatPanel.getMessageContainer());
            }
        });
    }

    public void handleDownload(AttachmentMessage attachment) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File");
        fileChooser.setInitialFileName(attachment.getFileName());

        Stage stage = (Stage) chatPanel.getComponent().getScene().getWindow();
        File saveFile = fileChooser.showSaveDialog(stage);

        if (saveFile != null) {
            try {
                File sourceFile = new File(attachment.getFilePath());
                Files.copy(sourceFile.toPath(), saveFile.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                showInfoAlert("Download Complete", "File saved to: " + saveFile.getAbsolutePath());
            } catch (IOException e) {
                showAlert("Download Error", "Failed to save file: " + e.getMessage());
            }
        }
    }

    public void handleOpenFile(AttachmentMessage attachment) {
        new Thread(() -> {
            try {
                File file = new File(attachment.getFilePath());
                if (file.exists()) {
                    Desktop.getDesktop().open(file);
                } else {
                    System.err.println("File không tồn tại: " + file.getAbsolutePath());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    public void showFullSizeImage(AttachmentMessage attachment) {
        File imageFile = new File(attachment.getFilePath());
        if (!imageFile.exists()) {
            showAlert("Error", "File does not exist.");
            return;
        }

        Image image = new Image(imageFile.toURI().toString());
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setFitWidth(1000); // Tùy chỉnh nếu muốn full screen

        StackPane root = new StackPane(imageView);
        root.setStyle("-fx-background-color: black;");

        Scene scene = new Scene(root, 1000, 800); // kích thước có thể thay đổi

        Stage imageStage = new Stage();
        imageStage.setTitle(attachment.getFileName());
        imageStage.setScene(scene);
        imageStage.initModality(Modality.APPLICATION_MODAL); // chặn thao tác với cửa sổ khác
        imageStage.show();

        // Đóng khi click vào nền
        root.setOnMouseClicked(e -> imageStage.close());
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void refreshMessageNode(Message message) {
        for (javafx.scene.Node node : chatPanel.getMessageContainer().getChildren()) {
            if (node instanceof VBox messageBox && messageBox.getUserData() == message) {
                HBox bubbleContainer = (HBox) messageBox.getChildren().get(0);
                VBox bubble = (VBox) bubbleContainer.getChildren().get(0);
                for (javafx.scene.Node bubbleChild : bubble.getChildren()) {
                    if (bubbleChild instanceof TextFlow) {
                        TextFlow contentFlow = messageRenderer.parseMessageToTextFlow(
                                messageRenderer.processMessageContent(message));
                        contentFlow.setMaxWidth(450);
                        contentFlow.setStyle(STYLE_CONTENT_LABEL);
                        int index = bubble.getChildren().indexOf(bubbleChild);
                        bubble.getChildren().set(index, contentFlow);
                        break;
                    }
                }
                break;
            }
        }
    }
}