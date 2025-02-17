package io.github.prathamdby.ui;

import io.github.prathamdby.theme.ChatbotColors;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class InputPanel extends JPanel {
    private JTextField inputField;
    private JButton sendButton;
    private JLabel statusLabel;

    public InputPanel(ActionListener sendListener) {
        setLayout(new BorderLayout(10, 0));
        setBackground(ChatbotColors.SECONDARY_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        setupInputComponents(sendListener);
    }

    private void setupInputComponents(ActionListener sendListener) {
        inputField = new JTextField();
        inputField.setBackground(ChatbotColors.INPUT_BACKGROUND);
        inputField.setForeground(ChatbotColors.TEXT_COLOR);
        inputField.setCaretColor(ChatbotColors.TEXT_COLOR);
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(86, 88, 105)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inputField.addActionListener(sendListener);

        sendButton = new JButton("Send");
        sendButton.setBackground(ChatbotColors.BUTTON_BACKGROUND);
        sendButton.setForeground(ChatbotColors.BUTTON_TEXT_COLOR);
        sendButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        sendButton.setFocusPainted(false);
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sendButton.setOpaque(true);
        sendButton.setBorderPainted(false);
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendButton.addActionListener(sendListener);

        // Add hover effect to send button
        sendButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                sendButton.setBackground(ChatbotColors.BUTTON_HOVER_BACKGROUND);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                sendButton.setBackground(ChatbotColors.BUTTON_BACKGROUND);
            }
        });

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.CENTER);

        statusLabel = new JLabel("AI is ready");
        statusLabel.setForeground(ChatbotColors.TEXT_COLOR);
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        add(statusLabel, BorderLayout.SOUTH);
    }

    public String getInputText() {
        return inputField.getText();
    }

    public void clearInput() {
        inputField.setText("");
    }

    public void setStatusText(String text) {
        statusLabel.setText(text);
    }

    public void focusInput() {
        inputField.requestFocusInWindow();
    }
}