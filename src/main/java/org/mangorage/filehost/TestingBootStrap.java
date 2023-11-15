package org.mangorage.filehost;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

public class TestingBootStrap {
    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> Server.main(args)).start();
        Thread.sleep(5000);
        new Thread(() -> Client.main(args)).start();
    }

    public static class Server {
        public static void main(String[] args) {
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
                                        String msg = packet.content().toString(CharsetUtil.UTF_8);
                                        System.out.println("Server received: " + msg);
                                        var data = Unpooled.copiedBuffer("Hellob", CharsetUtil.UTF_8);
                                        ch.writeAndFlush(new DatagramPacket(data, packet.sender())).sync();
                                        if ("quit".equals(msg)) {
                                            ctx.close();
                                        }
                                    }
                                });
                                System.out.println("Server Started");
                            }
                        });


                b.bind(8888).sync().channel().closeFuture().await();
            } catch (Exception e) {

            } finally {
                group.shutdownGracefully();
            }
        }
    }


    public static class Client {
        public static void main(String[] args) {
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
                                    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
                                        String response = packet.content().toString(CharsetUtil.UTF_8);

                                        if (response.startsWith("Hellob")) {
                                            System.out.println("Client received: " + response);
                                            ctx.close();
                                        }
                                    }
                                });
                            }
                        });

                Channel ch = b.bind(0).sync().channel();

                var data = Unpooled.copiedBuffer("Hello", CharsetUtil.UTF_8);


                ch.writeAndFlush(new DatagramPacket(data, new InetSocketAddress("127.0.0.1", 8888))).sync();

                if (!ch.closeFuture().await(15000)) {
                    System.err.println("Request timed out.");
                }
            } catch (Exception e) {

            } finally {
                group.shutdownGracefully();
            }
        }
    }
}
