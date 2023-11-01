package org.mangorage.filehost;

import org.mangorage.filehost.core.Scheduler;
import org.mangorage.filehost.networking.packets.core.PacketResponse;
import org.mangorage.filehost.networking.packets.core.PacketHandler;
import org.mangorage.filehost.networking.packets.core.Packets;
import org.opencv.core.Core;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

import static org.mangorage.filehost.core.Constants.PORT;

public class Server extends Thread {

    private static Server instance;

    public static DatagramSocket getInstance() {
        if (instance != null)
            return instance.server;
        return null;
    }

    public static void main(String[] args) throws SocketException {
        System.load("F:\\open\\opencv\\build\\java\\x64\\%s".formatted(Core.NATIVE_LIBRARY_NAME + ".dll"));
        Packets.init();
        instance = new Server();
        instance.start();
    }

    private final DatagramSocket server;
    private boolean running = true;
    private boolean stopping = false;

    public Server() throws SocketException {
        System.out.println("Starting Server Version 1.5");
        this.server = new DatagramSocket(PORT);
        System.out.println("Server Started");
    }

    @Override
    public void run() {
        while (!server.isClosed()) {
            try {
                ArrayList<DatagramPacket> PACKETS = new ArrayList<>();
                PacketResponse<?> response = PacketHandler.receivePacket(server, PACKETS);
                if (response != null) {
                    PacketHandler.handle(response.packet(), response.packetId(), response.source(), response.sentFrom());

                    System.out.printf("Received Packet: %s%n", response.packet().getClass().getName());
                    System.out.printf("From Side: %s%n", response.sentFrom());
                    System.out.printf("Source: %s%n", response.source());
                }
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }

            if (stopping)
                running = false;
        }
    }
}
