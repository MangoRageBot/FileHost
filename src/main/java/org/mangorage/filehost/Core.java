package org.mangorage.filehost;

import org.mangorage.filehost.core.Constants;
import org.mangorage.filehost.networking.packets.core.Packets;

import java.net.SocketException;

public class Core {
    public static void main(String[] args) throws SocketException {
        Constants.init();
        if (args.length == 0 ) {
            System.out.println("java -jar FileHost.jar client/server <Optional: IP Default: 24.23.42.191>");
            return;
        }

        String type = args[0];
        String ip = "24.23.42.191";
        if (args.length > 1)
            ip = args[1];

        Packets.init();
        if (type.equalsIgnoreCase("client")) {
            Client.create(ip);
        } else if(type.equalsIgnoreCase("server")) {
            Server.create(Integer.parseInt(ip));
        }
    }
}
