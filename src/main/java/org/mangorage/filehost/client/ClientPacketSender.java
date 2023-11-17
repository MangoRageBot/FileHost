package org.mangorage.filehost.client;


import io.netty.channel.Channel;
import org.mangorage.filehost.common.core.Constants;
import org.mangorage.filehost.common.networking.Side;
import org.mangorage.filehost.common.networking.core.Packet;
import org.mangorage.filehost.common.networking.core.PacketSender;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

// Handles sending packets to one connection (AKA the Server)
public class ClientPacketSender extends PacketSender {
    private final Supplier<Channel> channel;

    public ClientPacketSender(Side side, AtomicReference<Channel> channel) {
        super(side);
        this.channel = channel::get;
    }

    @Override
    protected Channel getActiveChannel(InetSocketAddress inetSocketAddress) {
        return channel.get();
    }
}
