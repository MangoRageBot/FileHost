package org.mangorage.filehost.networking.packets;

import org.mangorage.filehost.gui.BasicFrame;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class VideoFramePacket {
    public static VideoFramePacket decode(DataInputStream data) throws IOException {
        return new VideoFramePacket(data.readAllBytes());
    }

    private final byte[] frameData;

    public VideoFramePacket(byte[] frameData) {
        this.frameData = frameData;
    }

    public void handle() {
        BasicFrame.INSTANCE.frameDataQueue.add(frameData);
    }

    public void encode(DataOutputStream data) throws IOException {
        data.write(frameData);
    }


}
