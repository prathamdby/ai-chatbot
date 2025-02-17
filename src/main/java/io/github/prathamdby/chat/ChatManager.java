package io.github.prathamdby.chat;

import io.github.prathamdby.theme.ChatbotColors;
import io.github.prathamdby.ui.util.CustomScrollBarUI;
import io.github.prathamdby.db.ChatMessageDAO;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.IOException;

import com.google.gson.Gson;
import okhttp3.*;

public class ChatManager {
    private final JTextPane chatArea;
    private final ChatMessageDAO chatMessageDAO;
    private final OkHttpClient client;
    private final Gson gson;
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final List<Message> messageHistory;
    private static final String API_URL = "http://localhost:1234/v1/chat/completions";
    private final ExecutorService executorService;

    public ChatManager(JTextPane chatArea) {
        this.chatArea = chatArea;
        this.chatMessageDAO = new ChatMessageDAO();
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
        this.gson = new Gson();
        this.messageHistory = new ArrayList<>();
        this.executorService = Executors.newFixedThreadPool(4);
        // Add system message to set the context
        this.messageHistory.add(new Message("system", "You are a helpful AI assistant."));
        setupChatArea();
    }

    private void setupChatArea() {
        chatArea.setEditable(false);
        chatArea.setBackground(ChatbotColors.BACKGROUND_COLOR);
        chatArea.setForeground(ChatbotColors.TEXT_COLOR);
        chatArea.setMargin(new java.awt.Insets(10, 10, 10, 10));
        chatArea.setCaretColor(ChatbotColors.TEXT_COLOR);

        // Set default text style
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet defaultStyle = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.FontFamily, "Segoe UI");
        defaultStyle = sc.addAttribute(defaultStyle, StyleConstants.FontSize, 14);
        defaultStyle = sc.addAttribute(defaultStyle, StyleConstants.Foreground, ChatbotColors.TEXT_COLOR);
        chatArea.setCharacterAttributes(defaultStyle, true);

        // Make sure any container JScrollPane uses the custom dark theme
        if (chatArea.getParent() instanceof JViewport &&
                chatArea.getParent().getParent() instanceof JScrollPane) {
            JScrollPane scrollPane = (JScrollPane) chatArea.getParent().getParent();
            scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
            scrollPane.getHorizontalScrollBar().setUI(new CustomScrollBarUI());
            scrollPane.getViewport().setBackground(ChatbotColors.BACKGROUND_COLOR);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
        }
    }

    private static class Message {
        String role;
        String content;

        Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }

    private static class ChatRequest {
        String model = "llama-3.2-3b-instruct";
        List<Message> messages;
        double temperature = 0.7;
        int max_tokens = -1;
        boolean stream = false;

        ChatRequest(List<Message> messages) {
            this.messages = messages;
        }
    }

    private static class ChatResponse {
        List<Choice> choices;

        static class Choice {
            Message message;
        }
    }

    public void addMessage(String sender, String message) {
        CompletableFuture.runAsync(() -> {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String timestamp = sdf.format(new Date());

            Color messageColor = sender.equals("You") ? ChatbotColors.USER_MESSAGE_COLOR
                    : ChatbotColors.AI_MESSAGE_COLOR;

            // Create final style attributes
            final AttributeSet messageStyle = StyleContext.getDefaultStyleContext().addAttribute(
                    SimpleAttributeSet.EMPTY,
                    StyleConstants.Foreground,
                    messageColor);
            final AttributeSet finalStyle = StyleContext.getDefaultStyleContext().addAttribute(
                    StyleContext.getDefaultStyleContext().addAttribute(
                            messageStyle,
                            StyleConstants.FontFamily,
                            "Segoe UI"),
                    StyleConstants.FontSize,
                    14);

            // Update UI on EDT
            SwingUtilities.invokeLater(() -> {
                try {
                    int len = chatArea.getDocument().getLength();
                    chatArea.setCaretPosition(len);
                    chatArea.getDocument().insertString(len, sender + " [" + timestamp + "]\n", finalStyle);
                    chatArea.getDocument().insertString(chatArea.getDocument().getLength(), message + "\n\n",
                            finalStyle);
                    chatArea.setCaretPosition(chatArea.getDocument().getLength());
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            });

            // Save to database in background
            CompletableFuture.runAsync(() -> {
                try {
                    chatMessageDAO.saveMessage(sender, message);
                } catch (Exception e) {
                    System.err.println("Failed to save message to database: " + e.getMessage());
                }
            }, executorService);
        }, executorService);
    }

    public void clearChat() {
        CompletableFuture.runAsync(() -> {
            // Clear UI on EDT
            SwingUtilities.invokeLater(() -> {
                try {
                    chatArea.getDocument().remove(0, chatArea.getDocument().getLength());
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            });

            // Clear message history
            synchronized (messageHistory) {
                messageHistory.clear();
                messageHistory.add(new Message("system", "You are a helpful AI assistant."));
            }

            // Clear database in background
            CompletableFuture.runAsync(() -> {
                try {
                    chatMessageDAO.clearAllMessages();
                } catch (Exception e) {
                    System.err.println("Failed to clear messages from database: " + e.getMessage());
                }
            }, executorService);
        }, executorService);
    }

    public CompletableFuture<String> generateResponse(String userMessage) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Add user message to history
                synchronized (messageHistory) {
                    messageHistory.add(new Message("user", userMessage));
                }

                ChatRequest chatRequest = new ChatRequest(new ArrayList<>(messageHistory));
                String requestBody = gson.toJson(chatRequest);

                Request request = new Request.Builder()
                        .url(API_URL)
                        .post(RequestBody.create(requestBody, JSON))
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected response code: " + response);
                    }

                    String responseBody = response.body().string();
                    ChatResponse chatResponse = gson.fromJson(responseBody, ChatResponse.class);

                    if (chatResponse.choices != null && !chatResponse.choices.isEmpty()
                            && chatResponse.choices.get(0).message != null) {
                        String aiResponse = chatResponse.choices.get(0).message.content;
                        // Add AI response to history
                        synchronized (messageHistory) {
                            messageHistory.add(new Message("assistant", aiResponse));
                        }
                        return aiResponse;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "I apologize, but I encountered an error while processing your request. Please try again later.";
            }
            return "I apologize, but I was unable to generate a response at this time.";
        }, executorService);
    }

    // Clean up resources when shutting down
    public void shutdown() {
        executorService.shutdown();
    }
}