package org.mangorage.filehost.common.networking.packets;


import org.mangorage.filehost.common.core.buffer.SimpleByteBuffer;
import org.mangorage.filehost.server.ClientManager;
import org.mangorage.filehost.common.networking.Side;

import java.net.InetSocketAddress;

public class HandshakePacket {
    private final String username;
    private final String password;

    public HandshakePacket(SimpleByteBuffer data) {
        this(data.readString(), data.readString());
    }

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
