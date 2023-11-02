package org.mangorage.filehost.networking.packets.main;

import org.mangorage.filehost.core.Scheduler;
import org.mangorage.filehost.gui.VideoPlayer;
import org.mangorage.filehost.networking.Side;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class PlayVideoPacket {
    public static PlayVideoPacket decode(DataInputStream data) throws IOException {
        return new PlayVideoPacket(data.readUTF());
    }

    private final String message;

    public PlayVideoPacket(String message) {
        this.message = message;
    }

    public void handle(InetSocketAddress origin, Side side) {
        if (side == Side.SERVER) {
            Scheduler.RUNNER.schedule(() -> VideoPlayer.play(message), 500, TimeUnit.MILLISECONDS);
        }
    }

    public void encode(DataOutputStream data) throws IOException {
        data.writeUTF(message);
    }
}
