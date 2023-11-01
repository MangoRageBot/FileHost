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
    private static final HashMap<Integer, PacketHandler<?>> PACKETS = new HashMap<>();


    public static PacketResponse<?> receivePacket(DatagramSocket socket) throws IOException, ClassNotFoundException {
        return receivePacket(socket, null);
    }

    public static PacketResponse<?> receivePacket(DatagramSocket socket, @Nullable ArrayList<DatagramPacket> receivedRawPackets) throws IOException {
        byte[] headerByte = new byte[1024];
        DatagramPacket headerPacket = new DatagramPacket(headerByte, headerByte.length);
        socket.receive(headerPacket);
        if (receivedRawPackets != null)
            receivedRawPackets.add(headerPacket);

        DataInputStream header = new DataInputStream(new ByteArrayInputStream(headerPacket.getData()));
        int packetId = header.readInt();
        int sideId = header.readInt();
        if (sideId > Side.values().length || sideId < 0) return null;
        Side from = Side.values()[sideId];
        int packetLength = header.readInt();

        byte[] packetData = new byte[packetLength];
        DatagramPacket data = new DatagramPacket(packetData, packetData.length);
        socket.receive(data);
        if (receivedRawPackets != null)
            receivedRawPackets.add(data);

        if (!PACKETS.containsKey(packetId)) {
            return null;
        }

        return new PacketResponse<>(
                PACKETS.get(packetId).getDecoder().decode(new DataInputStream(new ByteArrayInputStream(data.getData()))),
                packetId,
                from,
                data.getSocketAddress()
        );
    }

    @SuppressWarnings("unchecked")
    public static <T> void handle(T packet, int packetId) {
        if (PACKETS.containsKey(packetId)) {
            ((PacketHandler<T>) PACKETS.get(packetId)).getHandler().handle(packet);
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

    public static <T> PacketHandler<T> create(Class<T> type, int ID, IEncoder<T> encoder, IDecoder<T> decoder, IHandler<T> handler) {
        PacketHandler<T> packetHandler = new PacketHandler<>(ID, encoder, decoder, handler);
        PACKETS.put(ID, packetHandler);
        return packetHandler;
    }


    private final int ID;
    private final IEncoder<T> encoder;
    private final IDecoder<T> decoder;
    private final IHandler<T> handler;

    private PacketHandler(int ID, IEncoder<T> encoder, IDecoder<T> decoder, IHandler<T> handler) {
        this.ID = ID;
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
            headerOS.writeInt(ID);
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
            System.out.println("Packet Had issue: %s".formatted(packet.getClass()));
        }
    }

    public IDecoder<T> getDecoder() {
        return decoder;
    }

    public IHandler<T> getHandler() {
        return handler;
    }
}
