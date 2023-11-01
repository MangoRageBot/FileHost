package org.mangorage.filehost;

import org.mangorage.filehost.core.Scheduler;
import org.mangorage.filehost.gui.Window;
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
import static org.mangorage.filehost.core.Constants.PORT;

public class Client extends Thread {
    public static void main(String[] args) throws SocketException {
        Packets.init();
        new Client("localhost").start();
        Window.create();
    }

    private final SocketAddress server;
    private final DatagramSocket client;
    private boolean running = true;
    private boolean stopping = false;

    public Client(String IP) throws SocketException {
        System.out.println("Starting Client Version 1.3 to IP: %s".formatted(IP));
        this.client = new DatagramSocket();
        this.server = new InetSocketAddress(IP, PORT);


        EchoPacket packet = new EchoPacket("Gimmie Video!");
        Packets.ECHO_PACKET.send(
                packet,
                Side.CLIENT,
                server,
                client
        );
    }

    @Override
    public void run() {
        while (running) {
            try {
                PacketResponse<?> response = PacketHandler.receivePacket(client);
                if (response != null)
                    Scheduler.RUNNER.execute(() -> PacketHandler.handle(response.packet(), response.packetId()));

            } catch (SocketTimeoutException timeoutException) {
                if (stopping)
                    running = false;
                timeoutException.printStackTrace(System.out);
            } catch (IOException | ClassNotFoundException e) {
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
