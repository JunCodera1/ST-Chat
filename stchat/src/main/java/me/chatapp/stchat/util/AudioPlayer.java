package me.chatapp.stchat.util;


import javazoom.jl.player.Player;

import java.io.FileInputStream;

public class AudioPlayer {
    private static Player player;

    public static void play(String filePath) {
        try {
            if (player != null) {
                player.close();
            }
            FileInputStream fis = new FileInputStream(filePath);
            player = new Player(fis);
            new Thread(() -> {
                try {
                    player.play();
                } catch (Exception e) {
                    System.err.println("Lỗi khi phát MP3: " + e.getMessage());
                }
            }).start();
        } catch (Exception e) {
            System.err.println("Không thể phát MP3: " + e.getMessage());
        }
    }
}
