package me.chatapp.stchat;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import me.chatapp.stchat.controller.ChatController;
import me.chatapp.stchat.model.ChatModel;
import me.chatapp.stchat.service.ChatService;
import me.chatapp.stchat.view.ChatView;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Initialize MVC components
        ChatModel model = new ChatModel();
        ChatView view = new ChatView();
        ChatController controller = new ChatController(model, view);

        // Setup stage
        primaryStage.setTitle("ST Chat - Modern Chat Application");
        primaryStage.setScene(view.getScene());
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(500);
        primaryStage.show();

        // Initialize controller after view is shown
        controller.initialize();
    }

    public static void main(String[] args) {
        launch();
    }
}
