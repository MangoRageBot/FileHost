package org.mangorage.filehost.networking.packets.main;

import org.mangorage.filehost.core.simplebbuffer.SimpleByteBuffer;
import org.mangorage.filehost.networking.Side;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;

public class EchoPacket {

    public static EchoPacket decode(SimpleByteBuffer data) throws IOException {
        return new EchoPacket(data.readString());
    }

    private final String message;

    public EchoPacket(String message) {
        this.message = message;
    }

    public void handle(InetSocketAddress origin, Side side) {
        System.out.println("EchoPacket: " + message);
    }

    public void encode(SimpleByteBuffer data) throws IOException {
        data.writeString(message);
    }
}
