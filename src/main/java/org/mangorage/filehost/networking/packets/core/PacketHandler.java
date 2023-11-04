package org.mangorage.filehost.networking.packets.core;

import org.mangorage.filehost.core.SimpleByteBuffer;
import org.mangorage.filehost.networking.Side;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;

public class PacketHandler<T> {
    private static final int PACKET_SIZE = 65536;
    private static final HashMap<Integer, PacketHandler<?>> PACKETS = new HashMap<>();


    public static PacketResponse<?> receivePacket(DatagramSocket socket) throws IOException {
        DatagramPacket datagramPacket = new DatagramPacket(new byte[PACKET_SIZE], PACKET_SIZE);
        socket.receive(datagramPacket);

        SimpleByteBuffer headerBuffer = new SimpleByteBuffer(datagramPacket.getData());
        int packetId = headerBuffer.readInt();
        Side from = headerBuffer.readEnum(Side.class);

        System.out.println("%s %s".formatted(packetId, from));
        SimpleByteBuffer packetBuffer = new SimpleByteBuffer(headerBuffer.readBytes());

        if (packetId < 0 || from == null || !PACKETS.containsKey(packetId)) {
            System.out.println("Received Bad Packet (Packet ID/Type: %s %s) from %s....".formatted(packetId, PACKETS.containsKey(packetId) ? PACKETS.get(packetId).getClazz().getName() : "Unknown", datagramPacket.getSocketAddress()));
            return null;
        }

        System.out.println("Response: %s %s %s".formatted(packetId, from, PACKETS.get(packetId).clazz));
        System.out.println("Packet received with size: %s".formatted(headerBuffer.toBytes().length));
        return new PacketResponse<>(
                PACKETS.get(packetId).getDecoder().decode(packetBuffer),
                packetId,
                from,
                (InetSocketAddress) datagramPacket.getSocketAddress()
        );
    }

    @SuppressWarnings("unchecked")
    public static <T> void handle(T packet, int packetId, InetSocketAddress origin, Side side) {
        if (PACKETS.containsKey(packetId)) {
            ((PacketHandler<T>) PACKETS.get(packetId)).getHandler().handle(packet, origin, side);
        }
    }


    public interface IEncoder<T> {
        void encode(SimpleByteBuffer data, T packet) throws IOException;
    }

    public interface IDecoder<T> {
        T decode(SimpleByteBuffer data) throws IOException;
    }

    public interface IHandler<T> {
        void handle(T packet, InetSocketAddress origin, Side side);
    }

    public static <T> PacketHandler<T> create(Class<T> type, int ID, IEncoder<T> encoder, IDecoder<T> decoder, IHandler<T> handler) {
        System.out.println("Created Packet Handler for: %s (Packet ID: %s)".formatted(type.getName(), ID));
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

    public void send(T packet, PacketSender sender, SocketAddress sendTo) {
        try {
            SimpleByteBuffer headerBuffer = new SimpleByteBuffer();
            SimpleByteBuffer packetBuffer = new SimpleByteBuffer();
            encoder.encode(packetBuffer, packet);

            headerBuffer.writeInt(ID);
            headerBuffer.writeEnum(sender.getSide());
            headerBuffer.writeBytes(packetBuffer.toBytes());

            byte[] data = headerBuffer.toBytes();
            if (data.length > PACKET_SIZE) {
                System.err.println("Unable to send packet %s exceeds packet size of %s, size of packet %s".formatted(packet.getClass().getName(), PACKET_SIZE, data.length));
                return;
            }

            DatagramPacket datagramPacket = new DatagramPacket(data, data.length, sendTo);
            sender.send(datagramPacket);
            System.out.println("Packet Sent with size: %s".formatted(data.length));
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
