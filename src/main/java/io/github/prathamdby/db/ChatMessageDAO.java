package io.github.prathamdby.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatMessageDAO {
    private final ExecutorService executorService;

    public ChatMessageDAO() {
        this.executorService = Executors.newFixedThreadPool(2);
    }

    public CompletableFuture<Void> saveMessage(String sender, String message) {
        return CompletableFuture.runAsync(() -> {
            String sql = "INSERT INTO chat_messages (sender, message) VALUES (?, ?)";

            try (Connection conn = DatabaseManager.getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, sender);
                pstmt.setString(2, message);
                pstmt.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to save chat message", e);
            }
        }, executorService);
    }

    public CompletableFuture<Void> saveAllMessages(List<String> messages) {
        return CompletableFuture.runAsync(() -> {
            clearAllMessages(); // Clear existing messages first

            String sql = "INSERT INTO chat_messages (sender, message) VALUES (?, ?)";

            try (Connection conn = DatabaseManager.getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(sql)) {

                conn.setAutoCommit(false);

                for (String fullMessage : messages) {
                    String[] parts = fullMessage.split(": ", 2);
                    if (parts.length == 2) {
                        pstmt.setString(1, parts[0]);
                        pstmt.setString(2, parts[1]);
                        pstmt.addBatch();
                    }
                }

                pstmt.executeBatch();
                conn.commit();

            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to save chat messages", e);
            }
        }, executorService);
    }

    public CompletableFuture<List<String>> loadAllMessages() {
        return CompletableFuture.supplyAsync(() -> {
            List<String> messages = new ArrayList<>();
            String sql = "SELECT sender, message, timestamp FROM chat_messages ORDER BY timestamp ASC";

            try (Connection conn = DatabaseManager.getConnection();
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    String sender = rs.getString("sender");
                    String message = rs.getString("message");
                    messages.add(sender + ": " + message);
                }

            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to load chat messages", e);
            }

            return messages;
        }, executorService);
    }

    public CompletableFuture<Void> clearAllMessages() {
        return CompletableFuture.runAsync(() -> {
            String sql = "DELETE FROM chat_messages";

            try (Connection conn = DatabaseManager.getConnection();
                    Statement stmt = conn.createStatement()) {

                stmt.executeUpdate(sql);

            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to clear chat messages", e);
            }
        }, executorService);
    }

    public void shutdown() {
        executorService.shutdown();
    }
}