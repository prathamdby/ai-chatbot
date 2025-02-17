package io.github.prathamdby.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/chatbot?createDatabaseIfNotExist=true";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    private static HikariDataSource dataSource;

    static {
        initializeDatabase();
    }

    private static void initializeDatabase() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DB_URL);
        config.setUsername(DB_USER);
        config.setPassword(DB_PASSWORD);

        // Connection pool settings
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30000); // 30 seconds
        config.setIdleTimeout(600000); // 10 minutes

        // MySQL specific properties
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(config);

        // Create tables if they don't exist
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement()) {

            // Chat messages table
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS chat_messages (" +
                            "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                            "sender VARCHAR(50) NOT NULL," +
                            "message TEXT NOT NULL," +
                            "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP" +
                            ")");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void closeDataSource() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}