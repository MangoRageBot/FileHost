package org.mangorage.filehost.server;

import org.mangorage.filehost.common.networking.NetworkingUtils;
import org.mangorage.filehost.common.networking.core.PacketHandler;
import org.mangorage.filehost.common.networking.Packets;
import org.mangorage.filehost.common.networking.packets.ChatMessagePacket;
import org.mangorage.filehost.common.core.Constants;
import org.mangorage.filehost.common.networking.core.IPacketSender;

import java.net.InetSocketAddress;
import java.util.HashMap;

public class ClientManager {
    private static final HashMap<String, ConnectedClient> CLIENTS = new HashMap<>();

    public static void setConnected(InetSocketAddress client, String username, String attemptedPassword) {
        if (attemptedPassword.equals(Constants.config.password())) {
            CLIENTS.put(NetworkingUtils.getIPString(client), new ConnectedClient(client, username));
            System.out.println("Connected Client!");
            sendPacketToAll(Server.getInstance(), Packets.CHAT_MESSAGE_PACKET, new ChatMessagePacket("System", "%s joined the Server!".formatted(username)));
        } else {
            System.out.println("Client attempted connection, yet they failed to put in correct password...");
        }
    }

    public static boolean isAuthenticated(InetSocketAddress client) {
        return CLIENTS.containsKey(NetworkingUtils.getIPString(client));
    }

    public static ConnectedClient getClient(InetSocketAddress client) {
        return CLIENTS.getOrDefault(NetworkingUtils.getIPString(client), null);
    }

    public static <T> void sendPacketToAll(IPacketSender sender, PacketHandler<T> packetHandler, T packet) { // Called from server only
        CLIENTS.values().stream()
                //.filter(s -> !NetworkingUtils.getIPString(s).equals(ExIP))
                .map(ConnectedClient::getAddress)
                .forEach(socket -> {
                    packetHandler.send(
                            packet,
                            sender,
                            socket
                    );
                });

    }

}
