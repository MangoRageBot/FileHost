package org.mangorage.filehost.networking.packets.main;


import org.mangorage.filehost.core.SimpleByteBuffer;
import org.mangorage.filehost.networking.ClientManager;
import org.mangorage.filehost.networking.Side;

import java.net.InetSocketAddress;

public class HandshakePacket {
    public static HandshakePacket decode(SimpleByteBuffer data) {
        return new HandshakePacket();
    }

    public HandshakePacket() {}

    public void encode(SimpleByteBuffer data) {}

    public void handle(InetSocketAddress origin, Side sentFrom) {
        if (sentFrom == Side.CLIENT) {
            ClientManager.setConnected(origin);
        }
    }
}
