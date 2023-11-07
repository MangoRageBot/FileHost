package org.mangorage.filehost.common.networking.core;

import org.mangorage.filehost.common.networking.Side;
import org.mangorage.filehost.common.core.Constants;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class PacketSender {
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

    public void send(Packet packet)  {
        packetsToSend.add(packet);
    }

    public void run() {
        try {
            if (packetsToSend.isEmpty()) return;
            var packet = packetsToSend.poll();
            socket.send(packet.packet());
            System.out.println("Sent Packet (Size: %s Bytes): %s".formatted(packet.packet().getLength(), packet.packetClass().getName()));
        } catch (IOException e) {
            System.out.println("Packet failed to send properly...");
            throw new RuntimeException(e);
        }
    }
}
