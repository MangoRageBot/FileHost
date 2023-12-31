package org.mangorage.filehost;

import io.netty.buffer.Unpooled;
import org.mangorage.filehost.common.core.buffer.SimpleByteBuf;
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

            COMMANDS.put("trick", a -> {
                if (a.length > 3) {
                    String cmdType = a[0];
                    String name = a[1];
                    StringBuilder result = new StringBuilder();
                    for (int i = 2; i < a.length; i++) {
                        result.append(a[i]).append(" ");
                    }

                    if (COMMANDS.containsKey(name))
                        return "Trick already exists!";

                    if (cmdType.equalsIgnoreCase("-add")) {
                        COMMANDS.put(name, b -> result.toString());
                        return "Created New trick";
                    }

                    return """
                            !trick -add <ID> <Content>
                            """;
                } else {
                    return "Please provide more args";
                }
            });
        }
    }



    public static DatagramPacket createBasicPacket(int packetID, int sideID, InetSocketAddress address, Consumer<SimpleByteBuf> packetBuffer) {
        SimpleByteBuf header = new SimpleByteBuf(Unpooled.buffer());
        SimpleByteBuf packet = new SimpleByteBuf(Unpooled.buffer());
        header.writeInt(packetID);
        header.writeInt(sideID);
        packetBuffer.accept(packet);
        header.writeByteArray(packet.array());
        byte[] data = header.array();
        return new DatagramPacket(data, data.length, address);
    }


    public static void main(String[] args) throws IOException {
        try (DatagramSocket socket = new DatagramSocket()) {
            var address = new InetSocketAddress("localhost", 25564);

            // Handle handshake
            socket.send(createBasicPacket(2, 1, address, d -> {
                d.writeString("MangoBot");
                d.writeString("12345!");
            }));


            // Handle chat....
            // We respond if someone types in a command...
            Consumer<String> response = s -> {
                try {
                    socket.send(createBasicPacket(3, 1, address, d -> {
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
                SimpleByteBuf buffer = new SimpleByteBuf(Unpooled.wrappedBuffer(header.getData()));
                int packetID = buffer.readInt();
                Side side = buffer.readEnum(Side.class);
                if (side == Side.SERVER && packetID == 3) {
                    SimpleByteBuf packetData = new SimpleByteBuf(Unpooled.wrappedBuffer(buffer.readByteArray()));
                    String username = packetData.readString();
                    String message = packetData.readString();

                    if (username.equals("System") || username.equals("MangoBot")) continue;

                    String[] rawArgs = message.split(" ");
                    if (rawArgs.length == 0) continue;
                    String cmd = rawArgs[0];
                    System.out.println(cmd);
                    if (!cmd.startsWith("!")) continue;
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
