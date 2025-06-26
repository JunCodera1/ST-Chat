package me.chatapp.stchat.view.components.molecules.Picker;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import me.chatapp.stchat.view.components.atoms.Button.EmojiButton;

public class EmojiPicker {
    private final Popup emojiPopup;
    private final EmojiButton emojiButton;

    // Emoji categories
    private final String[] smileys = {"😀", "😃", "😄", "😁", "😅", "😂", "🤣", "😊", "😇", "🙂", "🙃", "😉", "😌", "😍", "🥰", "😘", "😗", "😙", "😚", "😋", "😛", "😝", "😜", "🤪", "🤨", "🧐", "🤓", "😎", "🤩", "🥳"};
    private final String[] gestures = {"👍", "👎", "👌", "🤌", "🤏", "✌️", "🤞", "🤟", "🤘", "🤙", "👈", "👉", "👆", "🖕", "👇", "☝️", "👋", "🤚", "🖐️", "✋", "🖖", "👏", "🙌", "🤲", "🤝", "🙏"};
    private final String[] hearts = {"❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎", "💔", "❣️", "💕", "💞", "💓", "💗", "💖", "💘", "💝"};

    public EmojiPicker(Runnable onEmojiSelected) {
        emojiButton = new EmojiButton();
        emojiPopup = new Popup();

        emojiButton.setOnAction(e -> toggleEmojiPopup());
        setupEmojiPopup(onEmojiSelected);
    }

    private void setupEmojiPopup(Runnable onEmojiSelected) {
        VBox popupContent = new VBox(10);
        popupContent.setPadding(new Insets(15));
        popupContent.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #e9ecef; " +
                "-fx-border-radius: 12px; " +
                "-fx-background-radius: 12px; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        // Title
        Label title = new Label("What's Your Mood?");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #495057;");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPrefSize(280, 200);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox emojiContent = new VBox(15);

        // Add categories
        addEmojiCategory(emojiContent, "Smileys & People", smileys, onEmojiSelected);
        addEmojiCategory(emojiContent, "Gestures", gestures, onEmojiSelected);
        addEmojiCategory(emojiContent, "Hearts", hearts, onEmojiSelected);

        scrollPane.setContent(emojiContent);
        popupContent.getChildren().addAll(title, scrollPane);

        emojiPopup.getContent().clear();
        emojiPopup.getContent().add(popupContent);
        emojiPopup.setAutoHide(true);
    }

    private void addEmojiCategory(VBox parent, String categoryName, String[] emojis, Runnable onEmojiSelected) {
        Label categoryLabel = new Label(categoryName);
        categoryLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c757d; -fx-font-weight: bold;");

        FlowPane emojiGrid = new FlowPane();
        emojiGrid.setHgap(5);
        emojiGrid.setVgap(5);
        emojiGrid.setAlignment(Pos.CENTER_LEFT);

        for (String emoji : emojis) {
            Button emojiBtn = new Button(emoji);
            emojiBtn.setStyle("-fx-font-size: 18px; -fx-background-color: transparent; " +
                    "-fx-border-radius: 8px; -fx-background-radius: 8px; " +
                    "-fx-min-width: 32px; -fx-min-height: 32px;");

            emojiBtn.setOnMouseEntered(e -> emojiBtn.setStyle(emojiBtn.getStyle() + "-fx-background-color: #f8f9fa;"));
            emojiBtn.setOnMouseExited(e -> emojiBtn.setStyle(emojiBtn.getStyle().replace("-fx-background-color: #f8f9fa;", "")));

            emojiBtn.setOnAction(e -> {
                // Notify parent component
                onEmojiSelected.run();
                emojiPopup.hide();
            });

            emojiGrid.getChildren().add(emojiBtn);
        }

        parent.getChildren().addAll(categoryLabel, emojiGrid);
    }

    private void toggleEmojiPopup() {
        if (emojiPopup.isShowing()) {
            emojiPopup.hide();
        } else {
            javafx.geometry.Bounds bounds = emojiButton.localToScreen(emojiButton.getBoundsInLocal());
            emojiPopup.show(emojiButton, bounds.getMinX(), bounds.getMaxY() - 220);
        }
    }

    public EmojiButton getEmojiButton() {
        return emojiButton;
    }
}