package me.chatapp.stchat.view.components.organisms;

import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.Insets;
import me.chatapp.stchat.view.components.atom.PrimarySearchField;
import me.chatapp.stchat.view.components.molecules.ConversationCell;

import java.util.function.Consumer;

public class ConversationSidebar {
    private final VBox container;
    private final ListView<ConversationItem> conversationList;
    private Consumer<String> onConversationSelected;

    public ConversationSidebar() {
        container = new VBox(10);
        container.setPadding(new Insets(10));

        PrimarySearchField searchField = new PrimarySearchField("🔍 Search conversations...");
        Region spacer = new Region();
        spacer.setPrefHeight(15);

        conversationList = new ListView<>();
        conversationList.setCellFactory(listView -> new ConversationCell());
        VBox.setVgrow(conversationList, Priority.ALWAYS);

        conversationList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && onConversationSelected != null) {
                onConversationSelected.accept(newVal.getName());
            }
        });

        container.getChildren().addAll(searchField, spacer, conversationList);
    }

    public VBox getComponent() {
        return container;
    }

    public void addConversation(String name, String lastMessage, String time, boolean hasUnread) {
        conversationList.getItems().add(new ConversationItem(name, lastMessage, time, hasUnread));
    }

    public void setOnConversationSelected(Consumer<String> callback) {
        this.onConversationSelected = callback;
    }

    public void clearConversations() {
        conversationList.getItems().clear();
    }

    public static class ConversationItem {
        private final String name, lastMessage, time;
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
}