package org.mangorage.filehost.networking.packets;

import java.io.DataOutputStream;
import java.io.IOException;

public interface IPacket {

    void handle();

    void encode(DataOutputStream data) throws IOException;

    Class<?> getType();
}
