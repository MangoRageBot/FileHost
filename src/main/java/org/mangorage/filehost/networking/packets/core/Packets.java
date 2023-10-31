package org.mangorage.filehost.networking.packets.core;

import org.mangorage.filehost.networking.packets.EchoPacket;
import org.mangorage.filehost.networking.packets.FileTransferPacket;

public class Packets {
    public static final PacketHandler<EchoPacket> ECHO_PACKET = PacketHandler.create(
            EchoPacket.class,
            (data, packet) -> packet.encode(data),
            EchoPacket::decode,
            EchoPacket::handle
    );

    public static final PacketHandler<FileTransferPacket> FILE_TRANSFER_PACKET = PacketHandler.create(
            FileTransferPacket.class,
            (data, packet) -> packet.encode(data),
            FileTransferPacket::decode,
            FileTransferPacket::handle
    );


    public static void init() {}
}
