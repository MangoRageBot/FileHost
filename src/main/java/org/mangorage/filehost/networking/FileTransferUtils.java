package org.mangorage.filehost.networking;

import org.mangorage.filehost.core.Constants;
import org.mangorage.filehost.networking.packets.core.Packets;
import org.mangorage.filehost.networking.packets.core.RatelimitedPacketSender;
import org.mangorage.filehost.networking.packets.main.FileTransferPacket;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class FileTransferUtils {
    private static final HashMap<String, Transfer> TRANSFER_HASH_MAP = new HashMap<>(); // ID -> Transfer


    public static class Transfer {
        private final HashMap<Integer, byte[]> DATA = new HashMap<>();
        private final String dest;

        public Transfer(String dest) {
            this.dest = dest;
        }

        public void putChunk(int chunk, byte[] data) {
            DATA.put(chunk, data);
        }
    }





    public static void transferFile(File file, String dest, RatelimitedPacketSender sender, InetSocketAddress address, Runnable after) {
        long remainingSize = file.length() + 500;
        int chunkSize = Constants.config.chunkSize();
        int chunk = 0;
        long chunks = remainingSize / chunkSize;
        UUID ID = UUID.randomUUID();

        var Transfer = new Transfer(dest);


        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] buffer = new byte[chunkSize];
            int bytesRead;

            while ((bytesRead = fileInputStream.read(buffer)) != -1 && remainingSize > 0) {
                Transfer.putChunk(chunk, Arrays.copyOf(buffer, buffer.length)); // Copy it!
                // Send it now...
                Packets.FILE_TRANSFER_PACKET.send(
                        new FileTransferPacket(ID.toString(), chunk, buffer),
                        sender,
                        address
                );

                // Files.write(Path.of("local_%s".formatted(dest)), buffer, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

                remainingSize -= bytesRead;
                if (remainingSize < chunkSize) {
                    chunkSize = (int) remainingSize;
                    buffer = new byte[chunkSize];
                }
                String percentage = ("%s".formatted(((float)chunk/chunks) * 100));
                percentage = percentage.substring(0, Math.min(percentage.length(), 5)) + '%';
                System.out.println("Sending Chunk (Size: %s B): %s/%s (%s)".formatted(bytesRead, chunk, chunks, percentage));
                chunk++;
            }
            after.run();
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }
}
