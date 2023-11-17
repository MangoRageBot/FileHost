package org.mangorage.filehost.server;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;

public class ConnectedClient {
    private final InetSocketAddress address;
    private final String username;
    private Channel activeChannel;
    private long lastPing;


    public ConnectedClient(InetSocketAddress address, String username) {
        this.address = address;
        this.username = username;
        this.lastPing = System.currentTimeMillis() + 5000;
    }

    public void updateChannel(Channel channel) {
        this.activeChannel = channel;
    }

    public Channel getActiveChannel() {
        return activeChannel;
    }

    public InetSocketAddress getAddress() {
        return address;
    }
    public String getUsername() {
        return username;
    }
    public void ping() {
        this.lastPing = System.currentTimeMillis();
    }

    public long getLastPing() {
        return lastPing;
    }
}
