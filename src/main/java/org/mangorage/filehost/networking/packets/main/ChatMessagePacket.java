package org.mangorage.filehost.networking.packets.main;

import org.mangorage.filehost.Client;
import org.mangorage.filehost.Server;
import org.mangorage.filehost.core.SimpleByteBuffer;
import org.mangorage.filehost.networking.ClientManager;
import org.mangorage.filehost.networking.Side;
import org.mangorage.filehost.networking.packets.core.Packets;

import java.net.InetSocketAddress;

public class ChatMessagePacket {
    public static ChatMessagePacket decode(SimpleByteBuffer buffer) {
        return new ChatMessagePacket(buffer.readString());
    }

    private final String message;

    public ChatMessagePacket(String message) {
        this.message = message;
    }

    public void enocde(SimpleByteBuffer buffer) {
        buffer.writeString(message);
    }

    public void handle(InetSocketAddress origin, Side side) {
        if (side == Side.CLIENT) {
            if (!ClientManager.isAuthenticated(origin)) return;
            // On Server from Client
            // Send to Clients
            var client = ClientManager.getClient(origin);
            ClientManager.sendPacketToAll(
                    Server.getInstance(),
                    Packets.CHAT_MESSAGE_PACKET,
                    new ChatMessagePacket("[%s]: %s".formatted(client.getUsername(), message))
            );
        } else {
            // On Client from Server
            Client.getInstance().addMessage(message);
        }
    }
}
