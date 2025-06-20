package me.chatapp.stchat;

import javafx.application.Application;
import javafx.stage.Stage;
import me.chatapp.stchat.controller.ChatController;
import me.chatapp.stchat.model.ChatModel;
import me.chatapp.stchat.view.core.SceneManager;
import me.chatapp.stchat.view.config.ChatViewConfig;
import me.chatapp.stchat.view.components.pages.ChatView;
import me.chatapp.stchat.view.components.pages.Login;
import me.chatapp.stchat.view.components.pages.SignUp;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        SceneManager.setStage(primaryStage);
        showSignUpStage(primaryStage);
    }

    private void showSignUpStage(Stage primaryStage) {
        SignUp signUp = new SignUp(() -> showLoginStage(new Stage()));
        signUp.show();
    }

    private void showLoginStage(Stage loginStage) {
        Login login = new Login(() -> {
            loginStage.close();
            showSignUpStage(new Stage());
        }, user -> {
            // Khởi tạo ChatModel
            ChatModel model = new ChatModel();

            // Khởi tạo ChatView với user
            ChatViewConfig config = new ChatViewConfig();
            ChatView view = new ChatView(config, user);

            // Khởi tạo ChatControllerc
            ChatController controller = new ChatController(model, view);

            // Thiết lập stage chính
            Stage primaryStage = SceneManager.getStage();
            primaryStage.setTitle("ST Chat - " + user.getUsername());
            primaryStage.setScene(view.getScene());
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(650);
            primaryStage.show();

            // Khởi tạo controller
            controller.initialize();
        });
        login.show();
    }

    public static void main(String[] args) {
        launch();
    }
}