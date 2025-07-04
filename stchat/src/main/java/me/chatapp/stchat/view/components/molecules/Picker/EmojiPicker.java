package me.chatapp.stchat.view.components.molecules.Picker;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;
import javafx.stage.Popup;
import me.chatapp.stchat.view.components.atoms.Button.EmojiButton;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.Random;
import java.util.function.Consumer;

public class EmojiPicker {
    private final Popup emojiPopup;
    private final Button emojiButton;

    // Dữ liệu: tên icon + label
    private final String[][] smileys = {
            {"fas-smile", "Smile"}, {"fas-laugh", "Laugh"}, {"fas-grin-hearts", "Love"}, {"fas-kiss-wink-heart", "Flirt"}, {"fas-surprise", "Surprise"}
    };

    private final String[][] gestures = {
            {"fas-thumbs-up", "Thumbs Up"}, {"fas-thumbs-down", "Thumbs Down"}, {"fas-hand-peace", "Peace"}, {"fas-hand-point-up", "Point Up"}, {"fas-hand-spock", "Spock"}
    };

    private final String[][] hearts = {
            {"fas-heart", "Heart"}, {"fas-heart-broken", "Broken Heart"}
    };

    public EmojiPicker(Consumer<String> onEmojiSelected) {
        emojiButton = new EmojiButton();
        emojiPopup = new Popup();

        emojiButton.setOnAction(e -> toggleEmojiPopup());

        setupEmojiPopup(onEmojiSelected);
    }

    private void setupEmojiPopup(Consumer<String> onEmojiSelected) {
        VBox popupContent = new VBox(10);
        popupContent.setPadding(new Insets(15));
        popupContent.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #e9ecef;
            -fx-border-radius: 12px;
            -fx-background-radius: 12px;
            -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);
        """);

        Label title = new Label("Choose an icon");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #495057;");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPrefSize(280, 200);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox emojiContent = new VBox(15);

        addIconCategory(emojiContent, "Smileys", smileys, onEmojiSelected);
        addIconCategory(emojiContent, "Gestures", gestures, onEmojiSelected);
        addIconCategory(emojiContent, "Hearts", hearts, onEmojiSelected);

        scrollPane.setContent(emojiContent);
        popupContent.getChildren().addAll(title, scrollPane);

        emojiPopup.getContent().clear();
        emojiPopup.getContent().add(popupContent);
        emojiPopup.setAutoHide(true);
    }

    private void addIconCategory(VBox parent, String categoryName, String[][] icons, Consumer<String> onEmojiSelected) {
        Label categoryLabel = new Label(categoryName);
        categoryLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c757d; -fx-font-weight: bold;");

        FlowPane iconGrid = new FlowPane();
        iconGrid.setHgap(8);
        iconGrid.setVgap(8);
        iconGrid.setAlignment(Pos.CENTER_LEFT);

        for (String[] iconInfo : icons) {
            String iconCode = iconInfo[0];
            String tooltip = iconInfo[1];

            FontIcon icon = new FontIcon(iconCode);
            icon.setIconSize(20);
            icon.setIconColor(getRandomColor());

            Button iconBtn = new Button();
            iconBtn.setGraphic(icon);
            iconBtn.setTooltip(new Tooltip(tooltip));
            iconBtn.setStyle("-fx-background-color: transparent;");
            iconBtn.setPrefSize(32, 32);

            iconBtn.setOnMouseEntered(e -> iconBtn.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8px;"));
            iconBtn.setOnMouseExited(e -> iconBtn.setStyle("-fx-background-color: transparent;"));

            iconBtn.setOnAction(e -> {
                onEmojiSelected.accept(iconCode); // truyền iconCode (ví dụ "fas-smile") về cha
                emojiPopup.hide();
            });

            iconGrid.getChildren().add(iconBtn);
        }

        parent.getChildren().addAll(categoryLabel, iconGrid);
    }

    private void toggleEmojiPopup() {
        if (emojiPopup.isShowing()) {
            emojiPopup.hide();
        } else {
            javafx.geometry.Bounds bounds = emojiButton.localToScreen(emojiButton.getBoundsInLocal());
            emojiPopup.show(emojiButton, bounds.getMinX(), bounds.getMaxY());
        }
    }

    private Color getRandomColor() {
        Color[] palette = {
                Color.web("#f39c12"), Color.web("#e74c3c"), Color.web("#8e44ad"),
                Color.web("#2ecc71"), Color.web("#3498db"), Color.web("#ff6b6b")
        };
        return palette[new Random().nextInt(palette.length)];
    }

    public Button getEmojiButton() {
        return emojiButton;
    }
}
