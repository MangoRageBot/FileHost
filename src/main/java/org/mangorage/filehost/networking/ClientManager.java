package org.mangorage.filehost.networking;

import org.mangorage.filehost.networking.packets.core.PacketHandler;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.ArrayList;

public class ClientManager {
    private static final ArrayList<InetSocketAddress> connected = new ArrayList<>();

    public static void setConnected(InetSocketAddress client) {
        connected.add(client);
        System.out.println("Connected Client!");
    }

    public static <T> void sendPacketToAll(DatagramSocket server, PacketHandler<T> packetHandler, T packet) { // Called from server only
        connected.stream()
                //.filter(s -> !NetworkingUtils.getIPString(s).equals(ExIP))
                .forEach(socket -> {
                    packetHandler.send(
                            packet,
                            Side.SERVER,
                            socket,
                            server
                    );
                });

    }

}
