package com.stchat.server.service;

import com.stchat.server.model.User;

import java.util.*;

public class UserService {

    private static final Map<Integer, User> users = new HashMap<>();
    private static int idCounter = 1;

    public static List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public static User getUserById(int id) {
        return users.get(id);
    }

    public static User getUserByUsername(String username) {
        return users.values()
                .stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
    }

    public static User createUser(User user) {
        user.setId(idCounter++);
        users.put(user.getId(), user);
        return user;
    }

    public static boolean deleteUser(int id) {
        return users.remove(id) != null;
    }
}
