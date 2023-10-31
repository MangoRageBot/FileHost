package org.mangorage.filehost;

import org.mangorage.filehost.networking.Side;
import org.mangorage.filehost.networking.packets.BasicPacketHandler;
import org.mangorage.filehost.networking.packets.EchoPacket;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;

import static org.mangorage.filehost.Server.PORT;

public class Client {
    public static void main(String[] args) throws SocketException {
        new Client();
    }

    private final DatagramSocket client;
    public Client() throws SocketException {
        System.out.println("Starting Client");
        this.client = new DatagramSocket();
        SocketAddress server = new InetSocketAddress("localhost", PORT);
        System.out.println("Client Started");

        System.out.println("Sending Packet");
        EchoPacket packet = new EchoPacket("Hello World!");
        for (int i = 0; i < 10; i++) {
            BasicPacketHandler.sendPacket(
                    packet,
                    Side.CLIENT,
                    server,
                    client
            );
        }
        client.close();
    }
}
