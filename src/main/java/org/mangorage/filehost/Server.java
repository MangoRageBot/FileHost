package org.mangorage.filehost;

import org.mangorage.filehost.core.Scheduler;
import org.mangorage.filehost.networking.Side;
import org.mangorage.filehost.networking.packets.core.PacketResponse;
import org.mangorage.filehost.networking.packets.core.PacketHandler;
import org.mangorage.filehost.networking.packets.core.Packets;
import org.mangorage.filehost.networking.packets.core.PacketSender;
import org.mangorage.filehost.networking.packets.main.EchoPacket;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

import static org.mangorage.filehost.core.Constants.PORT;

public class Server extends Thread {

    private static Server instance;

    public static PacketSender getInstance() {
        if (instance != null)
            return instance.sender;
        return null;
    }

    public static void create(int port) throws SocketException {
        if (instance != null) {
            System.out.println("Server already running!");
            return;
        }
        instance = new Server(port);
        instance.start();
    }

    public static void main(String[] args) throws SocketException {
        Packets.init();
        instance = new Server(PORT);
        instance.start();
    }

    private final DatagramSocket server;
    private final PacketSender sender;
    private boolean running = true;
    private boolean stopping = false;

    private Server(int port) throws SocketException {
        System.out.println("Starting Server Version 1.6");
        this.server = new DatagramSocket(port);
        sender = new PacketSender(Side.SERVER, server);

        System.out.println("Server Started");
    }

    @Override
    public void run() {
        while (!server.isClosed()) {
            try {
                PacketResponse<?> response = PacketHandler.receivePacket(server);
                if (response != null) {
                    PacketHandler.handle(response.packet(), response.packetId(), response.source(), response.sentFrom());

                    System.out.printf("Received Packet: %s%n", response.packet().getClass().getName());
                    System.out.printf("From Side: %s%n", response.sentFrom());
                    System.out.printf("Source: %s%n", response.source());

                    if (response.packet() instanceof EchoPacket) {
                        Packets.ECHO_PACKET.send(
                                new EchoPacket("ReceivSDDSSDSDSDDSSDed! Echo"),
                                sender,
                                response.source()
                        );
                    }
                }
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }

            if (stopping)
                running = false;
        }
    }
}
