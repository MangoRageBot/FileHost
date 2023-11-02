package org.mangorage.filehost.networking;

import org.mangorage.filehost.core.Constants;
import org.mangorage.filehost.networking.packets.core.RatelimitedPacketSender;
import org.mangorage.filehost.networking.packets.main.FileTransferPacket;
import org.mangorage.filehost.networking.packets.core.Packets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class NetworkingUtils {
    public static String getIPString(InetSocketAddress address) {
        return "%s:%s".formatted(address.getHostString(), address.getPort());
    }
}
