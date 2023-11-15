package org.mangorage.filehost.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.mangorage.filehost.common.core.Constants;
import org.mangorage.filehost.common.core.Scheduler;
import org.mangorage.filehost.common.networking.Side;
import org.mangorage.filehost.common.networking.core.PacketResponse;
import org.mangorage.filehost.common.networking.core.PacketHandler;
import org.mangorage.filehost.common.networking.Packets;
import org.mangorage.filehost.common.networking.core.IPacketSender;

import java.net.SocketException;

import static org.mangorage.filehost.common.core.Constants.PORT;

public class Server extends Thread {
    private static Server instance;

    public static IPacketSender getInstance() {
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
        Constants.init();
        Packets.init();
        instance = new Server(PORT);
        instance.start();
    }

    private final int port;
    private IPacketSender sender = new ServerPacketSender(Side.SERVER);

    private Server(int port) {
        System.out.println("Starting Server Version 1.6");
        this.port = port;
    }

    @Override
    public void run() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        public void initChannel(final Channel ch) throws Exception {
                            ch.pipeline().addLast(new SimpleChannelInboundHandler<DatagramPacket>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
                                    PacketResponse<?> response = PacketHandler.receivePacket(packet);
                                    if (response != null) {
                                        Scheduler.RUNNER.execute(() -> {
                                            PacketHandler.handle(response.packet(), response.packetId(), response.source(), response.sentFrom());

                                            var client = ClientManager.getClient(response.source());
                                            client.updateChannel(ctx.channel()); // Updating this connection...

                                            System.out.printf("Received Packet: %s%n", response.packet().getClass().getName());
                                            System.out.printf("From Side: %s%n", response.sentFrom());
                                            System.out.printf("Source: %s%n", response.source());
                                        });
                                    }
                                }
                            });

                            System.out.println("Server Started");
                        }
                    });


            b.bind(port).sync().channel().closeFuture().await();
        } catch (Exception e) {

        } finally {
            group.shutdownGracefully();
        }
    }
}
