package org.mangorage.filehost.common.networking.packets;

import org.mangorage.filehost.common.networking.Side;
import org.mangorage.filehost.common.core.buffer.SimpleByteBuffer;

import java.net.InetSocketAddress;

public class EchoPacket {
    private final String message;

    public EchoPacket(SimpleByteBuffer data) {
        this(data.readString());
    }

    public EchoPacket(String message) {
        this.message = message;
    }

    public void handle(InetSocketAddress origin, Side side) {
        System.out.println("EchoPacket: " + message);
    }

    public void encode(SimpleByteBuffer data) {
        data.writeString(message);
    }
}
