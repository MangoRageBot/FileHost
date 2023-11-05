package org.mangorage.filehost.networking.packets.main;


import org.mangorage.filehost.core.SimpleByteBuffer;
import org.mangorage.filehost.networking.ClientManager;
import org.mangorage.filehost.networking.Side;

import java.net.InetSocketAddress;

public class HandshakePacket {
    public static HandshakePacket decode(SimpleByteBuffer data) {
        return new HandshakePacket(data.readString(), data.readString());
    }

    private final String username;
    private final String password;
    public HandshakePacket(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void encode(SimpleByteBuffer data) {
        data.writeString(username);
        data.writeString(password);
    }

    public void handle(InetSocketAddress origin, Side sentFrom) {
        if (sentFrom == Side.CLIENT) {
            ClientManager.setConnected(origin, username, password);
        }
    }
}
