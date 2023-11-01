package org.mangorage.filehost.networking.packets;

import org.mangorage.filehost.Client;
import org.mangorage.filehost.Server;
import org.mangorage.filehost.core.Scheduler;
import org.mangorage.filehost.networking.ClientManager;
import org.mangorage.filehost.networking.Side;
import org.mangorage.filehost.networking.packets.core.Packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class HandshakePacket {
    public static HandshakePacket decode(DataInputStream data) {
        return new HandshakePacket();
    }

    public HandshakePacket() {}

    public void encode(DataOutputStream data) {}

    public void handle(InetSocketAddress origin, Side sentFrom) {
        if (sentFrom == Side.CLIENT) {
            ClientManager.setConnected(origin);
            Scheduler.RUNNER.scheduleWithFixedDelay(() -> {
                Packets.ECHO_PACKET.send(
                        new EchoPacket("Ping From Server..."),
                        Side.SERVER,
                        origin,
                        Server.getInstance()
                );
            }, 0, 6, TimeUnit.SECONDS);
        }
    }
}
