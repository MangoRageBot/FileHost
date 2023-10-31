package org.mangorage.filehost;

import org.mangorage.filehost.core.Scheduler;
import org.mangorage.filehost.networking.Side;
import org.mangorage.filehost.networking.packets.BasicPacketHandler;
import org.mangorage.filehost.networking.packets.EchoPacket;
import org.mangorage.filehost.networking.packets.PacketResponse;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

import static org.mangorage.filehost.Server.PORT;

public class Client extends Thread {
    public static void main(String[] args) throws SocketException {
        new Client().start();
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

        Scheduler.RUNNER.schedule(() -> {
            System.out.println("Closing Client");
            client.close();
        }, 10, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        while (!client.isClosed()) {
            PacketResponse response = BasicPacketHandler.recieve(client);
            if (response != null) {
                response.packet().handle();
                System.out.println("Recieved Packet: %s".formatted(response.packet().getType().getName()));
                System.out.println("From Side: %s".formatted(response.sentFrom()));
                System.out.println("Source: %s".formatted(response.source()));
            }
        }
        System.out.println("Stopped Client");
        System.exit(0);
    }
}
