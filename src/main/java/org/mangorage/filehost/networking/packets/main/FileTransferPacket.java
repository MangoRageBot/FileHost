package org.mangorage.filehost.networking.packets.main;

import org.mangorage.filehost.networking.Side;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;

public class FileTransferPacket {

    public static FileTransferPacket decode(DataInputStream data) throws IOException {
        return new FileTransferPacket(data.readUTF(), data.readInt(), data.readAllBytes());
    }

    private final String ID;
    private final int chunk;
    private final byte[] data;


    public FileTransferPacket(String ID, int chunk, byte[] data) {
        this.ID = ID;
        this.chunk = chunk;
        this.data = data;
    }


    public void handle(InetSocketAddress origin, Side side) {

    }

    public void encode(DataOutputStream data) throws IOException {
        data.writeUTF(ID);
        data.writeInt(chunk);
        data.write(this.data);
    }
}
