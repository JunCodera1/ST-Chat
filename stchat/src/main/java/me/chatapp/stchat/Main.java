package me.chatapp.stchat;

import javafx.application.Application;
import javafx.stage.Stage;
import me.chatapp.stchat.controller.ChatController;
import me.chatapp.stchat.model.ChatModel;
import me.chatapp.stchat.view.ChatView;
import me.chatapp.stchat.view.Login;
import me.chatapp.stchat.view.SignUp;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Start with SignUp stage
        showSignUpStage(primaryStage);
    }

    private void showSignUpStage(Stage primaryStage) {
        SignUp signUp = new SignUp(() -> showLoginStage(primaryStage));
        signUp.show();
    }

    private void showLoginStage(Stage primaryStage) {
        Login login = new Login(() -> showSignUpStage(primaryStage), () -> {
            ChatModel model = new ChatModel();
            ChatView view = new ChatView();
            ChatController controller = new ChatController(model, view);

            primaryStage.setTitle("ST Chat - Modern Chat Application");
            primaryStage.setScene(view.getScene());
            primaryStage.setMinWidth(1600);
            primaryStage.setMinHeight(1000);
            primaryStage.show();

            controller.initialize();
        });
        login.show();
    }

    public static void main(String[] args) {
        launch();
    }
}