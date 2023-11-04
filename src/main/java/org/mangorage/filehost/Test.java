package org.mangorage.filehost;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class Test {
    public static void main(String[] args) throws IOException {
        ByteArrayOutputStream headerOS = new ByteArrayOutputStream();
        DataOutputStream headerDOS = new DataOutputStream(headerOS);
        headerDOS.writeInt(0);
        headerDOS.writeInt(1);

        ByteArrayOutputStream packetOS = new ByteArrayOutputStream();
        DataOutputStream packetDOS = new DataOutputStream(packetOS);
        packetDOS.writeUTF("TESTING 123");
        byte[] packetData = packetOS.toByteArray();

        headerDOS.writeInt(packetData.length);
        headerDOS.write(packetData);

        var client = new DatagramSocket();

        byte[] data = headerOS.toByteArray();
        DatagramPacket packet = new DatagramPacket(data, data.length, new InetSocketAddress("localhost", 25565));

        client.send(packet);
    }
}
