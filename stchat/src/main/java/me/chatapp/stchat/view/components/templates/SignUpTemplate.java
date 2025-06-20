package me.chatapp.stchat.view.components.templates;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import me.chatapp.stchat.view.components.organisms.BackgroundDecoration;

public class SignUpTemplate extends StackPane {
    private VBox mainCard;

    public SignUpTemplate() {
        setupBackground();
        setupMainCard();
        setupLayout();
        addEntranceAnimation();
    }

    private void setupBackground() {
        setStyle("-fx-background-color: linear-gradient(135deg, #667eea 0%, #764ba2 100%);");
        BackgroundDecoration.addToRoot(this);
    }

    private void setupMainCard() {
        mainCard = new VBox(20);
        mainCard.setAlignment(Pos.CENTER);
        mainCard.setPadding(new Insets(40, 35, 40, 35));
        mainCard.setMaxWidth(400);
        mainCard.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.95);" +
                        "-fx-background-radius: 20;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 25, 0, 0, 10);"
        );
    }

    public void addContent(Node... nodes) {
        if (!getChildren().contains(mainCard)) {
            getChildren().add(mainCard);
        }
        mainCard.getChildren().addAll(nodes);
    }


    private void addEntranceAnimation() {
        mainCard.setOpacity(0);
        mainCard.setScaleY(0.8);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(600), mainCard);
        fadeIn.setToValue(1.0);

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(600), mainCard);
        scaleIn.setToY(1.0);

        fadeIn.play();
        scaleIn.play();
    }
    private void setupLayout() {
        setAlignment(Pos.CENTER);
        if (!getChildren().contains(mainCard)) {
            getChildren().add(mainCard);
        }
    }
}
