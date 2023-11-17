package org.mangorage.filehost.server;


// Handles sending Packets to client

import io.netty.channel.Channel;
import org.mangorage.filehost.common.core.Constants;
import org.mangorage.filehost.common.networking.Side;
import org.mangorage.filehost.common.networking.core.PacketSender;
import org.mangorage.filehost.common.networking.core.Packet;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

// Can handle sending to multiple connections/channels
public class ServerPacketSender extends PacketSender {
    public ServerPacketSender(Side side) {
        super(side);
    }

    @Override
    protected Channel getActiveChannel(InetSocketAddress inetSocketAddress) {
        var client = ClientManager.getClient(inetSocketAddress);
        return client != null ? client.getActiveChannel() : null;
    }
}
