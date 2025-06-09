package me.chatapp.stchat.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DatabaseConnection {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());

    // Thông tin kết nối database
    private static final String URL = "jdbc:postgresql://localhost:5432/stchat_db";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "chaocacban";

    private static Connection connection;

    /**
     * Tạo kết nối đến PostgreSQL database
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                LOGGER.info("Kết nối database thành công!");
            } catch (ClassNotFoundException e) {
                LOGGER.severe("Không tìm thấy PostgreSQL driver: " + e.getMessage());
                throw new SQLException("PostgreSQL driver không được tìm thấy", e);
            } catch (SQLException e) {
                LOGGER.severe("Lỗi kết nối database: " + e.getMessage());
                throw e;
            }
        }
        return connection;
    }

    /**
     * Đóng kết nối database
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                LOGGER.info("Đã đóng kết nối database");
            } catch (SQLException e) {
                LOGGER.warning("Lỗi khi đóng kết nối database: " + e.getMessage());
            }
        }
    }

    /**
     * Kiểm tra kết nối database
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            LOGGER.warning("Test kết nối thất bại: " + e.getMessage());
            return false;
        }
    }
}