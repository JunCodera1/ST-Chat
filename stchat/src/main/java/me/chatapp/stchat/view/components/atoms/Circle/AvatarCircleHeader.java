package me.chatapp.stchat.view.components.atoms.Circle;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Objects;

public class AvatarCircleHeader extends StackPane {
    private final Circle circle;
    private final Label initials;

    public AvatarCircleHeader(String name, Color color) {
        circle = new Circle(20, color);
        initials = new Label(getInitials(name));
        initials.setTextFill(Color.WHITE);
        initials.setFont(Font.font("System", FontWeight.BOLD, 12));
        getChildren().addAll(circle, initials);
    }

    private String getInitials(String name) {
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        return ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
    }

    public void setImageFromUrl(String imageUrl) {
        try {
            if (imageUrl == null || imageUrl.isBlank()) throw new IllegalArgumentException("URL is null or blank");

            Image image = new Image(imageUrl, true);

            image.errorProperty().addListener((obs, old, isError) -> {
                if (isError) {
                    System.err.println("⚠️ Lỗi khi load avatar từ URL: " + imageUrl);
                    loadDefaultAvatar();
                }
            });

            image.progressProperty().addListener((obs, old, progress) -> {
                if (progress.doubleValue() >= 1.0 && !image.isError()) {
                    Platform.runLater(() -> {
                        circle.setFill(new ImagePattern(image));
                        initials.setVisible(false);
                    });
                }
            });

        } catch (Exception e) {
            System.err.println("⚠️ Không thể load avatar: " + e.getMessage());
            loadDefaultAvatar();
        }
    }


    private void loadDefaultAvatar() {
        Image fallback = new Image(Objects.requireNonNull(getClass().getResource("/image/default_avatar.png")).toExternalForm());
        circle.setFill(new ImagePattern(fallback));
        initials.setVisible(false);
    }


    public void setColor(Color color) {
        circle.setFill(color);
    }

    public void setInitials(String name) {
        initials.setText(getInitials(name));
    }
}
