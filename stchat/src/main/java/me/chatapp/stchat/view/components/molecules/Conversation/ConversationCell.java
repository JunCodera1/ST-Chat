package me.chatapp.stchat.view.components.molecules.Conversation;
import javafx.scene.control.ListCell;
import me.chatapp.stchat.view.components.organisms.Bar.ConversationSidebar;

public class ConversationCell extends ListCell<ConversationSidebar.ConversationItem> {
    private ConversationPreviewItem preview;

    @Override
    protected void updateItem(ConversationSidebar.ConversationItem item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
            setText(null);
            setStyle("-fx-background-color: transparent;");
        } else {
            preview = new ConversationPreviewItem(
                    item
            );

            setGraphic(preview);
            setText(null);
            setStyle("""
                -fx-background-color: transparent;
                -fx-padding: 8 12;
                -fx-border-color: transparent;
                """);

            // Hover
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

            selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                if (preview != null) {
                    setStyle(isSelected
                            ? "-fx-background-color: #1877f2; -fx-background-radius: 8; -fx-padding: 8 12;"
                            : "-fx-background-color: transparent; -fx-padding: 8 12;");
                    preview.setSelected(isSelected);
                }
            });
        }
    }
}

