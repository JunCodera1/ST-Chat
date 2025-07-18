package me.chatapp.stchat.view.factories;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import me.chatapp.stchat.model.User;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;
import java.util.function.Consumer;


public class ProfileViewFactory {

    public static VBox create(User currentUser, Runnable onBackClicked) {
        VBox mainContainer = new VBox();
        mainContainer.setStyle("-fx-background-color: #36393f;");

        VBox header = createHeader(onBackClicked);

        // Placeholder để gắn sau khi có window
        VBox contentPlaceholder = new VBox();
        VBox.setVgrow(contentPlaceholder, javafx.scene.layout.Priority.ALWAYS);
        mainContainer.getChildren().addAll(header, contentPlaceholder);

        mainContainer.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                Window window = newScene.getWindow();
                VBox content = createProfileContent(currentUser, window);

                mainContainer.getChildren().set(1, content); // Replace placeholder
                VBox.setVgrow(content, javafx.scene.layout.Priority.ALWAYS);
            }
        });

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

    private static VBox createProfileContent(User currentUser, Window window) {
        VBox content = new VBox();
        content.setStyle("-fx-background-color: #36393f;");
        content.setPadding(new Insets(0, 20, 20, 20));

        VBox profileSection = createProfileSection(currentUser, window, newAvatarUrl -> {
            currentUser.setAvatarUrl(newAvatarUrl);
            System.out.println("✅ Avatar URL updated to: " + newAvatarUrl);
        });

        VBox quoteSection = createQuoteSection();
        VBox contactSection = createContactSection(currentUser);
        VBox mediaSection = createMediaSection();
        VBox filesSection = createAttachedFilesSection();

        content.getChildren().addAll(profileSection, quoteSection, contactSection, mediaSection, filesSection);
        return content;
    }


    private static VBox createProfileSection(User currentUser, Window window, Consumer<String> onAvatarUpdated) {
        VBox section = new VBox();
        section.setAlignment(Pos.CENTER);
        section.setSpacing(15);
        section.setStyle("-fx-padding: 20 0 20 0;");

        // Load avatar image
        String avatarUrl = currentUser.getAvatarUrl();
        Image image;

        if (avatarUrl == null || avatarUrl.isBlank()) {
            image = new Image(Objects.requireNonNull(
                    ProfileViewFactory.class.getResource("/image/default_avatar.png")
            ).toExternalForm());
        } else {
            try {
                image = new Image(avatarUrl, 100, 100, false, true);
                if (image.isError()) {
                    throw new IllegalArgumentException("Avatar image failed to load.");
                }
            } catch (Exception e) {
                image = new Image(Objects.requireNonNull(
                        ProfileViewFactory.class.getResource("/image/default_avatar.png")
                ).toExternalForm());
            }
        }



        // Prepare ImageView
        ImageView avatarView = new ImageView(image);
        avatarView.setFitWidth(100);
        avatarView.setFitHeight(100);

        // Clip to circle
        Circle clip = new Circle(50, 50, 50);
        avatarView.setClip(clip);

        // Snapshot to get circular cropped avatar
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        WritableImage roundedAvatar = avatarView.snapshot(params, null);

        ImageView finalAvatarView = new ImageView(roundedAvatar);
        finalAvatarView.setFitWidth(100);
        finalAvatarView.setFitHeight(100);

        Button cameraButton = new Button();
        FontIcon cameraIcon = new FontIcon("fas-camera");
        cameraIcon.setIconSize(16);
        cameraButton.setGraphic(cameraIcon);
        cameraButton.setStyle("-fx-background-color: white; -fx-background-radius: 50%; -fx-padding: 6;");
        cameraButton.setPrefSize(24, 24);
        cameraButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose new avatar");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
            );

            File selectedFile = fileChooser.showOpenDialog(window);
            if (selectedFile != null) {
                uploadAvatarToServer(currentUser.getId(), selectedFile, newAvatarUrl -> {
                    Platform.runLater(() -> {
                        // Reload avatar
                        Image newImage = new Image(newAvatarUrl, 100, 100, false, true);
                        avatarView.setImage(newImage);

                        WritableImage newSnapshot = avatarView.snapshot(params, null);
                        finalAvatarView.setImage(newSnapshot);

                        // Update callback
                        onAvatarUpdated.accept(newAvatarUrl);
                    });
                });
            }
        });

        // Place avatar and camera icon in a StackPane
        StackPane avatarStack = new StackPane(finalAvatarView, cameraButton);
        StackPane.setAlignment(cameraButton, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(cameraButton, new Insets(0, 4, 4, 0));

        // Username label
        Label nameLabel = new Label(currentUser.getUsername());
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold;");

        // Role label
        Label titleLabel = new Label("Front end Developer");
        titleLabel.setStyle("-fx-text-fill: #b9bbbe; -fx-font-size: 14px;");

        section.getChildren().addAll(avatarStack, nameLabel, titleLabel);
        return section;
    }

    private static void uploadAvatarToServer(int userId, File file, Consumer<String> onSuccess) {
        new Thread(() -> {
            try {
                String boundary = "===" + System.currentTimeMillis() + "===";
                URL url = new URL("http://localhost:8081/api/users/" + userId + "/avatar");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                try (DataOutputStream request = new DataOutputStream(conn.getOutputStream())) {
                    request.writeBytes("--" + boundary + "\r\n");
                    request.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"\r\n");
                    request.writeBytes("Content-Type: " + Files.probeContentType(file.toPath()) + "\r\n\r\n");
                    Files.copy(file.toPath(), request);
                    request.writeBytes("\r\n--" + boundary + "--\r\n");
                    request.flush();
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                        String responseLine = in.readLine();

                        String fullUrl = responseLine;

                        System.out.println("✅ New avatar URL: " + fullUrl);

                        onSuccess.accept(fullUrl);
                    }
                } else {
                    System.err.println("❌ Upload failed with response code: " + responseCode);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
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