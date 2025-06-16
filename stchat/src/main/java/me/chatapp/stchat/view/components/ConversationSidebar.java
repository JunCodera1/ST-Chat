package me.chatapp.stchat.view.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.function.Consumer;

public class ConversationSidebar {
    private final VBox sidebarContainer;
    private final TextField searchField;
    private final ListView<ConversationItem> conversationList;
    private Consumer<String> onConversationSelected;

    public ConversationSidebar() {
        sidebarContainer = new VBox();
        sidebarContainer.setSpacing(0);
        sidebarContainer.setPadding(new Insets(10));

        // Search bar
        searchField = new TextField();
        searchField.setPromptText("🔍 Search conversations...");
        searchField.setStyle("""
            -fx-background-color: #e4e6ea;
            -fx-background-radius: 20;
            -fx-border-radius: 20;
            -fx-padding: 8 15;
            -fx-font-size: 14px;
            -fx-border-color: transparent;
            """);
        searchField.setPrefHeight(36);

        // Add some spacing after search
        Region searchSpacer = new Region();
        searchSpacer.setPrefHeight(15);

        // Conversations list
        conversationList = new ListView<>();
        conversationList.setCellFactory(listView -> new ConversationCell());
        conversationList.setStyle("""
            -fx-background-color: transparent;
            -fx-border-color: transparent;
            -fx-focus-color: transparent;
            -fx-faint-focus-color: transparent;
            """);

        // Handle conversation selection
        conversationList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && onConversationSelected != null) {
                onConversationSelected.accept(newVal.getName());
            }
        });

        VBox.setVgrow(conversationList, Priority.ALWAYS);

        sidebarContainer.getChildren().addAll(searchField, searchSpacer, conversationList);
    }

    public VBox getComponent() {
        return sidebarContainer;
    }

    public void addConversation(String name, String lastMessage, String time, boolean hasUnread) {
        ConversationItem item = new ConversationItem(name, lastMessage, time, hasUnread);
        conversationList.getItems().add(item);
    }

    public void setOnConversationSelected(Consumer<String> callback) {
        this.onConversationSelected = callback;
    }

    public void clearConversations() {
        conversationList.getItems().clear();
    }

    // Conversation item data class
    public static class ConversationItem {
        private final String name;
        private final String lastMessage;
        private final String time;
        private final boolean hasUnread;

        public ConversationItem(String name, String lastMessage, String time, boolean hasUnread) {
            this.name = name;
            this.lastMessage = lastMessage;
            this.time = time;
            this.hasUnread = hasUnread;
        }

        public String getName() { return name; }
        public String getLastMessage() { return lastMessage; }
        public String getTime() { return time; }
        public boolean hasUnread() { return hasUnread; }
    }

    // Custom cell for conversation items
    private static class ConversationCell extends ListCell<ConversationItem> {
        @Override
        protected void updateItem(ConversationItem item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setGraphic(null);
                setText(null);
                setStyle("-fx-background-color: transparent;");
            } else {
                HBox cellContainer = createConversationCell(item);
                setGraphic(cellContainer);
                setText(null);

                // Style the cell
                setStyle("""
                    -fx-background-color: transparent;
                    -fx-padding: 8 12;
                    -fx-border-color: transparent;
                    """);

                // Hover effect
                setOnMouseEntered(e -> setStyle("""
                    -fx-background-color: #e4e6ea;
                    -fx-background-radius: 8;
                    -fx-padding: 8 12;
                    """));

                setOnMouseExited(e -> {
                    if (!isSelected()) {
                        setStyle("""
                            -fx-background-color: transparent;
                            -fx-padding: 8 12;
                            """);
                    }
                });

                // Selection style
                selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                    if (isSelected) {
                        setStyle("""
                            -fx-background-color: #1877f2;
                            -fx-background-radius: 8;
                            -fx-padding: 8 12;
                            """);
                        // Update text colors for selected state
                        updateTextColorsForSelection(cellContainer, true);
                    } else {
                        setStyle("""
                            -fx-background-color: transparent;
                            -fx-padding: 8 12;
                            """);
                        updateTextColorsForSelection(cellContainer, false);
                    }
                });
            }
        }

        private HBox createConversationCell(ConversationItem item) {
            HBox cellContainer = new HBox(12);
            cellContainer.setAlignment(Pos.CENTER_LEFT);
            cellContainer.setPrefHeight(72);

            // Avatar circle
            Circle avatar = new Circle(24);
            avatar.setFill(Color.web("#1877f2"));

            // Avatar with initials
            StackPane avatarPane = new StackPane();
            avatarPane.getChildren().add(avatar);

            Label initials = new Label(getInitials(item.getName()));
            initials.setTextFill(Color.WHITE);
            initials.setFont(Font.font("System", FontWeight.BOLD, 14));
            avatarPane.getChildren().add(initials);

            // Content area
            VBox contentArea = new VBox(4);
            HBox.setHgrow(contentArea, Priority.ALWAYS);

            // Top row: name and time
            HBox topRow = new HBox();
            topRow.setAlignment(Pos.CENTER_LEFT);

            Label nameLabel = new Label(item.getName());
            nameLabel.setFont(Font.font("System", FontWeight.BOLD, 15));
            nameLabel.setTextFill(Color.web("#050505"));

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label timeLabel = new Label(item.getTime());
            timeLabel.setFont(Font.font("System", 13));
            timeLabel.setTextFill(Color.web("#65676b"));

            topRow.getChildren().addAll(nameLabel, spacer, timeLabel);

            // Bottom row: last message and unread indicator
            HBox bottomRow = new HBox();
            bottomRow.setAlignment(Pos.CENTER_LEFT);

            Label messageLabel = new Label(item.getLastMessage());
            messageLabel.setFont(Font.font("System", 14));
            messageLabel.setTextFill(Color.web("#65676b"));
            messageLabel.setMaxWidth(200);
            // Truncate long messages
            if (item.getLastMessage().length() > 30) {
                messageLabel.setText(item.getLastMessage().substring(0, 27) + "...");
            }

            Region messageSpacer = new Region();
            HBox.setHgrow(messageSpacer, Priority.ALWAYS);

            bottomRow.getChildren().add(messageLabel);

            // Unread indicator
            if (item.hasUnread()) {
                Circle unreadDot = new Circle(6);
                unreadDot.setFill(Color.web("#1877f2"));
                bottomRow.getChildren().addAll(messageSpacer, unreadDot);
            }

            contentArea.getChildren().addAll(topRow, bottomRow);
            cellContainer.getChildren().addAll(avatarPane, contentArea);

            return cellContainer;
        }

        private void updateTextColorsForSelection(HBox cellContainer, boolean selected) {
            VBox contentArea = (VBox) cellContainer.getChildren().get(1);
            HBox topRow = (HBox) contentArea.getChildren().get(0);
            HBox bottomRow = (HBox) contentArea.getChildren().get(1);

            Label nameLabel = (Label) topRow.getChildren().get(0);
            Label timeLabel = (Label) topRow.getChildren().get(2);
            Label messageLabel = (Label) bottomRow.getChildren().get(0);

            if (selected) {
                nameLabel.setTextFill(Color.WHITE);
                timeLabel.setTextFill(Color.WHITE);
                messageLabel.setTextFill(Color.WHITE);
            } else {
                nameLabel.setTextFill(Color.web("#050505"));
                timeLabel.setTextFill(Color.web("#65676b"));
                messageLabel.setTextFill(Color.web("#65676b"));
            }
        }

        private String getInitials(String name) {
            String[] parts = name.split(" ");
            if (parts.length >= 2) {
                return (parts[0].charAt(0) + "" + parts[1].charAt(0)).toUpperCase();
            } else if (parts.length == 1 && !parts[0].isEmpty()) {
                return parts[0].substring(0, 1).toUpperCase();
            }
            return "?";
        }
    }
}