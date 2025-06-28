package me.chatapp.stchat;

import javafx.application.Application;
import javafx.stage.Stage;
import me.chatapp.stchat.controller.ChatController;
import me.chatapp.stchat.model.ChatModel;
import me.chatapp.stchat.network.SocketClient;
import me.chatapp.stchat.view.core.SceneManager;
import me.chatapp.stchat.view.config.ChatViewConfig;
import me.chatapp.stchat.view.components.pages.ChatView;
import me.chatapp.stchat.view.components.pages.Login;
import me.chatapp.stchat.view.components.pages.SignUp;

import java.io.IOException;

public class Main extends Application {

    private SocketClient socketClient;

    @Override
    public void start(Stage primaryStage) {
        try {
            socketClient = new SocketClient("localhost", 8080);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

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
            try {
                SocketClient client = this.socketClient;

                ChatModel model = new ChatModel();
                ChatViewConfig config = new ChatViewConfig();
                ChatView view = new ChatView(config, user, loginStage);

                ChatController controller = new ChatController(model, view, client);

                Stage primaryStage = SceneManager.getStage();
                primaryStage.setTitle("ST Chat - " + user.getUsername());
                primaryStage.setScene(view.getScene());
                primaryStage.setMinWidth(900);
                primaryStage.setMinHeight(650);
                primaryStage.show();

                controller.initialize();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        login.show();
    }


    public static void main(String[] args) {
        launch();
    }
}
