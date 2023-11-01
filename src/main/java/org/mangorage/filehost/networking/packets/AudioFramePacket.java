package org.mangorage.filehost.networking.packets;

import org.mangorage.filehost.gui.BasicFrame;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class AudioFramePacket {

    public static AudioFramePacket decode(DataInputStream data) throws IOException {
        return new AudioFramePacket(data.readAllBytes());
    }


    private final byte[] audioData;

    public AudioFramePacket(byte[] audioData) {
        this.audioData = audioData;
    }

    public void handle() {
        BasicFrame.playSound(audioData);
    }

    public void encode(DataOutputStream data) throws IOException {
        data.write(audioData);
    }
}
