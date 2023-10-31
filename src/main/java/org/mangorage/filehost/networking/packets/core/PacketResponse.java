package org.mangorage.filehost.networking.packets.core;

import org.mangorage.filehost.networking.Side;

import java.net.SocketAddress;

public record PacketResponse<T>(T packet, Side sentFrom, SocketAddress source){}
