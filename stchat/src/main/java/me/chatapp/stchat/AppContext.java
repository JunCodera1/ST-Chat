package me.chatapp.stchat;

import me.chatapp.stchat.api.SocketClient;

public class AppContext {
    private static final AppContext instance = new AppContext();
    private SocketClient socketClient;

    private AppContext() {}

    public static AppContext getInstance() {
        return instance;
    }

    public void setSocketClient(SocketClient socketClient) {
        this.socketClient = socketClient;
    }

    public SocketClient getSocketClient() {
        return socketClient;
    }
}
