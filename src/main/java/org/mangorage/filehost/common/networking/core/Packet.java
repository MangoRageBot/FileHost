package org.mangorage.filehost.common.networking.core;

import java.net.DatagramPacket;

public record Packet(DatagramPacket packet, Class<?> packetClass) {
}
