package org.mangorage.filehost.networking.packets.main;

import org.mangorage.filehost.gui.BasicFrame;
import org.mangorage.filehost.networking.Side;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;

public class AudioFramePacket {

    public static AudioFramePacket decode(DataInputStream data) throws IOException {
        return new AudioFramePacket(data.readAllBytes());
    }


    private final byte[] audioData;

    public AudioFramePacket(byte[] audioData) {
        this.audioData = audioData;
    }

    public void handle(InetSocketAddress origin, Side side) {
        BasicFrame.playSound(audioData);
    }

    public void encode(DataOutputStream data) throws IOException {
        data.write(audioData);
    }
}
