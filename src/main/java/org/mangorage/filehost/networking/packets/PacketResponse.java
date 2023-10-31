package org.mangorage.filehost.networking.packets;

import org.mangorage.filehost.networking.Side;

import java.net.SocketAddress;

public record PacketResponse(IPacket packet, Side sentFrom, SocketAddress source){}
