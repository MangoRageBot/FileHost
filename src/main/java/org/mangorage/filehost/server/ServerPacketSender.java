package org.mangorage.filehost.server;


// Handles sending Packets to client

import io.netty.channel.Channel;
import org.mangorage.filehost.common.core.Constants;
import org.mangorage.filehost.common.networking.Side;
import org.mangorage.filehost.common.networking.core.IPacketSender;
import org.mangorage.filehost.common.networking.core.Packet;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

// Can handle sending to multiple connections/channels
public class ServerPacketSender implements IPacketSender {
    private final Side side;
    private final LinkedList<Packet> packetsToSend = new LinkedList<>();

    public ServerPacketSender(Side side) {
        this.side = side;
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ServerPacketSender.this.run();
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

            var clientToSendDataTo = ClientManager.getClient(packet.packet().recipient());
            if (clientToSendDataTo != null) {
                Channel channel = clientToSendDataTo.getActiveChannel();
                if (channel.isActive()) {
                    channel.writeAndFlush(packet.packet()).sync();
                    System.out.println("Sending Packet %s to %s with size of %s bytes".formatted(packet.packetName(), getSide(), packet.packet().content().readableBytes()));
                }
            }

        } catch (Exception e) {
            System.out.println("Packet failed to send properly...");
            throw new RuntimeException(e);
        }
    }
}
