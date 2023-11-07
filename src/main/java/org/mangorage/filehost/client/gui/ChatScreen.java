package org.mangorage.filehost.client.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.function.Consumer;

public class ChatScreen extends JFrame {
    private JTextArea textArea;
    private JTextField messageField;

    public ChatScreen(Consumer<String> enterConsumer) {
        setTitle("Chat Screen");
        setSize(600, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textArea = new JTextArea(10, 40);
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        messageField = new JTextField(30);

        JLabel sendMessageLabel = new JLabel("Send Message:");

        messageField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                String message = messageField.getText();
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !message.isEmpty()) {
                    enterConsumer.accept(message);
                    messageField.setText(""); // Clear the input field
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        JPanel inputPanel = new JPanel();
        inputPanel.add(sendMessageLabel);
        inputPanel.add(messageField);

        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
    }

    public void addMessage(String message) {
        textArea.append(message + "\n");
    }

    public static ChatScreen create(Consumer<String> enterConsumer) {
        ChatScreen screen = new ChatScreen(enterConsumer);
        SwingUtilities.invokeLater(() -> screen.setVisible(true));
        return screen;
    }
}