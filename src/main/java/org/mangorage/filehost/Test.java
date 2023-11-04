package org.mangorage.filehost;

import org.mangorage.filehost.core.SimpleByteBuffer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class Test {
    public static void main(String[] args) throws IOException {
        SimpleByteBuffer header = new SimpleByteBuffer();
        SimpleByteBuffer packet = new SimpleByteBuffer();
        packet.writeString("TEST!");
        header.writeInt(0);
        header.writeInt(1);
        header.writeBytes(packet.toBytes());


        byte[] data = header.toBytes();
        DatagramPacket datagramPacket = new DatagramPacket(data, data.length, new InetSocketAddress("localhost", 25565));

        DatagramSocket socket = new DatagramSocket();
        socket.send(datagramPacket);
    }
}
