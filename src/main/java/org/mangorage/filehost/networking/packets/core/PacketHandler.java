package org.mangorage.filehost.networking.packets.core;

import org.jetbrains.annotations.Nullable;
import org.mangorage.filehost.networking.Side;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;

public class PacketHandler<T> {
    private static final HashMap<Class<?>, PacketHandler<?>> PACKETS = new HashMap<>();


    public static PacketResponse<?> receivePacket(DatagramSocket socket) throws IOException, ClassNotFoundException {
        return receivePacket(socket, null);
    }

    public static PacketResponse<?> receivePacket(DatagramSocket socket, @Nullable ArrayList<DatagramPacket> receivedRawPackets) throws IOException, ClassNotFoundException {
        byte[] headerByte = new byte[1024];
        DatagramPacket headerPacket = new DatagramPacket(headerByte, headerByte.length);
        socket.receive(headerPacket);
        if (receivedRawPackets != null)
            receivedRawPackets.add(headerPacket);

        DataInputStream header = new DataInputStream(new ByteArrayInputStream(headerPacket.getData()));
        String packetType = header.readUTF();
        Side from = Side.values()[header.readInt()];
        int packetLength = header.readInt();

        byte[] packetData = new byte[packetLength];
        DatagramPacket data = new DatagramPacket(packetData, packetData.length);
        socket.receive(data);
        if (receivedRawPackets != null)
            receivedRawPackets.add(data);

        Class<?> packetClass = Class.forName(packetType);

        if (!PACKETS.containsKey(packetClass)) {
            return null;
        }

        return new PacketResponse<>(
                PACKETS.get(packetClass).getDecoder().decode(new DataInputStream(new ByteArrayInputStream(data.getData()))),
                from,
                data.getSocketAddress()
        );
    }

    @SuppressWarnings("unchecked")
    public static <T> void handle(T packet) {
        if (PACKETS.containsKey(packet.getClass())) {
            ((PacketHandler<T>) PACKETS.get(packet.getClass())).getHandler().handle(packet);
        }
    }


    public interface IEncoder<T> {
        void encode(DataOutputStream data, T packet) throws IOException;
    }

    public interface IDecoder<T> {
        T decode(DataInputStream data) throws IOException;
    }

    public interface IHandler<T> {
        void handle(T packet);
    }

    public static <T> PacketHandler<T> create(Class<T> type, IEncoder<T> encoder, IDecoder<T> decoder, IHandler<T> handler) {
        PacketHandler<T> packetHandler = new PacketHandler<>(encoder, decoder, handler);
        PACKETS.put(type, packetHandler);
        return packetHandler;
    }


    private final IEncoder<T> encoder;
    private final IDecoder<T> decoder;
    private final IHandler<T> handler;

    private PacketHandler(IEncoder<T> encoder, IDecoder<T> decoder, IHandler<T> handler) {
        this.encoder = encoder;
        this.decoder = decoder;
        this.handler = handler;
    }

    public void send(T packet, Side senderSide, SocketAddress address, DatagramSocket socket) {
        try {
            // Get Packet Data
            ByteArrayOutputStream BOS = new ByteArrayOutputStream();
            DataOutputStream OS = new DataOutputStream(BOS);
            encoder.encode(OS, packet);
            byte[] data = BOS.toByteArray();

            // Get Header
            ByteArrayOutputStream headerBOS = new ByteArrayOutputStream();
            DataOutputStream headerOS = new DataOutputStream(headerBOS);
            headerOS.writeUTF(packet.getClass().getName());
            headerOS.writeInt(senderSide.ordinal());
            headerOS.writeInt(data.length);
            byte[] header = headerBOS.toByteArray();

            // Create Header and Data Packets
            DatagramPacket headerPacket = new DatagramPacket(header, header.length, address);
            DatagramPacket dataPacket = new DatagramPacket(data, data.length, address);

            // Send Header and Data Packets
            socket.send(headerPacket);
            socket.send(dataPacket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public IDecoder<T> getDecoder() {
        return decoder;
    }

    public IHandler<T> getHandler() {
        return handler;
    }
}
