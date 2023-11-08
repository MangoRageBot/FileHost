package org.mangorage.filehost.client;

import org.mangorage.filehost.common.core.Constants;
import org.mangorage.filehost.common.core.Scheduler;
import org.mangorage.filehost.client.gui.ChatScreen;
import org.mangorage.filehost.common.networking.Side;
import org.mangorage.filehost.common.networking.core.PacketSender;
import org.mangorage.filehost.common.networking.core.PacketResponse;
import org.mangorage.filehost.common.networking.core.PacketHandler;
import org.mangorage.filehost.common.networking.Packets;
import org.mangorage.filehost.common.networking.packets.ChatMessagePacket;
import org.mangorage.filehost.common.networking.packets.HandshakePacket;
import org.mangorage.filehost.common.networking.packets.PingPacket;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class Client extends Thread {
    private static Client instance;

    public static Client getInstance() {
        return instance;
    }
    public static PacketSender getSender() {
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
        // 23.26.60.28:14126
        instance = new Client("localhost:25565", "Developer", Constants.config.password());
        instance.start();
    }

    public static void create(String IP, String username, String password) throws SocketException {
        if (instance != null) {
            System.out.println("Server already running!");
            return;
        }
        instance = new Client(IP, username, password);
        instance.start();
    }

    private final SocketAddress server;
    private final DatagramSocket client;
    private final PacketSender sender;
    private final String username;
    private final ChatScreen chatScreen;

    private boolean running = true;
    private boolean stopping = false;

    public Client(String IP, String username, String password) throws SocketException {
        System.out.println("Starting Client Version 1.6 to IP: %s".formatted(IP));
        String[] ipArr = IP.split(":");

        this.client = new DatagramSocket();
        this.server = new InetSocketAddress(ipArr[0], Integer.parseInt(ipArr[1]));
        this.sender = new PacketSender(Side.CLIENT, client);
        this.username = username;

        this.chatScreen = ChatScreen.create(message -> {
            // Send Chat Packet to server...
            Packets.CHAT_MESSAGE_PACKET.send(
                    new ChatMessagePacket(message),
                    sender,
                    server
            );
        });

        Packets.HANDSHAKE_PACKET.send(
                new HandshakePacket(username, password),
                sender,
                server
        );

        Scheduler.RUNNER.scheduleAtFixedRate(
                () -> Packets.PING_PACKET.send(new PingPacket(), sender, server),
                0,
                5,
                TimeUnit.SECONDS
        );
    }

    public void addMessage(String message) {
        chatScreen.addMessage(message);
    }

    @Override
    public void run() {
        while (running) {
            try {
                PacketResponse<?> response = PacketHandler.receivePacket(client);
                if (response != null) {
                    Scheduler.RUNNER.execute(() -> {
                        PacketHandler.handle(response.packet(), response.packetId(), response.source(), response.sentFrom());

                        System.out.printf("Received Packet: %s%n", response.packet().getClass().getName());
                        System.out.printf("From Side: %s%n", response.sentFrom());
                        System.out.printf("Source: %s%n", response.source());
                    });
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
