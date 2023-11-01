package org.mangorage.filehost;

import org.mangorage.filehost.core.Scheduler;
import org.mangorage.filehost.gui.Window;
import org.mangorage.filehost.networking.Side;
import org.mangorage.filehost.networking.packets.HandshakePacket;
import org.mangorage.filehost.networking.packets.PlaySoundPacket;
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
    private static Client instance;

    public static DatagramSocket getInstance() {
        if (instance != null)
            return instance.client;
        return null;
    }

    public static SocketAddress getServerInst() {
        if (instance != null)
            return instance.server;
        return null;
    }

    public static void main(String[] args) throws SocketException {
        Packets.init();
        instance = new Client("localhost");
        instance.start();
        Window.create();
    }

    private final SocketAddress server;
    private final DatagramSocket client;
    private boolean running = true;
    private boolean stopping = false;

    public Client(String IP) throws SocketException {
        System.out.println("Starting Client Version 1.5 to IP: %s".formatted(IP));
        this.client = new DatagramSocket();
        this.server = new InetSocketAddress(IP, PORT);

        Packets.HANDSHAKE_PACKET_PACKET.send(
                new HandshakePacket(),
                Side.CLIENT,
                server,
                client
        );

        Packets.PLAY_SOUND_PACKET_PACKET.send(
                new PlaySoundPacket("sound.wav"),
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
                if (response != null) {
                    PacketHandler.handle(response.packet(), response.packetId(), response.source(), response.sentFrom());
                    System.out.println("%s:%s".formatted(response.source().getHostString(), response.source().getHostName()));
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
