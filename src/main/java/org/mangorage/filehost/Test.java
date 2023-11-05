package org.mangorage.filehost;

import org.mangorage.filehost.core.SimpleByteBuffer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.function.Consumer;

public class Test {

    public static DatagramPacket createBasicPacket(int packetID, int sideID, InetSocketAddress address, Consumer<SimpleByteBuffer> packetBuffer) {
        SimpleByteBuffer header = new SimpleByteBuffer();
        SimpleByteBuffer packet = new SimpleByteBuffer();
        header.writeInt(packetID);
        header.writeInt(sideID);
        packetBuffer.accept(packet);
        header.writeBytes(packet.toBytes());
        byte[] data = header.toBytes();
        return new DatagramPacket(data, data.length, address);
    }


    public static void main(String[] args) throws IOException {
        try (DatagramSocket socket = new DatagramSocket()) {
            var address = new InetSocketAddress("localhost", 25565);

            // Handle handshake
            socket.send(createBasicPacket(1, 1, address, d -> {
                d.writeString("12345!");
            }));

            // Handle chat packet
            for (int i = 0; i < 100; i++) {
                socket.send(createBasicPacket(2, 1, address, d -> {
                    d.writeString("MangoRage");
                    d.writeString("I made a custom packet injector.");
                }));
            }
        }
    }
}
