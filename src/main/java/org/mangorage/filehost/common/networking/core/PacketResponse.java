package org.mangorage.filehost.common.networking.core;

import org.mangorage.filehost.common.networking.Side;

import java.net.InetSocketAddress;

public record PacketResponse<T>(T packet, int packetId, Side sentFrom, InetSocketAddress source){}
