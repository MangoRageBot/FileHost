package org.mangorage.filehost.networking;

import java.net.InetSocketAddress;

public class ConnectedClient {
    private final InetSocketAddress address;
    private boolean sentFiles = false;

    public ConnectedClient(InetSocketAddress address) {
        this.address = address;
    }

    public InetSocketAddress getAddress() {
        return address;
    }
}
