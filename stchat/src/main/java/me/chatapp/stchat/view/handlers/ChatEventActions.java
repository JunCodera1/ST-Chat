package me.chatapp.stchat.view.handlers;

public class ChatEventActions {

    @FunctionalInterface
    public interface ConnectAction {
        void execute(String host, int port, String username);
    }

    @FunctionalInterface
    public interface DisconnectAction {
        void execute();
    }

    @FunctionalInterface
    public interface SendMessageAction {
        void execute(String message);
    }
}