package org.mangorage.filehost.common.networking.core;


import io.netty.channel.socket.DatagramPacket;
import org.jetbrains.annotations.NotNull;

public record Packet(@NotNull DatagramPacket packet,@NotNull String packetName) {
}
