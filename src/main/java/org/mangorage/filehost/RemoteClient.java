package org.mangorage.filehost;

import org.mangorage.filehost.gui.Window;
import org.mangorage.filehost.networking.packets.core.Packets;

import java.net.SocketException;

public class RemoteClient {
    public static void main(String[] args) throws SocketException {
        Packets.init();
        new Client(args.length > 0 ? args[0] : "24.23.42.191").start();
        Window.create();
    }
}
