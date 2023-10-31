package org.mangorage.filehost.networking.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileTransferPacket {

    public static FileTransferPacket decode(DataInputStream data) throws IOException {
        return new FileTransferPacket(data.readUTF(), data.readAllBytes());
    }

    private final File toTransfer;
    private final String destination;
    private final byte[] data;

    public FileTransferPacket(File toTransfer, String destination) {
        this.toTransfer = toTransfer;
        this.destination = destination;
        this.data = null;
        if (!toTransfer.isFile())
            throw new IllegalStateException("Cannot transfer this! Must be a file!");
    }

    public FileTransferPacket(String dest, byte[] data) {
        this.toTransfer = null;
        this.destination = dest;
        this.data = data;
    }


    public void handle() {
        System.out.println("HANDLING FILE");
        try {
            Files.write(Path.of(destination), data, StandardOpenOption.CREATE);
        } catch (IOException ignored) {
            ignored.printStackTrace(System.out);
        }
        System.out.println("DONE HANDLING FILE");
    }

    public void encode(DataOutputStream data) throws IOException {
        data.writeUTF(destination);
        data.write(Files.readAllBytes(toTransfer.toPath()));
    }
}
