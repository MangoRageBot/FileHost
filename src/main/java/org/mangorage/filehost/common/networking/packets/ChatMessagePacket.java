package org.mangorage.filehost.common.networking.packets;

import org.mangorage.filehost.client.Client;
import org.mangorage.filehost.common.core.buffer.SimpleByteBuf;
import org.mangorage.filehost.server.Server;
import org.mangorage.filehost.server.ClientManager;
import org.mangorage.filehost.common.networking.Side;
import org.mangorage.filehost.common.networking.Packets;

import java.net.InetSocketAddress;

public class ChatMessagePacket {
    private final String username;
    private final String message;

    public ChatMessagePacket(SimpleByteBuf buffer) {
        this(buffer.readString(), buffer.readString());
    }

    public ChatMessagePacket(String username, String message) {
        this.username = username;
        this.message = message;
    }

    public ChatMessagePacket(String message) {
        this("client", message);
    }

    public void encode(SimpleByteBuf buffer) {
        buffer.writeString(username);
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
                    new ChatMessagePacket(client.getUsername(), message)
            );
        } else {
            // On Client from Server
            Client.getInstance().addMessage("[%s]: %s".formatted(username, message));
        }
    }
}
