package com.stchat.server.service;

import com.stchat.server.dao.UserDAO;
import com.stchat.server.model.User;

import java.util.List;
import java.util.Optional;

public class UserService {
    private final UserDAO userDAO = new UserDAO();

    public boolean registerUser(String username, String email, String password, String firstName, String lastName) {
        return userDAO.registerUser(username, email, password, firstName, lastName);
    }

    public Optional<User> getUserByUsername(String username) {
        return Optional.ofNullable(userDAO.getUserByUsername(username));
    }

    public Optional<User> getUserByEmail(String email) {
        return Optional.ofNullable(userDAO.getUserByEmail(email));
    }

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    public boolean isUsernameExists(String username) {
        return userDAO.isUsernameExists(username);
    }

    public boolean isEmailExists(String email) {
        return userDAO.isEmailExists(email);
    }

    public boolean updateUser(int userId, String username, String email) {
        return userDAO.updateUser(userId, username, email);
    }

    public boolean deleteUser(int userId) {
        return userDAO.deleteUser(userId);
    }

    public String resetPassword(String email) {
        return userDAO.resetPassword(email);
    }

    public int getUserCount() {
        return userDAO.getUserCount();
    }

    public boolean updatePassword(String email, String newPasswordHashed) {
        return userDAO.updatePassword(email, newPasswordHashed);
    }

    public Optional<String> getUsernameByEmail(String email) {
        return Optional.ofNullable(userDAO.getUsernameByEmail(email));
    }

    public boolean updateAvatar(int userId, String avatarUrl) {
        return userDAO.updateAvatarUrl(userId, avatarUrl);
    }

}
