package org.mangorage.filehost;

import org.mangorage.filehost.core.Constants;
import org.mangorage.filehost.networking.Side;
import org.mangorage.filehost.networking.packets.core.RatelimitedPacketSender;
import org.mangorage.filehost.networking.packets.main.HandshakePacket;
import org.mangorage.filehost.networking.packets.core.PacketResponse;
import org.mangorage.filehost.networking.packets.core.PacketHandler;
import org.mangorage.filehost.networking.packets.core.Packets;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class Client extends Thread {
    private static Client instance;

    public static RatelimitedPacketSender getInstance() {
        if (instance != null)
            return instance.sender;
        return null;
    }

    public static SocketAddress getServerInst() {
        if (instance != null)
            return instance.server;
        return null;
    }

    public static void main(String[] args) throws SocketException {
        Constants.init();
        Packets.init();
        instance = new Client("23.26.60.28:14126");
        instance.start();
    }

    public static void create(String IP) throws SocketException {
        if (instance != null) {
            System.out.println("Server already running!");
            return;
        }
        instance = new Client(IP);
        instance.start();
    }

    private final SocketAddress server;
    private final DatagramSocket client;

    private final RatelimitedPacketSender sender;
    private boolean running = true;
    private boolean stopping = false;

    public Client(String IP) throws SocketException {
        System.out.println("Starting Client Version 1.5 to IP: %s".formatted(IP));
        String[] ipArr = IP.split(":");
        this.client = new DatagramSocket();
        this.server = new InetSocketAddress(ipArr[0], Integer.parseInt(ipArr[1]));
        this.sender = new RatelimitedPacketSender(Side.CLIENT, client);

        Packets.HANDSHAKE_PACKET_PACKET.send(
                new HandshakePacket(),
                sender,
                server
        );
    }

    @Override
    public void run() {
        while (running) {
            try {
                PacketResponse<?> response = PacketHandler.receivePacket(client);
                if (response != null) {
                    System.out.println(response.packetId());
                    PacketHandler.handle(response.packet(), response.packetId(), response.source(), response.sentFrom());
                }
            } catch (SocketTimeoutException timeoutException) {
                if (stopping)
                    running = false;
                timeoutException.printStackTrace(System.out);
            } catch (IOException e) {
                e.printStackTrace(System.out);
            }
        }

        client.close();
        System.out.println("Stopped Client");
        System.exit(0);
    }

    public void stopClient() {
        this.stopping = true;
    }

    public SocketAddress getServer() {
        return server;
    }
}
