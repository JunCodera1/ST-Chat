package me.chatapp.stchat.view.core;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class SceneManager {
    private static Stage primaryStage;

    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    public static void switchScene(Parent root) {
        Scene scene = new Scene(root, 1600, 1000); // Kích thước tùy bạn
        primaryStage.setScene(scene);
    }

    public static void switchScene(Scene newScene) {
        primaryStage.setScene(newScene); // ✅ không tạo lại scene
    }
}
