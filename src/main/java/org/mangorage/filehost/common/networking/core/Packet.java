package org.mangorage.filehost.common.networking.core;


import io.netty.channel.socket.DatagramPacket;

public record Packet(DatagramPacket packet, String packetName) {
}
