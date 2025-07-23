package me.chatapp.stchat;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import me.chatapp.stchat.api.SocketClient;
import me.chatapp.stchat.controller.MessageController;
import me.chatapp.stchat.model.Message;
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
            socketClient.startListening(json -> {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    Message message = mapper.readValue(json, Message.class);

                    MessageController.getInstance().receiveMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });


        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        SceneManager.setStage(primaryStage);

        primaryStage.setOnCloseRequest(event -> {
            AppContext.getInstance().getSocketClient().close();
            Platform.exit();
            System.exit(0);
        });

        showLoginStage(primaryStage);
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

                Stage primaryStage = SceneManager.getStage();
                primaryStage.setTitle("ST Chat - " + user.getUsername());
                primaryStage.setScene(view.getScene());
                primaryStage.setMaximized(true);
                primaryStage.show();
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
