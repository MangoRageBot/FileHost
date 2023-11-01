package org.mangorage.filehost.networking.packets.core;

import org.mangorage.filehost.networking.packets.AudioFramePacket;
import org.mangorage.filehost.networking.packets.EchoPacket;
import org.mangorage.filehost.networking.packets.FileTransferPacket;
import org.mangorage.filehost.networking.packets.VideoFramePacket;

import java.util.concurrent.atomic.AtomicInteger;

public class Packets {
    private static final AtomicInteger ID = new AtomicInteger(0);
    public static final PacketHandler<EchoPacket> ECHO_PACKET = PacketHandler.create(
            EchoPacket.class,
            ID.getAndAdd(1),
            (data, packet) -> packet.encode(data),
            EchoPacket::decode,
            EchoPacket::handle
    );

    public static final PacketHandler<FileTransferPacket> FILE_TRANSFER_PACKET = PacketHandler.create(
            FileTransferPacket.class,
            ID.getAndAdd(1),
            (data, packet) -> packet.encode(data),
            FileTransferPacket::decode,
            FileTransferPacket::handle
    );

    public static final PacketHandler<VideoFramePacket> VIDEO_FRAME_PACKET = PacketHandler.create(
            VideoFramePacket.class,
            ID.getAndAdd(1),
            (data, packet) -> packet.encode(data),
            VideoFramePacket::decode,
            VideoFramePacket::handle
    );

    public static final PacketHandler<AudioFramePacket> AUDIO_FRAME_PACKET_PACKET = PacketHandler.create(
            AudioFramePacket.class,
            ID.getAndAdd(1),
            (data, packet) -> packet.encode(data),
            AudioFramePacket::decode,
            AudioFramePacket::handle
    );


    public static void init() {}
}
