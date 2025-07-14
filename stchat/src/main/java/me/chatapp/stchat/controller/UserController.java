package me.chatapp.stchat.controller;

import javafx.application.Platform;
import me.chatapp.stchat.api.UserApiClient;
import me.chatapp.stchat.model.User;
import me.chatapp.stchat.view.components.organisms.Bar.NavigationSidebar;

import java.util.List;
import java.util.function.Consumer;

public class UserController {
    private final UserApiClient userApiClient = new UserApiClient();
    private final User currentUser;

    public UserController(User currentUser) {
        this.currentUser = currentUser;
    }

    public void loadAllUsers(Consumer<List<User>> onUsersLoaded, Consumer<Throwable> onError) {
        userApiClient.getAllUsers()
                .thenAccept(users -> {
                    List<User> filteredUsers = users.stream()
                            .filter(u -> !u.getUsername().equals(currentUser.getUsername()))
                            .toList();

                    Platform.runLater(() -> onUsersLoaded.accept(filteredUsers));
                })
                .exceptionally(throwable -> {
                    Platform.runLater(() -> onError.accept(throwable));
                    return null;
                });
    }

    public void close() {
        userApiClient.close();
    }
}
