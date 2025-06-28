package me.chatapp.stchat.view.config;


public class ChatViewConfig {

    public static final int DEFAULT_PORT = 8080;
    public static final String DEFAULT_TITLE = "ST Chat - Modern Interface";
    public static final double DEFAULT_WIDTH = 1000;
    public static final double DEFAULT_HEIGHT = 700;
    public static final double MIN_WIDTH = 800;
    public static final double MIN_HEIGHT = 600;

    // CSS files
    public static final String[] CSS_FILES = {
            "/css/chat.css",
            "/css/style.css",
            "/css/login.css",
            "/css/signup.css"
    };

    // Time format
    public static final String TIME_FORMAT = "HH:mm:ss";

    // Validation
    public static final int MIN_USERNAME_LENGTH = 3;
    public static final int MAX_USERNAME_LENGTH = 20;
    public static final int MAX_MESSAGE_LENGTH = 500;

    private int port;
    private String title;
    private double width;
    private double height;
    private double minWidth;
    private double minHeight;

    public ChatViewConfig() {
        this.port = DEFAULT_PORT;
        this.title = DEFAULT_TITLE;
        this.width = DEFAULT_WIDTH;
        this.height = DEFAULT_HEIGHT;
        this.minWidth = MIN_WIDTH;
        this.minHeight = MIN_HEIGHT;
    }

    // Getters and Setters
    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public double getWidth() { return width; }
    public void setWidth(double width) { this.width = width; }

    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; }

    public double getMinWidth() { return minWidth; }
    public void setMinWidth(double minWidth) { this.minWidth = minWidth; }

    public double getMinHeight() { return minHeight; }
    public void setMinHeight(double minHeight) { this.minHeight = minHeight; }

    /**
     * Validate username
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        String trimmed = username.trim();
        return trimmed.length() >= MIN_USERNAME_LENGTH &&
                trimmed.length() <= MAX_USERNAME_LENGTH &&
                trimmed.matches("^[a-zA-Z0-9_]+$"); // Chỉ cho phép chữ, số và underscore
    }

    public static boolean isValidMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            return false;
        }
        return message.trim().length() <= MAX_MESSAGE_LENGTH;
    }


    public static boolean isValidHost(String host) {
        if (host == null || host.trim().isEmpty()) {
            return false;
        }
        return true;
    }
}