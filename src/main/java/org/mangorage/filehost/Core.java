package org.mangorage.filehost;

import org.mangorage.filehost.core.Constants;
import org.mangorage.filehost.gui.RegexDocumentFilter;
import org.mangorage.filehost.networking.packets.core.Packets;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.net.SocketException;

public class Core {
    public static void main(String[] args) throws SocketException {
        Constants.init();
        Packets.init();

        if (args.length == 0)
            args = new String[] {"client"};

        String type = args[0];
        String ip = "localhost";
        if (args.length > 1)
            ip = args[1];

        if (ip.equals("localhost") && type.equalsIgnoreCase("client")) {
            JFrame frame = new JFrame("Three Input Fields Dialog");

            // Create a panel to organize the input fields
            JPanel panel = new JPanel(new GridLayout(3, 2));

            // Create input fields and labels
            JTextField serverIP = new JTextField(20);
            JTextField serverPort = new JTextField(20);
            JTextField userName = new JTextField(20);

            serverIP.setText("localhost");
            serverPort.setText(Constants.PORT + "");

            ((AbstractDocument)serverIP.getDocument()).setDocumentFilter(new RegexDocumentFilter(RegexDocumentFilter.IPV4_PARTIAL_PATTERN));
            ((AbstractDocument)serverPort.getDocument()).setDocumentFilter(new RegexDocumentFilter(RegexDocumentFilter.NUMBERS_PATTERN, 5));
            ((AbstractDocument)userName.getDocument()).setDocumentFilter(new RegexDocumentFilter(RegexDocumentFilter.USERNAME_PATTERN, 16));

            JLabel serverIpLabel = new JLabel("Server IP:");
            JLabel serverPortlabel = new JLabel("Server Port:");
            JLabel usernameLabel = new JLabel("Username:");

            // Add input fields and labels to the panel
            panel.add(serverIpLabel);
            panel.add(serverIP);
            panel.add(serverPortlabel);
            panel.add(serverPort);
            panel.add(usernameLabel);
            panel.add(userName);

            // Create an option pane with the panel
            int result = JOptionPane.showConfirmDialog(null, panel, "Enter three values:", JOptionPane.OK_CANCEL_OPTION);

            // Check if the user clicked "OK"
            if (result == JOptionPane.OK_OPTION) {
                String IP = serverIP.getText();
                String PORT = serverPort.getText();
                String USERNAME = userName.getText();

                Client.create("%s:%s".formatted(IP, PORT));
            }
            frame.pack();
        } else if (type.equalsIgnoreCase("server")) {
            Server.create(Integer.parseInt(ip));
        }
    }
}
