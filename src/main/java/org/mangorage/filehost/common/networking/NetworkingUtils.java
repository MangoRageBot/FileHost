package org.mangorage.filehost.common.networking;

import java.net.InetSocketAddress;

public class NetworkingUtils {
    public static String getIPString(InetSocketAddress address) {
        return "%s:%s".formatted(address.getHostString(), address.getPort());
    }
}
