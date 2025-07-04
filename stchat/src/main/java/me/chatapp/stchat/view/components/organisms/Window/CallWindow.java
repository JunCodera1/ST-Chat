package me.chatapp.stchat.view.components.organisms.Window;

import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class CallWindow {
    private final Stage stage;
    private Timeline pulseAnimation;
    private Timeline breathingAnimation;

    public CallWindow(String calleeName) {
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);

        // Create main container with glassmorphism effect
        VBox mainContainer = createMainContainer();

        // Avatar with animated rings
        VBox avatarContainer = createAnimatedAvatar();

        // Caller name with elegant typography
        Label nameLabel = createNameLabel(calleeName);

        // Status with subtle animation
        Label statusLabel = createStatusLabel();

        // Action buttons with modern design
        HBox buttonContainer = createButtonContainer();

        // Assemble layout
        VBox layout = new VBox(25);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(avatarContainer, nameLabel, statusLabel, buttonContainer);

        mainContainer.getChildren().add(layout);

        Scene scene = new Scene(mainContainer, 320, 380);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);

        // Start animations
        startAnimations();
    }

    private VBox createMainContainer() {
        VBox container = new VBox();
        container.setAlignment(Pos.CENTER);

        // Glassmorphism background
        String containerStyle = """
            -fx-background-color: rgba(255, 255, 255, 0.1);
            -fx-background-radius: 20;
            -fx-border-radius: 20;
            -fx-border-color: rgba(255, 255, 255, 0.2);
            -fx-border-width: 1;
            -fx-padding: 30;
        """;

        container.setStyle(containerStyle);

        // Background with gradient
        LinearGradient gradient = new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#667eea")),
                new Stop(1, Color.web("#764ba2"))
        );

        BackgroundFill fill = new BackgroundFill(gradient, new CornerRadii(20), null);
        container.setBackground(new Background(fill));

        return container;
    }

    private VBox createAnimatedAvatar() {
        VBox avatarContainer = new VBox();
        avatarContainer.setAlignment(Pos.CENTER);

        // Main avatar circle
        Circle avatar = new Circle(50);
        LinearGradient avatarGradient = new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#42b883")),
                new Stop(1, Color.web("#35a574"))
        );
        avatar.setFill(avatarGradient);

        // Outer pulse rings
        Circle pulseRing1 = new Circle(50);
        Circle pulseRing2 = new Circle(50);
        Circle pulseRing3 = new Circle(50);

        pulseRing1.setFill(Color.TRANSPARENT);
        pulseRing2.setFill(Color.TRANSPARENT);
        pulseRing3.setFill(Color.TRANSPARENT);

        pulseRing1.setStroke(Color.web("#42b883", 0.3));
        pulseRing2.setStroke(Color.web("#42b883", 0.2));
        pulseRing3.setStroke(Color.web("#42b883", 0.1));

        pulseRing1.setStrokeWidth(2);
        pulseRing2.setStrokeWidth(2);
        pulseRing3.setStrokeWidth(2);

        // Add glow effect to avatar
        Glow glow = new Glow(0.3);
        DropShadow shadow = new DropShadow(15, Color.rgb(66, 184, 131, 0.4));
        glow.setInput(shadow);
        avatar.setEffect(glow);

        // Stack all circles
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(pulseRing3, pulseRing2, pulseRing1, avatar);

        avatarContainer.getChildren().add(stackPane);

        // Create pulse animation
        createPulseAnimation(pulseRing1, pulseRing2, pulseRing3);

        return avatarContainer;
    }

    private void createPulseAnimation(Circle ring1, Circle ring2, Circle ring3) {
        // Ring 1 animation
        ScaleTransition scale1 = new ScaleTransition(Duration.seconds(2), ring1);
        scale1.setFromX(1.0);
        scale1.setFromY(1.0);
        scale1.setToX(1.8);
        scale1.setToY(1.8);
        scale1.setCycleCount(Timeline.INDEFINITE);

        FadeTransition fade1 = new FadeTransition(Duration.seconds(2), ring1);
        fade1.setFromValue(0.4);
        fade1.setToValue(0.0);
        fade1.setCycleCount(Timeline.INDEFINITE);

        // Ring 2 animation (delayed)
        ScaleTransition scale2 = new ScaleTransition(Duration.seconds(2), ring2);
        scale2.setFromX(1.0);
        scale2.setFromY(1.0);
        scale2.setToX(1.6);
        scale2.setToY(1.6);
        scale2.setDelay(Duration.seconds(0.3));
        scale2.setCycleCount(Timeline.INDEFINITE);

        FadeTransition fade2 = new FadeTransition(Duration.seconds(2), ring2);
        fade2.setFromValue(0.3);
        fade2.setToValue(0.0);
        fade2.setDelay(Duration.seconds(0.3));
        fade2.setCycleCount(Timeline.INDEFINITE);

        // Ring 3 animation (more delayed)
        ScaleTransition scale3 = new ScaleTransition(Duration.seconds(2), ring3);
        scale3.setFromX(1.0);
        scale3.setFromY(1.0);
        scale3.setToX(1.4);
        scale3.setToY(1.4);
        scale3.setDelay(Duration.seconds(0.6));
        scale3.setCycleCount(Timeline.INDEFINITE);

        FadeTransition fade3 = new FadeTransition(Duration.seconds(2), ring3);
        fade3.setFromValue(0.2);
        fade3.setToValue(0.0);
        fade3.setDelay(Duration.seconds(0.6));
        fade3.setCycleCount(Timeline.INDEFINITE);

        // Start all animations
        scale1.play();
        fade1.play();
        scale2.play();
        fade2.play();
        scale3.play();
        fade3.play();
    }

    private Label createNameLabel(String calleeName) {
        Label nameLabel = new Label(calleeName);
        nameLabel.setStyle("""
            -fx-font-size: 24px;
            -fx-font-weight: bold;
            -fx-text-fill: white;
            -fx-font-family: 'Segoe UI', sans-serif;
        """);

        // Add text shadow
        DropShadow textShadow = new DropShadow(3, Color.rgb(0, 0, 0, 0.5));
        nameLabel.setEffect(textShadow);

        return nameLabel;
    }

    private Label createStatusLabel() {
        Label statusLabel = new Label("📞 Calling...");
        statusLabel.setStyle("""
            -fx-font-size: 16px;
            -fx-text-fill: rgba(255, 255, 255, 0.8);
            -fx-font-family: 'Segoe UI', sans-serif;
        """);

        // Add breathing animation to status
        FadeTransition breathe = new FadeTransition(Duration.seconds(1.5), statusLabel);
        breathe.setFromValue(0.6);
        breathe.setToValue(1.0);
        breathe.setAutoReverse(true);
        breathe.setCycleCount(Timeline.INDEFINITE);
        breathe.play();

        return statusLabel;
    }

    private HBox createButtonContainer() {
        HBox buttonContainer = new HBox(20);
        buttonContainer.setAlignment(Pos.CENTER);

        // Accept button
        Button acceptButton = createButton("✓", "#27ae60", "#2ecc71");
        acceptButton.setOnAction(e -> {
            // Handle accept call
            stage.close();
        });

        // Decline button
        Button declineButton = createButton("✕", "#c0392b", "#e74c3c");
        declineButton.setOnAction(e -> stage.close());

        buttonContainer.getChildren().addAll(acceptButton, declineButton);

        return buttonContainer;
    }

    private Button createButton(String icon, String baseColor, String hoverColor) {
        Button button = new Button(icon);

        String buttonStyle = String.format("""
            -fx-background-color: %s;
            -fx-text-fill: white;
            -fx-font-size: 18px;
            -fx-font-weight: bold;
            -fx-background-radius: 50;
            -fx-min-width: 60;
            -fx-min-height: 60;
            -fx-max-width: 60;
            -fx-max-height: 60;
            -fx-border-radius: 50;
            -fx-cursor: hand;
        """, baseColor);

        button.setStyle(buttonStyle);

        // Add shadow effect
        DropShadow buttonShadow = new DropShadow(8, Color.rgb(0, 0, 0, 0.3));
        button.setEffect(buttonShadow);

        // Hover effects
        button.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(100), button);
            scale.setToX(1.1);
            scale.setToY(1.1);
            scale.play();

            button.setStyle(buttonStyle.replace(baseColor, hoverColor));
        });

        button.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(100), button);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();

            button.setStyle(buttonStyle);
        });

        // Press effect
        button.setOnMousePressed(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(50), button);
            scale.setToX(0.95);
            scale.setToY(0.95);
            scale.play();
        });

        button.setOnMouseReleased(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(50), button);
            scale.setToX(1.1);
            scale.setToY(1.1);
            scale.play();
        });

        return button;
    }

    private void startAnimations() {
        // Window entrance animation
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), stage.getScene().getRoot());
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(300), stage.getScene().getRoot());
        scaleIn.setFromX(0.8);
        scaleIn.setFromY(0.8);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);

        ParallelTransition entrance = new ParallelTransition(fadeIn, scaleIn);
        entrance.play();
    }

    public void show() {
        stage.show();
    }

    public void close() {
        stage.close();
    }
}