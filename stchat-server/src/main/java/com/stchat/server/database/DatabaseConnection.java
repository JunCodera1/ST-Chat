package com.stchat.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DatabaseConnection {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());

    private static final String URL = "jdbc:postgresql://localhost:5432/stchat_db";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "chaocacban";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            LOGGER.info("Connect db success!");
            return conn;
        } catch (ClassNotFoundException e) {
            LOGGER.severe("Can't find PostgreSQL driver: " + e.getMessage());
            throw new SQLException("PostgreSQL driver not found!", e);
        } catch (SQLException e) {
            LOGGER.severe("Error occurred when connecting to db: " + e.getMessage());
            throw e;
        }
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            LOGGER.warning("Test failed: " + e.getMessage());
            return false;
        }
    }
}
