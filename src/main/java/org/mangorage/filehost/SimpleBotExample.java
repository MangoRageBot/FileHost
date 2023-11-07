package org.mangorage.filehost;

import org.mangorage.filehost.common.core.buffer.SimpleByteBuffer;
import org.mangorage.filehost.common.networking.Side;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Consumer;

public class SimpleBotExample {

    public interface ICommand {
        String handle(String[] args);
    }

    public static class CommandRegistry {
        private static final HashMap<String, ICommand> COMMANDS = new HashMap<>();
        private static final ICommand empty = a -> "Invalid Command";


        public static String handle(String command, String[] args) {
            return COMMANDS.getOrDefault(command, empty).handle(args);
        }

        static {
            COMMANDS.put("help", a -> "To get help, just contact the zoo!\nTesting the newline stuff\nlol");
            COMMANDS.put("add", a -> {
                if (a.length < 2) return "Please provide 2 args";
                Integer A = Integer.parseInt(a[0]);
                Integer B = Integer.parseInt(a[1]);
                return "Result: " + (A + B);
            });
        }
    }



    public static DatagramPacket createBasicPacket(int packetID, int sideID, InetSocketAddress address, Consumer<SimpleByteBuffer> packetBuffer) {
        SimpleByteBuffer header = new SimpleByteBuffer();
        SimpleByteBuffer packet = new SimpleByteBuffer();
        header.writeInt(packetID);
        header.writeInt(sideID);
        packetBuffer.accept(packet);
        header.writeBytes(packet.toBytes());
        byte[] data = header.toBytes();
        return new DatagramPacket(data, data.length, address);
    }


    public static void main(String[] args) throws IOException {
        try (DatagramSocket socket = new DatagramSocket()) {
            var address = new InetSocketAddress("localhost", 25565);

            // Handle handshake
            socket.send(createBasicPacket(1, 1, address, d -> {
                d.writeString("MangoBot");
                d.writeString("12345!");
            }));


            // Handle chat....
            // We respond if someone types in a command...
            Consumer<String> response = s -> {
                try {
                    socket.send(createBasicPacket(2, 1, address, d -> {
                        d.writeString("client");
                        d.writeString(s);
                    }));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            };

            while (!socket.isClosed()) {
                DatagramPacket header = new DatagramPacket(new byte[65536], 65536);
                socket.receive(header);
                SimpleByteBuffer buffer = new SimpleByteBuffer(header.getData());
                int packetID = buffer.readInt();
                Side side = buffer.readEnum(Side.class);
                if (side == Side.SERVER && packetID == 2) {
                    SimpleByteBuffer packetData = new SimpleByteBuffer(buffer.readBytes());
                    String username = packetData.readString();
                    String message = packetData.readString();

                    if (username.equals("System") || username.equals("MangoBot")) continue;

                    String[] rawArgs = message.split(" ");
                    if (rawArgs.length == 0) continue;
                    String cmd = rawArgs[0];
                    if (cmd.startsWith("!") && cmd.length() > 1) cmd = cmd.substring(1);

                    try {
                        response.accept(CommandRegistry.handle(cmd, rawArgs.length == 1 ? new String[]{} : Arrays.copyOfRange(rawArgs, 1, rawArgs.length)));
                    } catch (Exception e) {
                        response.accept("An exception has occurred while running this command: " + e);
                    }
                }
            }
        }
    }
}
