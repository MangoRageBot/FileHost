package org.mangorage.filehost.core.simplebbuffer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SimpleByteBuffer {
    private final SimpleByteArrayOutputStream BOS; // Write
    private final SimpleByteArrayInputStream BIS; // Read

    private final DataOutputStream OS; // Write
    private final DataInputStream IS; // Read

    public SimpleByteBuffer() {
        this(32);
    }

    public SimpleByteBuffer(int size) {
        this(new byte[size]);
    }

    public SimpleByteBuffer(byte[] array) {
        this.BIS = new SimpleByteArrayInputStream(array);
        this.BOS = new SimpleByteArrayOutputStream(array, BIS::update);
        this.OS = new DataOutputStream(BOS);
        this.IS = new DataInputStream(BIS);
    }

    public String readString() {
        try {
            return IS.readUTF();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeString(String string) throws IOException {
        OS.writeUTF(string);
        BOS.check();
    }

    public int readInt() throws IOException {
        return IS.readInt();
    }

    public void writeInt(int integer) throws IOException {
        OS.writeInt(integer);
        BOS.check();
    }

    public <E extends Enum> void writeEnum(E Enum) throws IOException {
        OS.writeInt(Enum.ordinal());
        BOS.check();
    }

    public <E extends Enum> E readEnum(Class<E> enumClass) throws IOException {
        int ordinal = IS.readInt();
        E[] enumConstants = enumClass.getEnumConstants();
        if (ordinal >= 0 && ordinal < enumConstants.length) {
            return enumConstants[ordinal];
        }
        return null;
    }

    public void writeBytes(byte[] array) throws IOException {
        OS.writeInt(array.length);
        OS.write(array);
        BOS.check();
    }

    public byte[] readBytes() throws IOException {
        int length = IS.readInt();
        byte[] array = new byte[length];
        IS.readFully(array, 0, length);
        return array;
    }

    public byte[] toWriteBytes() {
        return BOS.toByteArray();
    }

    public byte[] toReadBytes() {
        return BIS.toByteArray();
    }

    public void reset() {
        BIS.reset();
        BOS.reset();
    }

    public static void main(String[] args) {
        try {
            var a = new SimpleByteBuffer(1);
            a.writeString("WOOOOO! I ate pizza for dinner!1");
            a.reset();
            a.writeString("NOOO!");
            System.out.println(a.readString());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("An issue occured");
        }
    }
}
