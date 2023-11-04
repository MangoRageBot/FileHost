package org.mangorage.filehost.networking.packets.core;

import org.mangorage.filehost.core.Constants;
import org.mangorage.filehost.networking.Side;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class PacketSender {
    public record Packet(DatagramPacket packet) {}

    private final Side side;
    private final DatagramSocket socket;
    private final LinkedList<Packet> packetsToSend = new LinkedList<>();

    public PacketSender(Side side, DatagramSocket socket) {
        this.side = side;
        this.socket = socket;
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                PacketSender.this.run();
            }
        }, 0, Constants.config.packetRate());
    }

    public Side getSide() {
        return side;
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public void send(DatagramPacket packet) throws IOException {
        packetsToSend.add(new Packet(packet));
    }


    public void run() {
        try {
            if (packetsToSend.isEmpty()) return;
            var packet = packetsToSend.poll();
            socket.send(packet.packet());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
