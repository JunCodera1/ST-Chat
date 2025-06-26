package me.chatapp.stchat.network;

import me.chatapp.stchat.model.User;

public class SessionContext {
    private final User user;
    private final SocketClient client;

    public SessionContext(User user, SocketClient client) {
        this.user = user;
        this.client = client;
    }

    public User getUser() {
        return user;
    }

    public SocketClient getClient() {
        return client;
    }
}

