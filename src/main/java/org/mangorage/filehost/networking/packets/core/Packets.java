package org.mangorage.filehost.networking.packets.core;

import org.mangorage.filehost.networking.packets.main.AudioFramePacket;
import org.mangorage.filehost.networking.packets.main.EchoPacket;
import org.mangorage.filehost.networking.packets.main.FileTransferPacket;
import org.mangorage.filehost.networking.packets.main.HandshakePacket;
import org.mangorage.filehost.networking.packets.main.PlaySoundPacket;
import org.mangorage.filehost.networking.packets.main.PlayVideoPacket;
import org.mangorage.filehost.networking.packets.main.VideoFramePacket;

import java.util.concurrent.atomic.AtomicInteger;

public class Packets {
    public static class Counter {
        private final AtomicInteger integer = new AtomicInteger(0);

        public int create() {
            return integer.getAndIncrement();
        }
    }
    private static final Counter ID = new Counter();

    public static final PacketHandler<EchoPacket> ECHO_PACKET = PacketHandler.create(
            EchoPacket.class,
            ID.create(),
            (data, packet) -> packet.encode(data),
            EchoPacket::decode,
            EchoPacket::handle
    );

    public static final PacketHandler<FileTransferPacket> FILE_TRANSFER_PACKET = PacketHandler.create(
            FileTransferPacket.class,
            ID.create(),
            (data, packet) -> packet.encode(data),
            FileTransferPacket::decode,
            FileTransferPacket::handle
    );

    public static final PacketHandler<VideoFramePacket> VIDEO_FRAME_PACKET = PacketHandler.create(
            VideoFramePacket.class,
            ID.create(),
            (data, packet) -> packet.encode(data),
            VideoFramePacket::decode,
            VideoFramePacket::handle
    );

    public static final PacketHandler<AudioFramePacket> AUDIO_FRAME_PACKET_PACKET = PacketHandler.create(
            AudioFramePacket.class,
            ID.create(),
            (data, packet) -> packet.encode(data),
            AudioFramePacket::decode,
            AudioFramePacket::handle
    );

    public static final PacketHandler<HandshakePacket> HANDSHAKE_PACKET_PACKET = PacketHandler.create(
            HandshakePacket.class,
            ID.create(),
            (data, packet) -> packet.encode(data),
            HandshakePacket::decode,
            HandshakePacket::handle
    );

    public static final PacketHandler<PlaySoundPacket> PLAY_SOUND_PACKET_PACKET = PacketHandler.create(
            PlaySoundPacket.class,
            ID.create(),
            (data, packet) -> packet.encode(data),
            PlaySoundPacket::decode,
            PlaySoundPacket::handle
    );

    public static final PacketHandler<PlayVideoPacket> PLAY_VIDEO_PACKET_PACKET = PacketHandler.create(
            PlayVideoPacket.class,
            ID.create(),
            (data, packet) -> packet.encode(data),
            PlayVideoPacket::decode,
            PlayVideoPacket::handle
    );


    public static void init() {}
}
