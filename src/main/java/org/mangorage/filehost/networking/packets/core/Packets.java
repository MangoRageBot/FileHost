package org.mangorage.filehost.networking.packets.core;

import org.mangorage.filehost.networking.packets.main.EchoPacket;
import org.mangorage.filehost.networking.packets.main.HandshakePacket;

public class Packets {
    private static int ID = 0;

    public static final PacketHandler<EchoPacket> ECHO_PACKET = PacketHandler.create(
            EchoPacket.class,
            ID++,
            (data, packet) -> packet.encode(data),
            EchoPacket::decode,
            EchoPacket::handle
    );

    public static final PacketHandler<HandshakePacket> HANDSHAKE_PACKET_PACKET = PacketHandler.create(
            HandshakePacket.class,
            ID++,
            (data, packet) -> packet.encode(data),
            HandshakePacket::decode,
            HandshakePacket::handle
    );

    public static void init() {}
}
