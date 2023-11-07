package org.mangorage.filehost.common.networking.packets;

import org.mangorage.filehost.common.networking.Side;
import org.mangorage.filehost.common.core.ByteClassLoader;
import org.mangorage.filehost.common.core.buffer.SimpleByteBuffer;

import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;

public class ObjectPacket {

    private final String classname;
    private final byte[] classData;

    public ObjectPacket(SimpleByteBuffer data) {
        this(data.readString(), data.readBytes());
    }

    public ObjectPacket(String classname, byte[] classData) {
        this.classname = classname;
        this.classData = classData;
    }

    public void handle(InetSocketAddress origin, Side side) {
        System.out.println("Lol");
        ByteClassLoader classLoader = new ByteClassLoader();
        var clazz = classLoader.loadClassFromBytes(classname, classData);
        try {
            clazz.getConstructor().newInstance();
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void encode(SimpleByteBuffer data) {
        data.writeString(classname);
        data.writeBytes(classData);
    }
}
