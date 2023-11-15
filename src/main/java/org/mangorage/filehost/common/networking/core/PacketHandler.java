package org.mangorage.filehost.common.networking.core;

import io.netty.buffer.Unpooled;
import io.netty.channel.socket.DatagramPacket;
import org.mangorage.filehost.common.core.buffer.SimpleByteBuf;
import org.mangorage.filehost.common.networking.Side;

import java.io.IOException;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class PacketHandler<T> {
    private static final int PACKET_SIZE = 65536;
    private static final HashMap<Integer, PacketHandler<?>> PACKETS = new HashMap<>();


    public static PacketResponse<?> receivePacket(DatagramPacket datagramPacket) throws IOException {
        SimpleByteBuf headerBuffer = new SimpleByteBuf(datagramPacket.content());
        int packetId = headerBuffer.readInt();
        Side from = headerBuffer.readEnum(Side.class);

        System.out.println("%s %s".formatted(packetId, from));
        SimpleByteBuf packetBuffer = new SimpleByteBuf(Unpooled.wrappedBuffer(headerBuffer.readByteArray()));

        if (packetId < 0 || from == null || !PACKETS.containsKey(packetId)) {
            System.out.println("Received Bad Packet (Packet ID/Type: %s %s) from %s....".formatted(packetId, PACKETS.containsKey(packetId) ? PACKETS.get(packetId).getClazz().getName() : "Unknown", datagramPacket.sender()));
            return null;
        }

        System.out.println("Response: %s %s %s".formatted(packetId, from, PACKETS.get(packetId).clazz));
       // System.out.println("Packet received with size: %s".formatted(headerBuffer.array().length));
        return new PacketResponse<>(
                PACKETS.get(packetId).getDecoder().apply(packetBuffer),
                packetId,
                from,
                datagramPacket.sender()
        );
    }

    @SuppressWarnings("unchecked")
    public static <T> void handle(T packet, int packetId, InetSocketAddress origin, Side side) {
        if (PACKETS.containsKey(packetId)) {
            ((PacketHandler<T>) PACKETS.get(packetId)).getHandler().handle(packet, origin, side);
        }
    }

    public interface IHandler<T> {
        void handle(T packet, InetSocketAddress origin, Side side);
    }

    public static <T> PacketHandler<T> create(Class<T> type, int ID, BiConsumer<T, SimpleByteBuf> encoder, Function<SimpleByteBuf, T> decoder, IHandler<T> handler) {
        System.out.println("Created Packet Handler for: %s (Packet ID: %s)".formatted(type.getName(), ID));
        PacketHandler<T> packetHandler = new PacketHandler<>(type, ID, encoder, decoder, handler);
        PACKETS.put(ID, packetHandler);
        return packetHandler;
    }


    private final Class<T> clazz;
    private final int ID;
    private final BiConsumer<T, SimpleByteBuf> encoder;
    private final Function<SimpleByteBuf, T> decoder;
    private final IHandler<T> handler;

    private PacketHandler(Class<T> type, int ID, BiConsumer<T, SimpleByteBuf> encoder, Function<SimpleByteBuf, T> decoder, IHandler<T> handler) {
        this.clazz = type;
        this.ID = ID;
        this.encoder = encoder;
        this.decoder = decoder;
        this.handler = handler;
    }

    public void send(T packet, IPacketSender sender, InetSocketAddress sendTo) {
        SimpleByteBuf headerBuffer = new SimpleByteBuf(Unpooled.buffer(32));
        SimpleByteBuf packetBuffer = new SimpleByteBuf(Unpooled.buffer(32));
        encoder.accept(packet, packetBuffer);

        headerBuffer.writeInt(ID); // ID
        headerBuffer.writeEnum(sender.getSide()); // Side
        System.out.println(headerBuffer.array().length + " Size of Header....");
        headerBuffer.writeByteArray(packetBuffer.array());


        byte[] data = headerBuffer.array();
        if (data.length > PACKET_SIZE) {
            System.err.println("Unable to send packet %s exceeds packet size of %s, size of packet %s".formatted(packet.getClass().getName(), PACKET_SIZE, data.length));
            return;
        }

        DatagramPacket datagramPacket = new DatagramPacket(headerBuffer, sendTo);
        sender.send(new Packet(datagramPacket, packet.getClass()));
    }

    public Function<SimpleByteBuf, T> getDecoder() {
        return decoder;
    }

    public IHandler<T> getHandler() {
        return handler;
    }

    public Class<T> getClazz() {
        return clazz;
    }
}
