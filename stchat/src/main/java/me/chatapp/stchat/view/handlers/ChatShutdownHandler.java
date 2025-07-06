package me.chatapp.stchat.view.handlers;

import me.chatapp.stchat.api.SocketClient;
import me.chatapp.stchat.controller.MessageController;
import me.chatapp.stchat.model.User;

import java.util.logging.Logger;

public class ChatShutdownHandler {

    private static final Logger LOGGER = Logger.getLogger(ChatShutdownHandler.class.getName());

    public static void shutdown(User currentUser,
                                MessageController messageController,
                                ChatViewEventHandler eventHandler,
                                SocketClient socketClient) {

        if (currentUser != null) {
            LOGGER.info("User " + currentUser.getUsername() + " is closing the application");
        }

        // Close controller
        if (messageController != null) {
            messageController.close();
        }

        // Disconnect event handler
        if (eventHandler != null && eventHandler.getDisconnectAction() != null) {
            eventHandler.getDisconnectAction().execute();
        }

        // Close socket
        if (socketClient != null) {
            try {
                socketClient.close();
            } catch (Exception e) {
                LOGGER.warning("Error closing socket: " + e.getMessage());
            }
        }
    }
}
