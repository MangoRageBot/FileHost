package org.mangorage.filehost.common.networking.core;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import org.mangorage.filehost.common.core.Constants;
import org.mangorage.filehost.common.networking.Side;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public abstract class PacketSender {
    private final LinkedList<Packet> queuedPackets = new LinkedList<>();
    private final Side side;

    public PacketSender(Side side) {
        this.side = side;
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                PacketSender.this.sendNextPacket();
            }
        }, 0, Constants.config.packetRate());
    }

    public Side getSide() {
        return side;
    }

    public void send(Packet packet) {
        queuedPackets.add(packet);
    }

    protected LinkedList<Packet> getQueuedPackets() {
        return queuedPackets;
    }

    protected abstract Channel getActiveChannel(InetSocketAddress inetSocketAddress);


    public void sendNextPacket() {
        try {
            var packets = getQueuedPackets();
            if (packets.isEmpty()) return;
            var packet = packets.poll();

            var channel = getActiveChannel(packet.packet().recipient());
            if (channel != null && channel.isActive()) {
                channel.writeAndFlush(packet.packet()).sync();
                System.out.println("Sending Packet %s to %s with size of %s bytes".formatted(packet.packetName(), getSide(), packet.packet().content().readableBytes()));
            }
        } catch (Exception e) {
            System.out.println("Packet failed to send properly...");
            throw new RuntimeException(e);
        }
    }
}
