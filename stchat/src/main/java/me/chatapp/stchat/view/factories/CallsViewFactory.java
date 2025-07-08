package me.chatapp.stchat.view.factories;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;


public class CallsViewFactory {

    public static VBox create(Runnable onBackClicked) {
        VBox content = new VBox();
        content.setSpacing(0);
        content.setPadding(new Insets(15));
        content.setStyle("-fx-background-color: #1a1d21;");

        // Header
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 20, 0));

        Button backButton = new Button("←");
        backButton.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #acaeb4;" +
                        "-fx-font-size: 18px;" +
                        "-fx-border-color: transparent;" +
                        "-fx-cursor: hand;"
        );
        backButton.setOnAction(e -> onBackClicked.run());

        Label titleLabel = new Label("Calls");
        titleLabel.setStyle("-fx-text-fill: #333333; -fx-font-size: 24px; -fx-font-weight: bold;");

        header.getChildren().addAll(backButton, titleLabel);

        // Calls list
        VBox callsList = new VBox(0);

        // Create call items
        callsList.getChildren().addAll(
                createCallItem("BB", "Burgess Burt", "5 May, 2016, 06:16...", "5:37", false, true),
                createCallItem("BS", "Bryant Shaffer", "17 May, 2014, 10:22...", "5:24", true, false),
                createCallItem("CS", "Curtis Spears", "27 Nov, 2020, ...", "2:43", false, true),
                createCallItem("MG", "Mara Gilliam", "29 Aug, 2019, ...", "2:41", false, true),
                createCallItem("DS", "Duncan Snyder", "23 Dec, 2018, ...", "1:57", true, false),
                createCallItem("EA", "Eunice Atkinson", "28 Oct, 2019, 09:42...", "1:24", false, true),
                createCallItem("MW", "Meyer Walton", "19 Sep, 2019, 07:33...", "1:22", true, false),
                createCallItem("DD", "Debra Davis", "7 Oct, 2019, 0...", "4:52", false, true),
                createCallItem("HH", "Hansen Haynes", "16 Jan, 2014, 0...", "4:24", true, false),
                createCallItem("FW", "Freida Waters", "12 Mar, 2014, 06:47...", "4:13", false, true),
                createCallItem("RR", "Rosie Russo", "11 Sep, 2021, ...", "2:12", true, false),
                createCallItem("DL", "Day Lawrence", "13 Dec, 2016, ...", "5:33", false, true),
                createCallItem("TD", "Tamra Dudley", "22 Apr, 2015, 0...", "2:55", true, false),
                createCallItem("MW", "Marissa Weiss", "22 Jul, 2015, 1...", "2:52", false, true),
                createCallItem("HR", "Herrera Randall", "14 Feb, 2015, 10:54...", "3:13", true, false),
                createCallItem("MS", "Mindy Salas", "", "3:44", false, true)
        );

        content.getChildren().addAll(header, callsList);
        return content;
    }

    private static HBox createCallItem(String initials, String name, String dateTime, String duration, boolean isIncoming, boolean isVideoCall) {
        HBox item = new HBox(12);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(12, 0, 12, 0));
        item.setStyle("-fx-cursor: hand;");

        // Hover effect
        item.setOnMouseEntered(e -> item.setStyle("-fx-background-color: #78797c; -fx-cursor: hand;"));
        item.setOnMouseExited(e -> item.setStyle("-fx-background-color: transparent; -fx-cursor: hand;"));

        // Avatar circle with initials
        Circle avatar = new Circle(20);
        avatar.setFill(getAvatarColor(initials));

        Label initialsLabel = new Label(initials);
        initialsLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold;");

        // Stack pane equivalent using HBox positioning
        HBox avatarContainer = new HBox();
        avatarContainer.setAlignment(Pos.CENTER);
        avatarContainer.setPrefSize(40, 40);
        avatarContainer.getChildren().addAll(avatar, initialsLabel);
        // Position initials on top of circle
        initialsLabel.setLayoutX(-initials.length() * 3);

        // Name and date container
        VBox nameContainer = new VBox(2);
        nameContainer.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-text-fill: #acaeb4; -fx-font-size: 16px; -fx-font-weight: bold;");

        HBox dateRow = new HBox(5);
        dateRow.setAlignment(Pos.CENTER_LEFT);

        // Call direction icon
        String directionIcon = isIncoming ? "↙" : "↗";
        Label directionLabel = new Label(directionIcon);
        directionLabel.setStyle("-fx-text-fill: " + (isIncoming ? "#4CAF50" : "#FF5722") + "; -fx-font-size: 12px;");

        Label dateLabel = new Label(dateTime);
        dateLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 12px;");

        if (!dateTime.isEmpty()) {
            dateRow.getChildren().addAll(directionLabel, dateLabel);
        }

        nameContainer.getChildren().addAll(nameLabel, dateRow);

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Duration and call type
        VBox rightContainer = new VBox(2);
        rightContainer.setAlignment(Pos.CENTER_RIGHT);

        Label durationLabel = new Label(duration);
        durationLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 12px;");

        // Call type icon (video or audio)
        String callIcon = isVideoCall ? "📹" : "📞";
        Label callTypeLabel = new Label(callIcon);
        callTypeLabel.setStyle("-fx-font-size: 16px;");

        rightContainer.getChildren().addAll(durationLabel, callTypeLabel);

        item.getChildren().addAll(avatarContainer, nameContainer, spacer, rightContainer);

        return item;
    }

    private static Color getAvatarColor(String initials) {
        // Generate colors based on initials
        switch (initials.charAt(0)) {
            case 'B': return Color.web("#4CAF50"); // Green
            case 'C': return Color.web("#2196F3"); // Blue
            case 'D': return Color.web("#9C27B0"); // Purple
            case 'E': return Color.web("#00BCD4"); // Cyan
            case 'F': return Color.web("#333333"); // Dark gray
            case 'H': return Color.web("#FF9800"); // Orange
            case 'M': return Color.web("#607D8B"); // Blue gray
            case 'R': return Color.web("#4CAF50"); // Green
            case 'T': return Color.web("#673AB7"); // Deep purple
            default: return Color.web("#757575"); // Gray
        }
    }

    private static Region createSeparator() {
        Region separator = new Region();
        separator.setPrefHeight(1);
        separator.setStyle("-fx-background-color: #e0e0e0;");
        VBox.setMargin(separator, new Insets(5, 0, 5, 0));
        return separator;
    }
}