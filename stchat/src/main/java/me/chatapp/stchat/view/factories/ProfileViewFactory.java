package me.chatapp.stchat.view.factories;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import me.chatapp.stchat.model.User;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.*;

import java.util.Objects;


public class ProfileViewFactory {

    public static VBox create(User currentUser, Runnable onBackClicked) {
        VBox mainContainer = new VBox();
        mainContainer.setStyle("-fx-background-color: #36393f;");

        VBox header = createHeader(onBackClicked);

        VBox content = createProfileContent(currentUser);

        mainContainer.getChildren().addAll(header, content);
        VBox.setVgrow(content, javafx.scene.layout.Priority.ALWAYS);


        return mainContainer;
    }

    private static VBox createHeader(Runnable onBackClicked) {
        VBox header = new VBox();
        header.setStyle("-fx-background-color: linear-gradient(to bottom, #7c4dff, #5e35b1); -fx-min-height: 120;");
        header.setPadding(new Insets(15));

        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setSpacing(10);

        Button backButton = new Button();
        FontIcon backIcon = new FontIcon(MaterialDesignA.ARROW_LEFT);
        backIcon.setIconColor(Color.WHITE);
        backIcon.setIconSize(20);
        backButton.setGraphic(backIcon);
        backButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        backButton.setOnAction(e -> onBackClicked.run());

        Label title = new Label("My Profile");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        Button menuButton = new Button();
        FontIcon menuIcon = new FontIcon(MaterialDesignM.MENU);
        menuIcon.setIconColor(Color.WHITE);
        menuIcon.setIconSize(20);
        menuButton.setGraphic(menuIcon);
        menuButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        topBar.getChildren().addAll(backButton, title, spacer, menuButton);
        header.getChildren().add(topBar);

        return header;
    }

    private static VBox createProfileContent(User currentUser) {
        VBox content = new VBox();
        content.setStyle("-fx-background-color: #36393f;");
        content.setPadding(new Insets(0, 20, 20, 20));

        VBox profileSection = createProfileSection(currentUser);

        VBox quoteSection = createQuoteSection();

        VBox contactSection = createContactSection(currentUser);

        VBox mediaSection = createMediaSection();

        VBox filesSection = createAttachedFilesSection();

        content.getChildren().addAll(profileSection, quoteSection, contactSection, mediaSection, filesSection);

        return content;
    }

    private static VBox createProfileSection(User currentUser) {
        VBox section = new VBox();
        section.setAlignment(Pos.CENTER);
        section.setSpacing(15);
        section.setStyle("-fx-padding: 20 0 20 0;");

        // Profile image
        Circle profileImage = new Circle(50);
        profileImage.setFill(Color.LIGHTGRAY);
        profileImage.setStroke(Color.WHITE);
        profileImage.setStrokeWidth(3);

        if (currentUser != null && currentUser.getAvatarUrl() != null) {
            ImageView imageView = new ImageView(new Image(currentUser.getAvatarUrl()));
            imageView.setFitWidth(100);
            imageView.setFitHeight(100);
            imageView.setPreserveRatio(true);
            Circle clip = new Circle(50);
            imageView.setClip(clip);
        }

        Label nameLabel = new Label(currentUser != null ? currentUser.getUsername() : "Adam Zampa");
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold;");

        Label titleLabel = new Label("Front end Developer");
        titleLabel.setStyle("-fx-text-fill: #b9bbbe; -fx-font-size: 14px;");

        section.getChildren().addAll(profileImage, nameLabel, titleLabel);

        return section;
    }

    private static VBox createQuoteSection() {
        VBox section = new VBox();
        section.setSpacing(10);
        section.setPadding(new Insets(10, 0, 20, 0));

        Label quote = new Label("If several languages coalesce, the grammar of the resulting language is more simple.");
        quote.setStyle("-fx-text-fill: #b9bbbe; -fx-font-size: 14px; -fx-wrap-text: true;");
        quote.setWrapText(true);

        section.getChildren().add(quote);

        return section;
    }

    private static VBox createContactSection(User currentUser) {
        VBox section = new VBox();
        section.setSpacing(15);
        section.setPadding(new Insets(0, 0, 20, 0));

        HBox nameRow = createInfoRow(MaterialDesignA.ACCOUNT, currentUser != null ? currentUser.getUsername() : "Adam Zampa");
        HBox emailRow = createInfoRow(MaterialDesignE.EMAIL, "admin@themesbrand.com");
        HBox locationRow = createInfoRow(MaterialDesignL.LOCATION_ENTER, "California, USA");


        section.getChildren().addAll(nameRow, emailRow, locationRow);

        return section;
    }


    private static HBox createInfoRow(Ikon ikon, String text) {
        HBox row = new HBox();
        row.setSpacing(15);
        row.setAlignment(Pos.CENTER_LEFT);

        FontIcon icon = new FontIcon(ikon);
        icon.setIconColor(Color.valueOf("#7c4dff"));
        icon.setIconSize(18);

        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #b9bbbe; -fx-font-size: 14px;");

        row.getChildren().addAll(icon, label);
        return row;
    }



    private static VBox createMediaSection() {
        VBox section = new VBox();
        section.setSpacing(15);
        section.setPadding(new Insets(0, 0, 20, 0));

        // Header
        HBox headerRow = new HBox();
        headerRow.setAlignment(Pos.CENTER_LEFT);
        headerRow.setSpacing(10);

        Label mediaTitle = new Label("MEDIA");
        mediaTitle.setStyle("-fx-text-fill: #b9bbbe; -fx-font-size: 12px; -fx-font-weight: bold;");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Button showAllButton = new Button("Show all");
        showAllButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #7c4dff; -fx-border-color: transparent; -fx-font-size: 12px;");

        headerRow.getChildren().addAll(mediaTitle, spacer, showAllButton);

        // Media grid
        HBox mediaGrid = new HBox();
        mediaGrid.setSpacing(10);

        // Sample media items
        for (int i = 0; i < 3; i++) {
            VBox mediaItem = new VBox();
            mediaItem.setStyle("-fx-background-color: #40444b; -fx-pref-width: 80; -fx-pref-height: 80; -fx-background-radius: 8;");
            mediaItem.setAlignment(Pos.CENTER);

            if (i == 2) {
                Label moreLabel = new Label("+ 15");
                moreLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
                mediaItem.getChildren().add(moreLabel);
            } else {
                FontIcon imageIcon = new FontIcon(MaterialDesignI.IMAGE);
                imageIcon.setIconColor(Color.valueOf("#7c4dff"));
                imageIcon.setIconSize(30);
                mediaItem.getChildren().add(imageIcon);
            }

            mediaGrid.getChildren().add(mediaItem);
        }

        section.getChildren().addAll(headerRow, mediaGrid);

        return section;
    }

    private static VBox createAttachedFilesSection() {
        VBox section = new VBox();
        section.setSpacing(15);
        section.setPadding(new Insets(0, 0, 20, 0));

        Label filesTitle = new Label("ATTACHED FILES");
        filesTitle.setStyle("-fx-text-fill: #b9bbbe; -fx-font-size: 12px; -fx-font-weight: bold;");

        VBox filesList = new VBox();
        filesList.setSpacing(10);

        String[] files = {
                "design-phase-1.zip|12.5 MB",
                "image-1.jpg|4.2 MB",
                "image-2.jpg|3.1 MB",
                "Landing-A.zip|6.7 MB"
        };

        for (String fileInfo : files) {
            String[] parts = fileInfo.split("\\|");
            HBox fileItem = createFileItem(parts[0], parts[1]);
            filesList.getChildren().add(fileItem);
        }

        section.getChildren().addAll(filesTitle, filesList);

        return section;
    }

    private static HBox createFileItem(String fileName, String fileSize) {
        HBox fileItem = new HBox();
        fileItem.setSpacing(15);
        fileItem.setAlignment(Pos.CENTER_LEFT);
        fileItem.setPadding(new Insets(10));
        fileItem.setStyle("-fx-background-color: #40444b; -fx-background-radius: 8;");

        // File icon
        FontIcon fileIcon;
        if (fileName.endsWith(".zip")) {
            fileIcon = new FontIcon(MaterialDesignF.FOLDER_ZIP);
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".png")) {
            fileIcon = new FontIcon(MaterialDesignI.IMAGE);
        } else {
            fileIcon = new FontIcon(MaterialDesignF.FILE);
        }
        fileIcon.setIconColor(Color.valueOf("#7c4dff"));
        fileIcon.setIconSize(20);

        // File info
        VBox fileInfo = new VBox();
        fileInfo.setSpacing(2);

        Label nameLabel = new Label(fileName);
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        Label sizeLabel = new Label(fileSize);
        sizeLabel.setStyle("-fx-text-fill: #b9bbbe; -fx-font-size: 12px;");

        fileInfo.getChildren().addAll(nameLabel, sizeLabel);

        // Spacer
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        // Download button
        Button downloadButton = new Button();
        FontIcon downloadIcon = new FontIcon(MaterialDesignC.CLOUD_DOWNLOAD);
        downloadIcon.setIconColor(Color.valueOf("#b9bbbe"));
        downloadIcon.setIconSize(18);
        downloadButton.setGraphic(downloadIcon);
        downloadButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        // Menu button
        Button menuButton = new Button();
        FontIcon menuIcon = new FontIcon(MaterialDesignM.MENU);
        menuIcon.setIconColor(Color.valueOf("#b9bbbe"));
        menuIcon.setIconSize(18);
        menuButton.setGraphic(menuIcon);
        menuButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        fileItem.getChildren().addAll(fileIcon, fileInfo, spacer, downloadButton, menuButton);

        return fileItem;
    }
}