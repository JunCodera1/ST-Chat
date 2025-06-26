package com.stchat.server.model;

public class PendingPasswordChange {
    private final String email;
    private final String hashedPassword;
    private final String token;

    public PendingPasswordChange(String email, String hashedPassword, String token) {
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public String getToken() {
        return token;
    }
}
