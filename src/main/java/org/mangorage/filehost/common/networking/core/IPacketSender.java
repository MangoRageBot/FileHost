package org.mangorage.filehost.common.networking.core;

import org.mangorage.filehost.common.networking.Side;

public interface IPacketSender {
    Side getSide();
    void send(Packet packet);
}
