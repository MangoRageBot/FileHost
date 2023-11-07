package org.mangorage.filehost.common.networking.core;

import org.mangorage.filehost.common.networking.packets.ChatMessagePacket;
import org.mangorage.filehost.common.networking.packets.EchoPacket;
import org.mangorage.filehost.common.networking.packets.HandshakePacket;
import org.mangorage.filehost.common.networking.packets.ObjectPacket;

public class Packets {
    private static int ID = 0;
    public static final PacketHandler<EchoPacket> ECHO_PACKET = PacketHandler.create(
            EchoPacket.class,
            ID++,
            EchoPacket::encode,
            EchoPacket::new,
            EchoPacket::handle
    );
    public static final PacketHandler<HandshakePacket> HANDSHAKE_PACKET = PacketHandler.create(
            HandshakePacket.class,
            ID++,
            HandshakePacket::encode,
            HandshakePacket::new,
            HandshakePacket::handle
    );
    public static final PacketHandler<ChatMessagePacket> CHAT_MESSAGE_PACKET = PacketHandler.create(
            ChatMessagePacket.class,
            ID++,
            ChatMessagePacket::encode,
            ChatMessagePacket::new,
            ChatMessagePacket::handle
    );
    public static final PacketHandler<ObjectPacket> OBJECT_PACKET = PacketHandler.create(
            ObjectPacket.class,
            ID++,
            ObjectPacket::encode,
            ObjectPacket::new,
            ObjectPacket::handle
    );

    public static void init() {}
}
