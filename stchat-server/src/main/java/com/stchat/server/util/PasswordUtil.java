package com.stchat.server.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.stchat.server.dao.UserDAO.LOGGER;

public class PasswordUtil {
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            // Convert byte array to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            LOGGER.severe("Error when hash the password: " + e.getMessage());
            throw new RuntimeException("Can't hash the password", e);
        }
    }
    public static boolean matchPassword(String input, String storedHash) {
        return hashPassword(input).equals(storedHash);
    }
}
