package org.mangorage.filehost;

import org.mangorage.filehost.core.Scheduler;
import org.mangorage.filehost.networking.Side;
import org.mangorage.filehost.networking.packets.EchoPacket;
import org.mangorage.filehost.networking.packets.FileTransferPacket;
import org.mangorage.filehost.networking.packets.core.PacketResponse;
import org.mangorage.filehost.networking.packets.core.PacketHandler;
import org.mangorage.filehost.networking.packets.core.Packets;

import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Server extends Thread {
    public static final int PORT = 25565;

    public static void main(String[] args) {
        try {
            Packets.init();
            new Server().start();
        } catch (SocketException ignored) {}
    }

    private final DatagramSocket server;
    private boolean running = true;
    private boolean stopping = false;

    public Server() throws SocketException {
        System.out.println("Starting Server");
        this.server = new DatagramSocket(PORT);
        System.out.println("Server Started");
    }

    @Override
    public void run() {
        FileTransferPacket packet = new FileTransferPacket(new File("TEST.txt"), "downloaded.txt");
        EchoPacket echoPacket = new EchoPacket("Hello World FROM SERVER!");
        while (!server.isClosed()) {
            try {
                ArrayList<DatagramPacket> PACKETS = new ArrayList<>();
                PacketResponse<?> response = PacketHandler.receivePacket(server, PACKETS);
                if (response != null) {
                    PacketHandler.handle(response.packet());
                    System.out.printf("Received Packet: %s%n", response.packet().getClass().getName());
                    System.out.printf("From Side: %s%n", response.sentFrom());
                    System.out.printf("Source: %s%n", response.source());
                    System.out.println("Sending back!");

                    Packets.FILE_TRANSFER_PACKET.send(
                            packet,
                            Side.SERVER,
                            response.source(),
                            server
                    );
                    Scheduler.RUNNER.schedule(() -> {
                        Packets.ECHO_PACKET.send(
                                echoPacket,
                                Side.SERVER,
                                response.source(),
                                server
                        );
                    }, 5, TimeUnit.SECONDS);
                }
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }

            if (stopping)
                running = false;
        }
    }
}
