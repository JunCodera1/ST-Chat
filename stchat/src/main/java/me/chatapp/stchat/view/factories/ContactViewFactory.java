package me.chatapp.stchat.view.factories;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.material2.Material2AL;

public class ContactViewFactory {

    public static VBox create(Runnable onBackClicked) {
        VBox content = new VBox();
        content.setSpacing(10);
        content.setPadding(new Insets(15));
        content.setStyle("-fx-background-color: #1a1d21;");

        HBox header = createHeader(onBackClicked);

        HBox searchContainer = createSearchBar();

        VBox contactsContainer = createContactsContainer();

        content.getChildren().addAll(header, searchContainer, contactsContainer);
        return content;
    }

    private static HBox createHeader(Runnable onBackClicked) {
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Button backButton = new Button();
        FontIcon backIcon = new FontIcon(Feather.ARROW_LEFT);
        backIcon.setIconColor(Color.WHITE);
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

        Label titleLabel = new Label("Contacts");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addButton = new Button();
        FontIcon addIcon = new FontIcon(Feather.PLUS);
        addIcon.setIconColor(Color.WHITE);
        addIcon.setIconSize(16);
        addButton.setGraphic(addIcon);
        addButton.setStyle(
                "-fx-background-color: #5865f2;" +
                        "-fx-border-radius: 50%;" +
                        "-fx-background-radius: 50%;" +
                        "-fx-cursor: hand;" +
                        "-fx-min-width: 35px;" +
                        "-fx-min-height: 35px;"
        );

        addButton.setOnMouseEntered(e -> addButton.setStyle(
                "-fx-background-color: #4752c4;" +
                        "-fx-border-radius: 50%;" +
                        "-fx-background-radius: 50%;" +
                        "-fx-cursor: hand;" +
                        "-fx-min-width: 35px;" +
                        "-fx-min-height: 35px;"
        ));
        addButton.setOnMouseExited(e -> addButton.setStyle(
                "-fx-background-color: #5865f2;" +
                        "-fx-border-radius: 50%;" +
                        "-fx-background-radius: 50%;" +
                        "-fx-cursor: hand;" +
                        "-fx-min-width: 35px;" +
                        "-fx-min-height: 35px;"
        ));

        header.getChildren().addAll(backButton, titleLabel, spacer, addButton);
        return header;
    }

    private static HBox createSearchBar() {
        HBox searchContainer = new HBox();
        searchContainer.setAlignment(Pos.CENTER_LEFT);
        searchContainer.setStyle(
                "-fx-background-color: #2f3136;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-padding: 8px 12px;"
        );

        TextField searchField = new TextField();
        searchField.setPromptText("Search Contacts..");
        searchField.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-prompt-text-fill: #72767d;" +
                        "-fx-border-color: transparent;" +
                        "-fx-focus-color: transparent;" +
                        "-fx-faint-focus-color: transparent;"
        );
        HBox.setHgrow(searchField, Priority.ALWAYS);

        FontIcon searchIcon = new FontIcon(Feather.SEARCH);
        searchIcon.setIconColor(Color.web("#72767d"));
        searchIcon.setIconSize(16);

        searchContainer.getChildren().addAll(searchField, searchIcon);
        return searchContainer;
    }

    private static VBox createContactsContainer() {
        VBox container = new VBox(8);
        container.setPadding(new Insets(10, 0, 0, 0));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        VBox contactsList = new VBox(5);
        contactsList.setPadding(new Insets(5));

        ContactItem[] contacts = {
                new ContactItem("A", "AL", "Alvarez Luna", "#7289DA"),
                new ContactItem("C", "CS", "Carla Serrano", "#43B581"),
                new ContactItem("D", "DV", "Dean Vargas", "#FAA61A"),
                new ContactItem("D", "DR", "Donaldson Riddle", "#F04747"),
                new ContactItem("D", "DW", "Daniels Webster", "#E91E63"),
                new ContactItem("E", "ES", "Earnestine Sears", "#99AAB5"),
                new ContactItem("F", "FB", "Faulkner Benjamin", "#9C27B0"),
                new ContactItem("H", "HJ", "Heath Jarvis", "#7289DA"),
                new ContactItem("H", "HM", "Hendrix Martin", "#8BC34A"),
                new ContactItem("J", "JR", "Jennifer Ramirez", "#FF9800"),
                new ContactItem("K", "KW", "Katrina Winters", "#795548"),
                new ContactItem("K", "KC", "Kitty Cannon", "#4CAF50")
        };

        String currentSection = "";
        for (ContactItem contact : contacts) {
            if (!contact.section.equals(currentSection)) {
                if (!currentSection.isEmpty()) {
                    contactsList.getChildren().add(createSeparator());
                }
                Label sectionLabel = new Label(contact.section);
                sectionLabel.setStyle(
                        "-fx-text-fill: #72767d;" +
                                "-fx-font-size: 12px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-padding: 10px 0 5px 0;"
                );
                contactsList.getChildren().add(sectionLabel);
                currentSection = contact.section;
            }

            contactsList.getChildren().add(createContactCard(contact));
        }

        scrollPane.setContent(contactsList);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        container.getChildren().add(scrollPane);
        return container;
    }

    private static HBox createContactCard(ContactItem contact) {
        HBox card = new HBox(12);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(8, 12, 8, 12));
        card.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-cursor: hand;"
        );

        // Avatar
        Label avatarLabel = new Label(contact.initials);
        avatarLabel.setStyle(
                "-fx-background-color: " + contact.color + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 50%;" +
                        "-fx-alignment: center;" +
                        "-fx-min-width: 40px;" +
                        "-fx-min-height: 40px;" +
                        "-fx-max-width: 40px;" +
                        "-fx-max-height: 40px;"
        );

        // Name
        Label nameLabel = new Label(contact.name);
        nameLabel.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: normal;"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button moreButton = new Button();
        FontIcon moreIcon = new FontIcon(Feather.MORE_VERTICAL);
        moreIcon.setIconColor(Color.web("#72767d"));
        moreIcon.setIconSize(16);
        moreButton.setGraphic(moreIcon);
        moreButton.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-cursor: hand;" +
                        "-fx-min-width: 30px;" +
                        "-fx-min-height: 30px;"
        );

        // Hover effects
        card.setOnMouseEntered(e -> {
            card.setStyle(
                    "-fx-background-color: #36393f;" +
                            "-fx-background-radius: 8px;" +
                            "-fx-cursor: hand;"
            );
        });
        card.setOnMouseExited(e -> {
            card.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-background-radius: 8px;" +
                            "-fx-cursor: hand;"
            );
        });

        moreButton.setOnMouseEntered(e -> {
            moreButton.setStyle(
                    "-fx-background-color: #5865f2;" +
                            "-fx-background-radius: 4px;" +
                            "-fx-cursor: hand;" +
                            "-fx-min-width: 30px;" +
                            "-fx-min-height: 30px;"
            );
            moreIcon.setIconColor(Color.WHITE);
        });
        moreButton.setOnMouseExited(e -> {
            moreButton.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-cursor: hand;" +
                            "-fx-min-width: 30px;" +
                            "-fx-min-height: 30px;"
            );
            moreIcon.setIconColor(Color.web("#72767d"));
        });

        card.getChildren().addAll(avatarLabel, nameLabel, spacer, moreButton);
        return card;
    }

    private static Region createSeparator() {
        Region separator = new Region();
        separator.setPrefHeight(1);
        separator.setStyle("-fx-background-color: #40444b;");
        VBox.setMargin(separator, new Insets(5, 0, 5, 0));
        return separator;
    }

    // Data class for contact items
    private static class ContactItem {
        private final String section;
        private final String initials;
        private final String name;
        private final String color;

        public ContactItem(String section, String initials, String name, String color) {
            this.section = section;
            this.initials = initials;
            this.name = name;
            this.color = color;
        }
    }
}