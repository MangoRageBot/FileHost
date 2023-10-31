package org.mangorage.filehost.networking.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class EchoPacket {

    public static EchoPacket decode(DataInputStream data) throws IOException {
        return new EchoPacket(data.readUTF());
    }

    private final String message;

    public EchoPacket(String message) {
        this.message = message;
    }

    public void handle() {
        System.out.println("EchoPacket: " + message);
    }

    public void encode(DataOutputStream data) throws IOException {
        data.writeUTF(message);
    }
}
