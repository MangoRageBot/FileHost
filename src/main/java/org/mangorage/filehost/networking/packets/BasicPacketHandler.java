package org.mangorage.filehost.networking.packets;

import org.mangorage.filehost.networking.Side;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

public class BasicPacketHandler {
    public static void sendPacket(IPacket packet, Side sendTo, SocketAddress address, DatagramSocket socket) {
        try {
            // Get Packet Data
            ByteArrayOutputStream BOS = new ByteArrayOutputStream();
            DataOutputStream OS = new DataOutputStream(BOS);
            packet.encode(OS);
            byte[] data = BOS.toByteArray();

            // Get Header
            ByteArrayOutputStream headerBOS = new ByteArrayOutputStream();
            DataOutputStream headerOS = new DataOutputStream(headerBOS);
            headerOS.writeUTF(packet.getType().getName());
            headerOS.writeInt(sendTo.ordinal());
            headerOS.writeInt(data.length);
            byte[] header = headerBOS.toByteArray();

            DatagramPacket headerPacket = new DatagramPacket(header, header.length, address);
            DatagramPacket dataPacket = new DatagramPacket(data, data.length, address);
            socket.send(headerPacket);
            socket.send(dataPacket);
            System.out.println("Sent Packet!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static IPacket recievePacket(Class<?> packetClass, DataInputStream data) {
        try {
            IPacket recievedPacket = (IPacket) packetClass.getDeclaredMethod("decode", DataInputStream.class).invoke(null, data);
            return recievedPacket;
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ignore) {}
        return null;
    }


    public static PacketResponse recieve(DatagramSocket socket) {
        byte[] headerByte = new byte[1024];
        try {
            DatagramPacket headerPacket = new DatagramPacket(headerByte, headerByte.length);
            socket.receive(headerPacket);

            DataInputStream header = new DataInputStream(new ByteArrayInputStream(headerPacket.getData()));
            String packetType = header.readUTF();
            Side from = Side.values()[header.readInt()];
            int packetLength = header.readInt();

            byte[] packetData = new byte[packetLength];
            DatagramPacket data = new DatagramPacket(packetData, packetData.length);
            socket.receive(data);

            Class<?> packetClass = Class.forName(packetType);
            return new PacketResponse(
                    BasicPacketHandler.recievePacket(packetClass, new DataInputStream(new ByteArrayInputStream(data.getData()))),
                    from,
                    data.getSocketAddress()
            );
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }
}
