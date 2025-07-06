package me.chatapp.stchat.test;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import me.chatapp.stchat.AppContext;
import me.chatapp.stchat.api.SocketClient;
import me.chatapp.stchat.controller.MessageController;
import me.chatapp.stchat.model.User;

public class MessageTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize JavaFX toolkit is now available

        // 1. Login user1
        SocketClient socketClient1 = new SocketClient("localhost", 8080);
        socketClient1.simulateLogin("user1", "123456");
        AppContext.getInstance().setSocketClient(socketClient1);

        // 2. Create controller
        MessageController messageController = new MessageController();

        // 3. Create test user
        // 3. Create test user
        User user1 = new User();
        user1.setUsername("user1");


        // 4. Send message
        int conversationId = 1;
        messageController.sendMessage(user1, "Xin chào từ REST + Socket", conversationId, null);

        // 5. Listen for responses
        socketClient1.startListening(response -> {
            System.out.println("Server response: " + response);
        });

        // 6. Wait and then close
        new Thread(() -> {
            try {
                Thread.sleep(5000);
                Platform.runLater(() -> {
                    messageController.close();
                    try {
                        socketClient1.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println("Test completed.");
                    Platform.exit();
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}