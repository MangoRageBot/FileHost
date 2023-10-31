package org.mangorage.filehost;

import org.mangorage.filehost.networking.Side;
import org.mangorage.filehost.networking.packets.BasicPacketHandler;
import org.mangorage.filehost.networking.packets.EchoPacket;
import org.mangorage.filehost.networking.packets.IPacket;
import org.mangorage.filehost.networking.packets.PacketResponse;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Server extends Thread {
    public static final int PORT = 25565;

    public static void main(String[] args) {
        try {
            new Server().start();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }



    private boolean running = true;
    private final DatagramSocket server;

    public Server() throws SocketException {
        System.out.println("Starting Server");
        this.server = new DatagramSocket(PORT);
        System.out.println("Server Started");
    }

    @Override
    public void run() {
        IPacket sendBack = new EchoPacket("Welcome home client!");
        while (running) {
            PacketResponse response = BasicPacketHandler.recieve(server);
            if (response != null) {
                response.packet().handle();

                System.out.println("Recieved Packet: %s".formatted(response.packet().getType().getName()));
                System.out.println("From Side: %s".formatted(response.sentFrom()));
                System.out.println("Source: %s".formatted(response.source()));
                System.out.println("Sending back!");

                BasicPacketHandler.sendPacket(
                        sendBack,
                        Side.SERVER,
                        response.source(),
                        server
                );
            }
        }
    }
}
