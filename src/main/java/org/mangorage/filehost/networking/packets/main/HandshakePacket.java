package org.mangorage.filehost.networking.packets.main;

import org.mangorage.filehost.Server;
import org.mangorage.filehost.core.Scheduler;
import org.mangorage.filehost.networking.ClientManager;
import org.mangorage.filehost.networking.FileTransferUtils;
import org.mangorage.filehost.networking.NetworkingUtils;
import org.mangorage.filehost.networking.Side;
import org.mangorage.filehost.networking.packets.core.Packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.InetSocketAddress;
import java.util.UUID;

public class HandshakePacket {
    public static HandshakePacket decode(DataInputStream data) {
        return new HandshakePacket();
    }

    public HandshakePacket() {}

    public void encode(DataOutputStream data) {}

    public void handle(InetSocketAddress origin, Side sentFrom) {
        if (sentFrom == Side.CLIENT) {
            ClientManager.setConnected(origin);
            var server = Server.getInstance();
            if (server == null) return;
            Scheduler.RUNNER.execute(() -> {
                String VIDEO = "%s.mp4".formatted(UUID.randomUUID());
                FileTransferUtils.transferFile(new File("video2.mp4"), VIDEO, server, origin, () -> {
                    Packets.PLAY_VIDEO_PACKET_PACKET.send(
                            new PlayVideoPacket(VIDEO),
                            server,
                            origin
                    );
                });
            });
        }
    }
}
