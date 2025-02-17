package io.github.prathamdby;

import io.github.prathamdby.chat.ChatManager;
import io.github.prathamdby.db.ChatMessageDAO;
import io.github.prathamdby.theme.ChatbotColors;
import io.github.prathamdby.ui.HeaderPanel;
import io.github.prathamdby.ui.InputPanel;
import io.github.prathamdby.ui.SidebarPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatbotMainWindow extends JFrame {
    private ChatManager chatManager;
    private InputPanel inputPanel;
    private SidebarPanel sidebarPanel;
    private JTextPane chatArea;
    private Timer typingTimer;
    private ChatMessageDAO chatMessageDAO;
    private final ExecutorService executorService;

    public ChatbotMainWindow() {
        this.executorService = Executors.newFixedThreadPool(4);
        chatMessageDAO = new ChatMessageDAO();
        initializeFrame();
        setupComponents();
        setupListeners();
        loadChatHistory();
        displayInitialMessage();
    }

    private void initializeFrame() {
        setTitle("AI Chatbot");
        setSize(1280, 720); // 16:9 aspect ratio
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(ChatbotColors.BACKGROUND_COLOR);
    }

    private void setupComponents() {
        // Chat area first since both header and sidebar need it
        chatArea = new JTextPane();
        chatManager = new ChatManager(chatArea);

        // Sidebar
        sidebarPanel = new SidebarPanel();
        add(sidebarPanel, BorderLayout.WEST);

        // Header with sidebar reference and chat manager
        HeaderPanel headerPanel = new HeaderPanel(sidebarPanel, chatManager);
        add(headerPanel, BorderLayout.NORTH);

        // Main content
        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(ChatbotColors.BACKGROUND_COLOR);

        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(ChatbotColors.BACKGROUND_COLOR);
        mainContentPanel.add(scrollPane, BorderLayout.CENTER);

        // Input panel
        ActionListener sendListener = e -> sendMessage();
        inputPanel = new InputPanel(sendListener);
        mainContentPanel.add(inputPanel, BorderLayout.SOUTH);

        add(mainContentPanel, BorderLayout.CENTER);
    }

    private void loadChatHistory() {
        chatMessageDAO.loadAllMessages()
                .thenAcceptAsync(messages -> {
                    for (String message : messages) {
                        String[] parts = message.split(": ", 2);
                        if (parts.length == 2) {
                            chatManager.addMessage(parts[0], parts[1]);
                            sidebarPanel.addToHistory(parts[0], parts[1]);
                        }
                    }
                }, SwingUtilities::invokeLater)
                .exceptionally(throwable -> {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(
                                this,
                                "Failed to load chat history: " + throwable.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    });
                    return null;
                });
    }

    private void setupListeners() {
        typingTimer = new Timer(500, e -> {
            if (inputPanel != null) {
                inputPanel.setStatusText("AI is typing...");
            }
        });
        typingTimer.setRepeats(false);
    }

    private void sendMessage() {
        if (inputPanel != null) {
            String userMessage = inputPanel.getInputText();
            if (!userMessage.trim().isEmpty()) {
                // Add user message
                chatManager.addMessage("You", userMessage);
                sidebarPanel.addToHistory("You", userMessage);
                inputPanel.clearInput();

                // Show typing indicator
                typingTimer.start();
                inputPanel.setStatusText("AI is typing...");

                // Generate AI response asynchronously
                chatManager.generateResponse(userMessage)
                        .thenAcceptAsync(aiResponse -> {
                            typingTimer.stop(); // Stop the typing timer
                            chatManager.addMessage("AI", aiResponse);
                            sidebarPanel.addToHistory("AI", aiResponse);
                            inputPanel.setStatusText("AI is ready");
                        }, SwingUtilities::invokeLater)
                        .exceptionally(throwable -> {
                            SwingUtilities.invokeLater(() -> {
                                typingTimer.stop(); // Stop the typing timer
                                String errorMessage = "Error generating response: " + throwable.getMessage();
                                chatManager.addMessage("AI", errorMessage);
                                sidebarPanel.addToHistory("AI", errorMessage);
                                inputPanel.setStatusText("AI is ready");
                            });
                            return null;
                        });
            }
        }
    }

    private void displayInitialMessage() {
        if (chatArea.getDocument().getLength() == 0) { // Only show if no history is loaded
            SwingUtilities.invokeLater(() -> {
                chatManager.addMessage("AI", "Hello! How can I assist you today?");
                sidebarPanel.addToHistory("AI", "Hello! How can I assist you today?");
            });
        }
    }

    @Override
    public void dispose() {
        executorService.shutdown();
        chatManager.shutdown();
        super.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ChatbotMainWindow().setVisible(true);
        });
    }
}