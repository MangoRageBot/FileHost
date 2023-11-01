package org.mangorage.filehost.networking.packets;

import org.mangorage.filehost.Client;
import org.mangorage.filehost.networking.ClientManager;
import org.mangorage.filehost.networking.Side;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;

public class HandshakePacket {
    public static HandshakePacket decode(DataInputStream data) {
        return new HandshakePacket();
    }

    public HandshakePacket() {}

    public void encode(DataOutputStream data) {}

    public void handle(InetSocketAddress origin, Side sentFrom) {
        if (sentFrom == Side.CLIENT) {
            ClientManager.setConnected(origin);
        }
    }
}
