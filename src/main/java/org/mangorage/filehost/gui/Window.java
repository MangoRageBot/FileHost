package org.mangorage.filehost.gui;

import javax.swing.*;

public class Window {
    public static void create() {
        // Create a JFrame (window)
        JFrame frame = new JFrame("Simple Window");

        // Set the size of the window
        frame.setSize(1280, 720);

        // Set the default close operation
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(BasicFrame.INSTANCE);
        frame.addKeyListener(BasicFrame.INSTANCE);

        // Make the window visible
        frame.setVisible(true);
    }
}
