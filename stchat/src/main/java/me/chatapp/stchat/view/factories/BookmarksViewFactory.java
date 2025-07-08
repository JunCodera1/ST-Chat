package me.chatapp.stchat.view.factories;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;

public class BookmarksViewFactory {

    public static VBox create(Runnable onBackClicked) {
        VBox content = new VBox();
        content.setSpacing(10);
        content.setPadding(new Insets(15));
        content.setStyle("-fx-background-color: #1a1d21;");

        // Header
        HBox header = createHeader(onBackClicked);

        // Bookmarks list container
        VBox bookmarksContainer = createBookmarksContainer();

        content.getChildren().addAll(header, createSeparator(), bookmarksContainer);
        return content;
    }

    private static HBox createHeader(Runnable onBackClicked) {
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Button backButton = new Button();
        FontIcon backIcon = new FontIcon(Feather.ARROW_LEFT);
        backIcon.setIconColor(javafx.scene.paint.Color.WHITE);
        backIcon.setIconSize(16);
        backButton.setGraphic(backIcon);

        backButton.setStyle(
                "-fx-background-color: #2f3136;" +
                        "-fx-border-radius: 50%;" +
                        "-fx-background-radius: 50%;" +
                        "-fx-cursor: hand;" +
                        "-fx-min-width: 35px;" +
                        "-fx-min-height: 35px;"
        );
        backButton.setOnAction(e -> onBackClicked.run());

        // Hover effect for back button
        backButton.setOnMouseEntered(e -> backButton.setStyle(
                "-fx-background-color: #40444b;" +
                        "-fx-border-radius: 50%;" +
                        "-fx-background-radius: 50%;" +
                        "-fx-cursor: hand;" +
                        "-fx-min-width: 35px;" +
                        "-fx-min-height: 35px;"
        ));
        backButton.setOnMouseExited(e -> backButton.setStyle(
                "-fx-background-color: #2f3136;" +
                        "-fx-border-radius: 50%;" +
                        "-fx-background-radius: 50%;" +
                        "-fx-cursor: hand;" +
                        "-fx-min-width: 35px;" +
                        "-fx-min-height: 35px;"
        ));

        Label titleLabel = new Label("Bookmarks");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        header.getChildren().addAll(backButton, titleLabel);
        return header;
    }

    private static VBox createBookmarksContainer() {
        VBox container = new VBox(8);
        container.setPadding(new Insets(10, 0, 0, 0));

        // Create scroll pane for bookmarks
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        VBox bookmarksList = new VBox(12);
        bookmarksList.setPadding(new Insets(5));

        // Sample bookmarks data with Ikonli icons
        BookmarkItem[] bookmarks = {
                new BookmarkItem(Material2AL.DESCRIPTION, "design-phase-1-approv...", "12.5 MB", "#4CAF50"),
                new BookmarkItem(Material2AL.BRUSH, "Bg Pattern", "https://bgpattern.com/", "#FF9800"),
                new BookmarkItem(Material2AL.IMAGE, "Image-001.jpg", "4.2 MB", "#2196F3"),
                new BookmarkItem(Material2AL.COLLECTIONS, "Images", "https://images123.com/", "#9C27B0"),
                new BookmarkItem(Material2AL.GRADIENT, "Bg Gradient", "https://bggradient.com/", "#E91E63"),
                new BookmarkItem(Material2AL.IMAGE, "Image-012.jpg", "3.1 MB", "#00BCD4"),
                new BookmarkItem(Material2AL.ANALYTICS, "analytics dashboard.zip", "6.7 MB", "#795548"),
                new BookmarkItem(Material2AL.IMAGE, "Image-031.jpg", "4.2 MB", "#607D8B"),
                new BookmarkItem(Material2AL.ARTICLE, "Changelog.txt", "6.7 MB", "#8BC34A"),
                new BookmarkItem(Material2MZ.WIDGETS, "Widgets.zip", "6.7 MB", "#FFC107"),
                new BookmarkItem(Material2AL.IMAGE, "logo-light.png", "4.2 MB", "#3F51B5"),
                new BookmarkItem(Material2AL.IMAGE, "Image-2.jpg", "3.1 MB", "#FF5722"),
                new BookmarkItem(Material2AL.ATTACH_FILE, "Landing-A.zip", "6.7 MB", "#009688")
        };

        // Add bookmarks to the list
        for (BookmarkItem bookmark : bookmarks) {
            bookmarksList.getChildren().add(createBookmarkCard(bookmark));
        }

        scrollPane.setContent(bookmarksList);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        container.getChildren().add(scrollPane);
        return container;
    }

    private static HBox createBookmarkCard(BookmarkItem bookmark) {
        HBox card = new HBox(12);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(12, 15, 12, 15));
        card.setStyle(
                "-fx-background-color: #2f3136;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-cursor: hand;"
        );

        // Icon container
        HBox iconContainer = new HBox();
        iconContainer.setAlignment(Pos.CENTER);
        iconContainer.setPrefWidth(40);
        iconContainer.setPrefHeight(40);
        iconContainer.setStyle(
                "-fx-background-color: " + bookmark.color + ";" +
                        "-fx-background-radius: 6px;"
        );

        FontIcon icon = new FontIcon((Ikon) bookmark.icon);
        icon.setIconColor(javafx.scene.paint.Color.WHITE);
        icon.setIconSize(18);
        iconContainer.getChildren().add(icon);

        // Content container
        VBox contentBox = new VBox(2);
        contentBox.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(bookmark.title);
        titleLabel.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;"
        );

        Label subtitleLabel = new Label(bookmark.subtitle);
        subtitleLabel.setStyle(
                "-fx-text-fill: #72767d;" +
                        "-fx-font-size: 12px;"
        );

        contentBox.getChildren().addAll(titleLabel, subtitleLabel);

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // More options button
        Button moreButton = new Button();
        FontIcon moreIcon = new FontIcon(Feather.MORE_HORIZONTAL);
        moreIcon.setIconColor(javafx.scene.paint.Color.web("#72767d"));
        moreIcon.setIconSize(16);
        moreButton.setGraphic(moreIcon);
        moreButton.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-cursor: hand;" +
                        "-fx-min-width: 30px;" +
                        "-fx-min-height: 30px;"
        );

        // Hover effects
        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: #40444b;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-cursor: hand;"
        ));
        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color: #2f3136;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-cursor: hand;"
        ));

        moreButton.setOnMouseEntered(e -> {
            moreButton.setStyle(
                    "-fx-background-color: #5865f2;" +
                            "-fx-background-radius: 4px;" +
                            "-fx-cursor: hand;" +
                            "-fx-min-width: 30px;" +
                            "-fx-min-height: 30px;"
            );
            moreIcon.setIconColor(javafx.scene.paint.Color.WHITE);
        });
        moreButton.setOnMouseExited(e -> {
            moreButton.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-cursor: hand;" +
                            "-fx-min-width: 30px;" +
                            "-fx-min-height: 30px;"
            );
            moreIcon.setIconColor(javafx.scene.paint.Color.web("#72767d"));
        });

        card.getChildren().addAll(iconContainer, contentBox, spacer, moreButton);
        return card;
    }

    private static Region createSeparator() {
        Region separator = new Region();
        separator.setPrefHeight(1);
        separator.setStyle("-fx-background-color: #40444b;");
        VBox.setMargin(separator, new Insets(10, 0, 10, 0));
        return separator;
    }

    // Data class for bookmark items
    private static class BookmarkItem {
        private final Object icon;
        private final String title;
        private final String subtitle;
        private final String color;

        public BookmarkItem(Object icon, String title, String subtitle, String color) {
            this.icon = icon;
            this.title = title;
            this.subtitle = subtitle;
            this.color = color;
        }
    }
}