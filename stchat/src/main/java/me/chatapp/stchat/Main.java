package me.chatapp.stchat;

import javafx.application.Application;
import javafx.stage.Stage;
import me.chatapp.stchat.controller.ChatController;
import me.chatapp.stchat.api.SocketClient;
import me.chatapp.stchat.view.init.SceneManager;
import me.chatapp.stchat.view.config.ChatViewConfig;
import me.chatapp.stchat.view.components.pages.ChatView;
import me.chatapp.stchat.view.components.pages.Login;
import me.chatapp.stchat.view.components.pages.SignUp;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            SocketClient socketClient = new SocketClient("localhost", 8080);
            AppContext.getInstance().setSocketClient(socketClient);
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
                ChatViewConfig config = new ChatViewConfig();
                ChatView view = new ChatView(config, user, loginStage);

                ChatController controller = new ChatController(user, loginStage);

                Stage primaryStage = SceneManager.getStage();
                primaryStage.setTitle("ST Chat - " + user.getUsername());
                primaryStage.setScene(view.getScene());
                primaryStage.setMaximized(true);
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
