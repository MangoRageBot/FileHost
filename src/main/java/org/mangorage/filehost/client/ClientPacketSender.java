package org.mangorage.filehost.client;


import io.netty.channel.Channel;
import org.mangorage.filehost.common.core.Constants;
import org.mangorage.filehost.common.networking.Side;
import org.mangorage.filehost.common.networking.core.Packet;
import org.mangorage.filehost.common.networking.core.IPacketSender;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

// Handles sending packets to one connection (AKA the Server)
public class ClientPacketSender implements IPacketSender {
    private final Side side;
    private final Supplier<Channel> channel;
    private final LinkedList<Packet> packetsToSend = new LinkedList<>();

    public ClientPacketSender(Side side, AtomicReference<Channel> channel) {
        this.side = side;
        this.channel = channel::get;
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ClientPacketSender.this.run();
            }
        }, 0, Constants.config.packetRate());
    }

    public Side getSide() {
        return side;
    }


    public void send(Packet packet)  {
        packetsToSend.add(packet);
    }

    public void run() {
        try {
            if (packetsToSend.isEmpty()) return;
            var packet = packetsToSend.poll();
            var channel = ClientPacketSender.this.channel.get();
            if (channel != null && channel.isOpen()) {
                channel.writeAndFlush(packet.packet()).sync();
                System.out.println("Sent Packet (Size: %s Bytes): %s".formatted(packet.packet().content().readableBytes(), packet.packetClass().getName()));
            }
        } catch (Exception e) {
            System.out.println("Packet failed to send properly...");
            throw new RuntimeException(e);
        }
    }
}
