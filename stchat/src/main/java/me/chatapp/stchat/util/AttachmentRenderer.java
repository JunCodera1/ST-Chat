package me.chatapp.stchat.util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import me.chatapp.stchat.model.AttachmentMessage;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.FileInputStream;

public class AttachmentRenderer {
    public VBox createAttachmentBox(AttachmentMessage attachment, MessageActions messageActions) {
        VBox attachmentBox = new VBox();
        attachmentBox.setSpacing(8);
        attachmentBox.setPadding(new Insets(8));
        attachmentBox.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8px; -fx-border-color: #dee2e6; -fx-border-radius: 8px;");

        HBox fileInfoBox = new HBox();
        fileInfoBox.setSpacing(8);
        fileInfoBox.setAlignment(Pos.CENTER_LEFT);

        Label fileIcon = new Label(attachment.getFileIcon());
        fileIcon.setStyle("-fx-font-size: 24px;");

        VBox fileDetails = new VBox();
        fileDetails.setSpacing(2);

        Label fileName = new Label(attachment.getFileName());
        fileName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label fileInfo = new Label(attachment.getFileType() + " • " + attachment.getFormattedSize());
        fileInfo.setStyle("-fx-text-fill: #6c757d; -fx-font-size: 12px;");

        fileDetails.getChildren().addAll(fileName, fileInfo);
        fileInfoBox.getChildren().addAll(fileIcon, fileDetails);
        attachmentBox.getChildren().add(fileInfoBox);

        if (attachment.isImage()) {
            ImageView imagePreview = createImagePreview(attachment);
            if (imagePreview != null) {
                attachmentBox.getChildren().add(imagePreview);
            }
        }

        HBox actionButtons = new HBox();
        actionButtons.setSpacing(8);
        actionButtons.setAlignment(Pos.CENTER_LEFT);

        Button downloadBtn = new Button("Download");
        downloadBtn.setGraphic(new FontIcon(FontAwesome.DOWNLOAD));
        downloadBtn.setOnAction(e -> messageActions.handleDownload(attachment));

        Button openBtn = new Button("Open");
        openBtn.setGraphic(new FontIcon(FontAwesome.EXTERNAL_LINK));
        openBtn.setOnAction(e -> messageActions.handleOpenFile(attachment));

        actionButtons.getChildren().addAll(downloadBtn, openBtn);
        attachmentBox.getChildren().add(actionButtons);

        return attachmentBox;
    }

    private ImageView createImagePreview(AttachmentMessage attachment) {
        try {
            File imageFile = new File(attachment.getFilePath());
            if (imageFile.exists()) {
                FileInputStream fis = new FileInputStream(imageFile);
                Image image = new Image(fis);

                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(300);
                imageView.setFitHeight(200);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                imageView.setStyle("-fx-background-radius: 8px;");

                imageView.setOnMouseClicked(e -> new MessageActions(null, null).showFullSizeImage(attachment));
                fis.close();
                return imageView;
            }
        } catch (Exception e) {
            System.err.println("Error creating image preview: " + e.getMessage());
        }
        return null;
    }
}