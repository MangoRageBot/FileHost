package org.mangorage.filehost.common.networking.core;

import org.mangorage.filehost.common.networking.Side;

import java.net.InetSocketAddress;

public record PacketResponse<T>(T packet, String packetName, int packetId, Side sentFrom, InetSocketAddress source){}
