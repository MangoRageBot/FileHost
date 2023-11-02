package org.mangorage.filehost.networking.packets.main;

import org.mangorage.filehost.Server;
import org.mangorage.filehost.networking.ClientManager;
import org.mangorage.filehost.networking.Side;
import org.mangorage.filehost.networking.packets.core.Packets;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlaySoundPacket {

    public static void playSoundFile(String filePath) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            try {
                // Create an AudioInputStream from the specified WAV file
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath));

                // Get the audio format from the AudioInputStream
                AudioFormat sourceFormat = audioInputStream.getFormat();

                // Define a compatible target audio format
                AudioFormat targetFormat = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        44100, // Sample rate
                        16,    // Sample size in bits
                        2,     // Channels (stereo)
                        4,     // Frame size
                        44100, // Frame rate
                        false  // Little-endian byte order
                );

                // Convert the audio data to the target format
                AudioInputStream convertedStream = AudioSystem.getAudioInputStream(targetFormat, audioInputStream);

                // Create a DataLine.Info object for the SourceDataLine
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, targetFormat);

                // Open a SourceDataLine to play the audio
                SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
                sourceDataLine.open(targetFormat);
                sourceDataLine.start();

                // Create a buffer to read data from the AudioInputStream
                byte[] buffer = new byte[4096];
                int bytesRead;

                // Read and play the audio data
                while ((bytesRead = convertedStream.read(buffer, 0, buffer.length)) != -1) {
                    sourceDataLine.write(buffer, 0, bytesRead);
                }

                // Wait for the audio to finish playing
                sourceDataLine.drain();

                // Close the SourceDataLine when done
                sourceDataLine.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        executor.shutdown();
    }


    public static PlaySoundPacket decode(DataInputStream data) throws IOException {
        return new PlaySoundPacket(data.readUTF());
    }

    private final String file;
    public PlaySoundPacket(String s) {
        this.file = s;
    }

    public void encode(DataOutputStream data) throws IOException {
        data.writeUTF(file);
    }

    public void handle(InetSocketAddress origin, Side sentFrom) {
        System.out.println("HANDLE?");
        if (sentFrom == Side.SERVER) {
            System.out.println("Playing sound on Client!");
            playSoundFile(file);
        } else {
            ClientManager.sendPacketToAll(
                    Server.getInstance(),
                    Packets.PLAY_SOUND_PACKET_PACKET,
                    new PlaySoundPacket(file)
            );
        }
    }
}
