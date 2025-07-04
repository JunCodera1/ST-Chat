package me.chatapp.stchat.view.components.organisms.Window;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class VideoCallWindow {
    private final Stage stage;

    public VideoCallWindow(String localUser, String remoteUser) {
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: black;");

        // Top bar: remote user name
        Label remoteNameLabel = new Label(remoteUser);
        remoteNameLabel.setStyle("""
            -fx-text-fill: white;
            -fx-font-size: 18px;
            -fx-font-weight: bold;
            -fx-padding: 10px;
        """);

        HBox topBar = new HBox(remoteNameLabel);
        topBar.setAlignment(Pos.TOP_LEFT);
        topBar.setPadding(new Insets(10));
        root.setTop(topBar);

        // Center: remote video (placeholder)
        StackPane remoteVideo = new StackPane();
        remoteVideo.setStyle("-fx-background-color: #333333;");
        remoteVideo.setPrefSize(640, 480);

        Label remoteVideoLabel = new Label("Remote Video");
        remoteVideoLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        remoteVideo.getChildren().add(remoteVideoLabel);

        root.setCenter(remoteVideo);

        // Bottom-right: local video (small preview)
        StackPane localVideo = new StackPane();
        localVideo.setPrefSize(120, 90);
        localVideo.setStyle("-fx-background-color: #555;");
        Label localLabel = new Label("You");
        localLabel.setStyle("-fx-text-fill: white;");
        localVideo.getChildren().add(localLabel);
        localVideo.setPadding(new Insets(5));
        localVideo.setEffect(new DropShadow());

        StackPane.setAlignment(localVideo, Pos.BOTTOM_RIGHT);
        StackPane videoLayer = new StackPane(remoteVideo, localVideo);
        root.setCenter(videoLayer);

        // Bottom controls
        HBox controls = new HBox(20);
        controls.setAlignment(Pos.CENTER);
        controls.setPadding(new Insets(15));

        Button micButton = createCircleButton("🎤", "#f39c12");
        Button cameraButton = createCircleButton("📷", "#2980b9");
        Button endCallButton = createCircleButton("❌", "#c0392b");

        endCallButton.setOnAction(e -> stage.close());

        controls.getChildren().addAll(micButton, cameraButton, endCallButton);
        root.setBottom(controls);

        Scene scene = new Scene(root, 700, 520);
        stage.setScene(scene);
    }

    private Button createCircleButton(String icon, String color) {
        Button button = new Button(icon);
        button.setStyle(String.format("""
            -fx-background-color: %s;
            -fx-text-fill: white;
            -fx-font-size: 20px;
            -fx-background-radius: 50;
            -fx-min-width: 50;
            -fx-min-height: 50;
            -fx-max-width: 50;
            -fx-max-height: 50;
            -fx-cursor: hand;
        """, color));
        return button;
    }

    public void show() {
        stage.show();
    }
}
