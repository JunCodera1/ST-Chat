package me.chatapp.stchat.view.handlers;

import javafx.application.Platform;
import me.chatapp.stchat.model.Message;
import me.chatapp.stchat.model.User;
import me.chatapp.stchat.view.state.ChatViewStateManager;

import java.util.logging.Logger;

public class ChatAutoConnector {

    private static final Logger LOGGER = Logger.getLogger(ChatAutoConnector.class.getName());

    public static void connect(User user, ChatViewStateManager stateManager, Runnable updateConnectionStatus) {
        if (user == null) return;

        new Thread(() -> {
            try {
                Thread.sleep(1000);
                Platform.runLater(() -> {
                    stateManager.addMessage(new Message("System",
                            "Connecting to chat server...",
                            Message.MessageType.SYSTEM));
                    updateConnectionStatus.run();
                    stateManager.addMessage(new Message("System",
                            "Connected successfully! You can now start chatting.",
                            Message.MessageType.SYSTEM));
                    LOGGER.info("Auto-connected user: " + user.getUsername());
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.warning("Auto-connect interrupted: " + e.getMessage());
            }
        }).start();
    }
}
