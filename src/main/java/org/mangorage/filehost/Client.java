package org.mangorage.filehost;

import org.mangorage.filehost.core.Scheduler;
import org.mangorage.filehost.networking.Side;
import org.mangorage.filehost.networking.packets.EchoPacket;
import org.mangorage.filehost.networking.packets.core.PacketResponse;
import org.mangorage.filehost.networking.packets.core.PacketHandler;
import org.mangorage.filehost.networking.packets.core.Packets;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import static org.mangorage.filehost.Server.PORT;

public class Client extends Thread {
    public static void main(String[] args) throws SocketException {
        Packets.init();
        new Client().start();
    }

    private final DatagramSocket client;
    private boolean running = true;
    private boolean stopping = false;

    public Client() throws SocketException {
        System.out.println("Starting Client");
        this.client = new DatagramSocket();
        client.setSoTimeout(5000);

        SocketAddress server = new InetSocketAddress("localhost", PORT);
        System.out.println("Client Started");
        System.out.println("Sending Packet");

        EchoPacket packet = new EchoPacket("Give me the file!");

        Packets.ECHO_PACKET.send(
                packet,
                Side.CLIENT,
                server,
                client
        );

        Scheduler.RUNNER.schedule(() -> {
            System.out.println("Closing Client");
            stopping = true;
        }, 10, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        while (running) {
            try {
                PacketResponse<?> response = PacketHandler.receivePacket(client);
                if (response != null) {
                    PacketHandler.handle(response.packet());
                    System.out.printf("Recieved Packet: %s%n", response.packet().getClass().getName());
                    System.out.printf("From Side: %s%n", response.sentFrom());
                    System.out.printf("Source: %s%n", response.source());
                }
            } catch (SocketTimeoutException timeoutException) {
                if (stopping)
                    running = false;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace(System.out);
            }
        }

        client.close();
        System.out.println("Stopped Client");
        System.exit(0);
    }
}
