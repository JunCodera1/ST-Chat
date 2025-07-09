package com.stchat.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DatabaseConnection {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());

    // Thông tin kết nối com.stchat.server.database
    private static final String URL = "jdbc:postgresql://localhost:5432/stchat_db";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "chaocacban";

    private static Connection connection;

    /**
     * Tạo kết nối đến PostgreSQL com.stchat.server.database
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                LOGGER.info("Connect db success!");
            } catch (ClassNotFoundException e) {
                LOGGER.severe("Can't found PostgreSQL driver: " + e.getMessage());
                throw new SQLException("PostgreSQL driver not found!", e);
            } catch (SQLException e) {
                LOGGER.severe("Error occurred when connect com.stchat.server.database : " + e.getMessage());
                throw e;
            }
        }
        return connection;
    }

    /**
     * Close the connect
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                LOGGER.info("Closed connection db");
            } catch (SQLException e) {
                LOGGER.warning("Error occurred when closing connect db: " + e.getMessage());
            }
        }
    }

    /**
     * Check connect a db
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            LOGGER.warning("Test is failed: " + e.getMessage());
            return false;
        }
    }
}