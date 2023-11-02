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
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;

public class PacketHandler<T> {
    private static final HashMap<Integer, PacketHandler<?>> PACKETS = new HashMap<>();


    public static PacketResponse<?> receivePacket(DatagramSocket socket) throws IOException {
        return receivePacket(socket, null);
    }

    public static PacketResponse<?> receivePacket(DatagramSocket socket, @Nullable ArrayList<DatagramPacket> receivedRawPackets) throws IOException {
        byte[] headerByte = new byte[12];
        DatagramPacket headerPacket = new DatagramPacket(headerByte, headerByte.length);
        socket.receive(headerPacket);
        if (receivedRawPackets != null)
            receivedRawPackets.add(headerPacket);

        DataInputStream header = new DataInputStream(new ByteArrayInputStream(headerPacket.getData()));
        int packetId = header.readInt();
        int sideId = header.readInt();
        int packetLength = header.readInt();
        if (packetId < 0 || sideId > 1 || sideId < 0 || !PACKETS.containsKey(packetId)) {
            System.out.println("Received Bad Packet (Packet ID/Type: %s %s) from %s....".formatted(packetId, PACKETS.containsKey(packetId) ? PACKETS.get(packetId).getClazz().getName() : "Unknown", headerPacket.getSocketAddress()));
            return null;
        }
        Side from = Side.values()[sideId];

        byte[] packetData = new byte[packetLength];
        DatagramPacket data = new DatagramPacket(packetData, packetData.length);
        socket.receive(data);

        if (receivedRawPackets != null)
            receivedRawPackets.add(data);

        return new PacketResponse<>(
                PACKETS.get(packetId).getDecoder().decode(new DataInputStream(new ByteArrayInputStream(data.getData()))),
                packetId,
                from,
                (InetSocketAddress) data.getSocketAddress()
        );
    }

    @SuppressWarnings("unchecked")
    public static <T> void handle(T packet, int packetId, InetSocketAddress origin, Side side) {
        if (PACKETS.containsKey(packetId)) {
            ((PacketHandler<T>) PACKETS.get(packetId)).getHandler().handle(packet, origin, side);
        }
    }


    public interface IEncoder<T> {
        void encode(DataOutputStream data, T packet) throws IOException;
    }

    public interface IDecoder<T> {
        T decode(DataInputStream data) throws IOException;
    }

    public interface IHandler<T> {
        void handle(T packet, InetSocketAddress origin, Side side);
    }

    public static <T> PacketHandler<T> create(Class<T> type, int ID, IEncoder<T> encoder, IDecoder<T> decoder, IHandler<T> handler) {
        PacketHandler<T> packetHandler = new PacketHandler<>(type, ID, encoder, decoder, handler);
        PACKETS.put(ID, packetHandler);
        return packetHandler;
    }


    private final Class<T> clazz;
    private final int ID;
    private final IEncoder<T> encoder;
    private final IDecoder<T> decoder;
    private final IHandler<T> handler;

    private PacketHandler(Class<T> type, int ID, IEncoder<T> encoder, IDecoder<T> decoder, IHandler<T> handler) {
        this.clazz = type;
        this.ID = ID;
        this.encoder = encoder;
        this.decoder = decoder;
        this.handler = handler;
    }

    public void send(T packet, RatelimitedPacketSender sender, SocketAddress sendTo) {
        try {
            // Get Packet Data
            ByteArrayOutputStream BOS = new ByteArrayOutputStream();
            DataOutputStream OS = new DataOutputStream(BOS);
            encoder.encode(OS, packet);
            byte[] data = BOS.toByteArray();

            // Get Header
            ByteArrayOutputStream headerBOS = new ByteArrayOutputStream(65000);
            DataOutputStream headerOS = new DataOutputStream(headerBOS);
            headerOS.writeInt(ID);
            headerOS.writeInt(sender.getSide().ordinal());
            headerOS.writeInt(data.length);
            byte[] header = headerBOS.toByteArray();

            // Create Header and Data Packets
            DatagramPacket headerPacket = new DatagramPacket(header, header.length, sendTo);
            DatagramPacket dataPacket = new DatagramPacket(data, data.length, sendTo);

            if (dataPacket.getData().length > 65_527) {
                System.out.println("Failed to send Packet! To Large Size: %s-> %s".formatted(dataPacket.getData().length, packet.getClass()));
                return;
            }

            // Send Header and Data Packets
            sender.send(headerPacket, dataPacket);
        } catch (IOException e) {
            System.out.println("Packet Had issue: %s".formatted(packet.getClass()));
            e.printStackTrace(System.out);
        }
    }

    public IDecoder<T> getDecoder() {
        return decoder;
    }

    public IHandler<T> getHandler() {
        return handler;
    }

    public Class<T> getClazz() {
        return clazz;
    }
}
