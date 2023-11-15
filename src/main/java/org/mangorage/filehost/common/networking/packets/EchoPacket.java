package org.mangorage.filehost.common.networking.packets;

import org.mangorage.filehost.common.core.buffer.SimpleByteBuf;
import org.mangorage.filehost.common.networking.Side;

import java.net.InetSocketAddress;

public class EchoPacket {
    private final String message;

    public EchoPacket(SimpleByteBuf data) {
        this(data.readString());
    }

    public EchoPacket(String message) {
        this.message = message;
    }

    public void handle(InetSocketAddress origin, Side side) {
        System.out.println("EchoPacket: " + message);
    }

    public void encode(SimpleByteBuf data) {
        data.writeString(message);
    }
}
