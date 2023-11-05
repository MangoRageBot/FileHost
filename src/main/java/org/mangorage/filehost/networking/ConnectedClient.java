package org.mangorage.filehost.networking;

import java.net.InetSocketAddress;

public class ConnectedClient {
    private final InetSocketAddress address;
    private final String username;

    public ConnectedClient(InetSocketAddress address, String username) {
        this.address = address;
        this.username = username;
    }

    public InetSocketAddress getAddress() {
        return address;
    }
    public String getUsername() {
        return username;
    }
}
