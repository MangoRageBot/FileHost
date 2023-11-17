package org.mangorage.filehost.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.jetbrains.annotations.NotNull;
import org.mangorage.filehost.common.core.Constants;
import org.mangorage.filehost.common.core.Scheduler;
import org.mangorage.filehost.client.gui.ChatScreen;
import org.mangorage.filehost.common.networking.Side;
import org.mangorage.filehost.common.networking.core.EmptyPacket;
import org.mangorage.filehost.common.networking.core.PacketSender;
import org.mangorage.filehost.common.networking.core.PacketResponse;
import org.mangorage.filehost.common.networking.core.PacketHandler;
import org.mangorage.filehost.common.networking.Packets;
import org.mangorage.filehost.common.networking.packets.ChatMessagePacket;
import org.mangorage.filehost.common.networking.packets.HandshakePacket;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

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
        instance = new Client("localhost:25564", "Developer", Constants.config.password());
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

    private final InetSocketAddress server;
    private final DatagramSocket client;
    private final String username;
    private final String password;


    private final AtomicReference<Channel> channel = new AtomicReference<>();
    private final PacketSender sender = new ClientPacketSender(Side.CLIENT, channel);
    private ChatScreen chatScreen;

    private boolean running = true;
    private boolean stopping = false;

    public Client(String IP, String username, String password) throws SocketException {
        System.out.println("Starting Client Version 1.6 to IP: %s".formatted(IP));
        String[] ipArr = IP.split(":");

        this.client = new DatagramSocket();
        this.server = new InetSocketAddress(ipArr[0], Integer.parseInt(ipArr[1]));
        this.username = username;
        this.password = password;

        /**

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
         **/
    }

    public void addMessage(String message) {
        chatScreen.addMessage(message);
    }

    @Override
    public void run() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioDatagramChannel.class)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        public void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(new SimpleChannelInboundHandler<DatagramPacket>() {

                                @Override
                                public void channelActive(@NotNull ChannelHandlerContext ctx) throws Exception {
                                    Client.this.channel.set(ctx.channel());
                                }

                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
                                    PacketResponse<?> response = PacketHandler.receivePacket(packet);
                                    if (response != null) {
                                        Scheduler.RUNNER.execute(() -> {
                                            PacketHandler.handle(response.packet(), response.packetId(), response.source(), response.sentFrom());

                                            Client.this.channel.set(ctx.channel());

                                            System.out.printf("Received Packet: %s%n", response.packetName());
                                            System.out.printf("From Side: %s%n", response.sentFrom());
                                            System.out.printf("Source: %s%n", response.source());
                                        });
                                    }
                                }
                            });

                            Client.this.channel.set(ch);

                            Client.this.chatScreen = ChatScreen.create(message -> {
                                // Send Chat Packet to server...
                                Packets.CHAT_MESSAGE_PACKET.send(
                                        new ChatMessagePacket(message),
                                        sender,
                                        server
                                );
                            });

                            Packets.HANDSHAKE_PACKET.send(
                                    new HandshakePacket(username, Client.this.password),
                                    sender,
                                    server
                            );

                            Scheduler.RUNNER.scheduleAtFixedRate(
                                    () -> Packets.PING_PACKET.send(EmptyPacket.INSTANCE, sender, server),
                                    0,
                                    5,
                                    TimeUnit.SECONDS
                            );

                            System.out.println("Client Started...");
                        }
                    });

            b.bind(0).sync().channel().closeFuture().await();
        } catch (Exception e) {

        } finally {
            group.shutdownGracefully();
        }
    }

    public void stopClient() {
        this.stopping = true;
    }

    public SocketAddress getServer() {
        return server;
    }
}
