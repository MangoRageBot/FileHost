package org.mangorage.filehost.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

public class ChatScreen extends JFrame {
    private JTextArea textArea;
    private JTextField messageField;

    public ChatScreen(Consumer<String> enterConsumer) {
        setTitle("Chat Screen");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textArea = new JTextArea(10, 40);
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        messageField = new JTextField(30);

        JButton sendMessageButton = new JButton("Send Message");
        sendMessageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = messageField.getText();
                if (!message.isEmpty()) {
                    enterConsumer.accept(message);
                    messageField.setText(""); // Clear the input field
                }
            }
        });

        JPanel inputPanel = new JPanel();
        inputPanel.add(sendMessageButton);
        inputPanel.add(messageField);

        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
    }

    public void addMessage(String message) {
        textArea.append(message + "\n");
    }

    public static ChatScreen create(Consumer<String> enterConsumer) {
        ChatScreen screen = new ChatScreen(enterConsumer);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                screen.setVisible(true);
            }
        });

        return screen;
    }
}