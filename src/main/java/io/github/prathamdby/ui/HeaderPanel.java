package io.github.prathamdby.ui;

import io.github.prathamdby.theme.ChatbotColors;
import io.github.prathamdby.db.ChatMessageDAO;
import io.github.prathamdby.chat.ChatManager;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class HeaderPanel extends JPanel {
    private final ChatMessageDAO chatMessageDAO;
    private final SidebarPanel sidebarPanel;
    private final ChatManager chatManager;

    public HeaderPanel(SidebarPanel sidebarPanel, ChatManager chatManager) {
        this.sidebarPanel = sidebarPanel;
        this.chatManager = chatManager;
        this.chatMessageDAO = new ChatMessageDAO();

        setBackground(ChatbotColors.SECONDARY_BACKGROUND);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel titleLabel = new JLabel("AI Chatbot");
        titleLabel.setForeground(ChatbotColors.TEXT_COLOR);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        add(titleLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        JButton clearButton = createHeaderButton("Clear chat");
        JButton saveButton = createHeaderButton("Save chat");

        clearButton.addActionListener(e -> handleClearChat());
        saveButton.addActionListener(e -> handleSaveChat());

        // Apply hover effect
        setupButtonHoverEffect(clearButton);
        setupButtonHoverEffect(saveButton);

        buttonPanel.add(clearButton);
        buttonPanel.add(saveButton);
        add(buttonPanel, BorderLayout.EAST);
    }

    private JButton createHeaderButton(String text) {
        JButton button = new JButton(text);
        button.setForeground(ChatbotColors.TEXT_COLOR);
        button.setBackground(ChatbotColors.BUTTON_BACKGROUND);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void setupButtonHoverEffect(JButton button) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(ChatbotColors.BUTTON_HOVER_BACKGROUND);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(ChatbotColors.BUTTON_BACKGROUND);
            }
        });
    }

    private void handleClearChat() {
        int option = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to clear the chat history?",
                "Clear Chat",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (option == JOptionPane.YES_OPTION) {
            chatManager.clearChat();
            sidebarPanel.clearHistory();
            chatMessageDAO.clearAllMessages()
                    .exceptionally(throwable -> {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(
                                    this,
                                    "Failed to clear chat history: " + throwable.getMessage(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        });
                        return null;
                    });
        }
    }

    private void handleSaveChat() {
        List<String> messages = sidebarPanel.getFullChatHistory();
        if (messages.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "No messages to save.",
                    "Save Chat",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        chatMessageDAO.saveAllMessages(messages)
                .thenRun(() -> {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(
                                this,
                                "Chat history saved successfully!",
                                "Save Chat",
                                JOptionPane.INFORMATION_MESSAGE);
                    });
                })
                .exceptionally(throwable -> {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(
                                this,
                                "Failed to save chat history: " + throwable.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    });
                    return null;
                });
    }
}