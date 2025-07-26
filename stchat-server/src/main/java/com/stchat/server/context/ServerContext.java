package com.stchat.server.context;

import com.stchat.server.Main;

public class ServerContext {
    private static final ServerContext instance = new ServerContext();
    private Main mainServer;

    public static ServerContext getInstance() {
        return instance;
    }

    public void setMainServer(Main server) {
        this.mainServer = server;
    }

    public Main getMainServer() {
        return mainServer;
    }
}

