package org.mangorage.filehost;

import org.mangorage.filehost.core.Scheduler;
import org.mangorage.filehost.core.VideoProcessor;
import org.mangorage.filehost.networking.Side;
import org.mangorage.filehost.networking.packets.EchoPacket;
import org.mangorage.filehost.networking.packets.core.PacketResponse;
import org.mangorage.filehost.networking.packets.core.PacketHandler;
import org.mangorage.filehost.networking.packets.core.Packets;
import org.opencv.core.Core;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static org.mangorage.filehost.core.Constants.PORT;

public class Server extends Thread {

    public static void main(String[] args) throws SocketException {
        System.load("F:\\open\\opencv\\build\\java\\x64\\%s".formatted(Core.NATIVE_LIBRARY_NAME + ".dll"));
        Packets.init();
        new Server().start();
    }

    private final DatagramSocket server;
    private boolean running = true;
    private boolean stopping = false;

    public Server() throws SocketException {
        System.out.println("Starting Server Version 1.3");
        this.server = new DatagramSocket(PORT);
        System.out.println("Server Started");
    }

    @Override
    public void run() {
        EchoPacket echoPacket = new EchoPacket("Hello World FROM SERVER!");
        while (!server.isClosed()) {
            try {
                ArrayList<DatagramPacket> PACKETS = new ArrayList<>();
                PacketResponse<?> response = PacketHandler.receivePacket(server, PACKETS);
                if (response != null) {
                    Scheduler.RUNNER.execute(() -> PacketHandler.handle(response.packet(), response.packetId()));

                    System.out.printf("Received Packet: %s%n", response.packet().getClass().getName());
                    System.out.printf("From Side: %s%n", response.sentFrom());
                    System.out.printf("Source: %s%n", response.source());
                    System.out.println("Sending back!");

                    Scheduler.RUNNER.schedule(() -> {
                        System.out.println("Sending Video to client!");
                        Packets.ECHO_PACKET.send(
                                echoPacket,
                                Side.SERVER,
                                response.source(),
                                server
                        );

                        VideoProcessor.processWithAudio("video4.mp4", a -> {
                            Packets.VIDEO_FRAME_PACKET.send(
                                    a,
                                    Side.SERVER,
                                    response.source(),
                                    server
                            );
                        }, b -> {
                            Packets.AUDIO_FRAME_PACKET_PACKET.send(
                                    b,
                                    Side.SERVER,
                                    response.source(),
                                    server
                            );
                        });
                    }, 10, TimeUnit.SECONDS);
                }
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }

            if (stopping)
                running = false;
        }
    }
}
