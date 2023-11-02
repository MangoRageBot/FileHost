package org.mangorage.filehost.networking;

import org.mangorage.filehost.networking.packets.core.PacketHandler;
import org.mangorage.filehost.networking.packets.core.RatelimitedPacketSender;

import java.net.InetSocketAddress;
import java.util.HashMap;

public class ClientManager {
    private static final HashMap<String, ConnectedClient> CLIENTS = new HashMap<>();

    public static void setConnected(InetSocketAddress client) {
        CLIENTS.put(NetworkingUtils.getIPString(client), new ConnectedClient(client));
        System.out.println("Connected Client!");
    }

    public static <T> void sendPacketToAll(RatelimitedPacketSender sender, PacketHandler<T> packetHandler, T packet) { // Called from server only
        CLIENTS.values().stream()
                //.filter(s -> !NetworkingUtils.getIPString(s).equals(ExIP))
                .map(ConnectedClient::getAddress)
                .forEach(socket -> {
                    packetHandler.send(
                            packet,
                            sender,
                            socket
                    );
                    System.out.println("Sending Sounnd to -> %s".formatted(
                            NetworkingUtils.getIPString(socket)
                    ));
                });

    }

}
