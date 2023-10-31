package org.mangorage.filehost;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class zMain {
    public static void main(String[] args) throws IOException {
        ByteArrayOutputStream outByte = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outByte);
        dataOutputStream.writeUTF("HELLO");
        dataOutputStream.writeInt(100);
        dataOutputStream.writeFloat(10.124f);
        dataOutputStream.writeDouble(10.213d);
        dataOutputStream.writeLong(100L);
        dataOutputStream.writeBoolean(true);
        dataOutputStream.writeChar('A');
        dataOutputStream.writeShort(100);
        dataOutputStream.writeUTF("WORLD!");


        DataInputStream DIS = new DataInputStream(new ByteArrayInputStream(outByte.toByteArray()));
        System.out.println(DIS.readUTF());
        System.out.println(DIS.readInt());
        System.out.println(DIS.readFloat());
        System.out.println(DIS.readDouble());
        System.out.println(DIS.readLong());
        System.out.println(DIS.readBoolean());
        System.out.println(DIS.readChar());
        System.out.println(DIS.readShort());
        System.out.println(DIS.readUTF());
    }
}
