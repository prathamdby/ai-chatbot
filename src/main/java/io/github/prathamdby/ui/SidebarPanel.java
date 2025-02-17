package io.github.prathamdby.ui;

import io.github.prathamdby.theme.ChatbotColors;
import io.github.prathamdby.ui.util.CustomScrollBarUI;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SidebarPanel extends JPanel {
    private JList<String> chatHistoryList;
    private DefaultListModel<String> chatHistoryModel;
    private List<String> fullChatHistory;

    public SidebarPanel() {
        setLayout(new BorderLayout());
        setBackground(ChatbotColors.SECONDARY_BACKGROUND);
        setPreferredSize(new Dimension(250, 0));
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(86, 88, 105)));

        setupHeader();
        setupChatHistoryList();
        fullChatHistory = new ArrayList<>();
    }

    private void setupHeader() {
        JLabel sidebarTitle = new JLabel("Chat History");
        sidebarTitle.setForeground(ChatbotColors.TEXT_COLOR);
        sidebarTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        sidebarTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(sidebarTitle, BorderLayout.NORTH);
    }

    private void setupChatHistoryList() {
        chatHistoryModel = new DefaultListModel<>();
        chatHistoryList = new JList<>(chatHistoryModel);
        chatHistoryList.setBackground(ChatbotColors.SECONDARY_BACKGROUND);
        chatHistoryList.setForeground(ChatbotColors.TEXT_COLOR);
        chatHistoryList.setSelectionBackground(new Color(66, 69, 73));
        chatHistoryList.setSelectionForeground(ChatbotColors.TEXT_COLOR);
        chatHistoryList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        chatHistoryList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JScrollPane historyScrollPane = new JScrollPane(chatHistoryList);
        historyScrollPane.setBackground(ChatbotColors.SECONDARY_BACKGROUND);
        historyScrollPane.setBorder(BorderFactory.createEmptyBorder());
        historyScrollPane.getViewport().setBackground(ChatbotColors.SECONDARY_BACKGROUND);

        // Customize scrollbars
        historyScrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        historyScrollPane.getHorizontalScrollBar().setUI(new CustomScrollBarUI());

        add(historyScrollPane, BorderLayout.CENTER);
    }

    public void addToHistory(String sender, String message) {
        String historyEntry = sender + ": " + (message.length() > 30 ? message.substring(0, 27) + "..." : message);
        chatHistoryModel.addElement(historyEntry);
        fullChatHistory.add(sender + ": " + message);
    }

    public void clearHistory() {
        chatHistoryModel.clear();
        fullChatHistory.clear();
    }

    public List<String> getFullChatHistory() {
        return fullChatHistory;
    }
}