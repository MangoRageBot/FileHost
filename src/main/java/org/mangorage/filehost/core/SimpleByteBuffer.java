package org.mangorage.filehost.core;

import org.mangorage.filehost.networking.Side;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class SimpleByteBuffer {
    private byte[] data;
    private int writeOffset;
    private int readOffset;

    public SimpleByteBuffer() {
        this(32);
    }

    public SimpleByteBuffer(int initialCapacity) {
        data = new byte[initialCapacity];
        writeOffset = 0;
        readOffset = 0;
    }

    public SimpleByteBuffer(byte[] bytes) {
        data = Arrays.copyOf(bytes, bytes.length);
        writeOffset = bytes.length;
        readOffset = 0;
    }

    private void ensureCapacity(int bytesToWrite) {
        if (writeOffset + bytesToWrite > data.length) {
            int newCapacity = Math.max(data.length * 2, writeOffset + bytesToWrite);
            data = Arrays.copyOf(data, newCapacity);
        }
    }

    public void writeInt(int value) {
        ensureCapacity(4);
        ByteBuffer buffer = ByteBuffer.wrap(data, writeOffset, 4);
        buffer.putInt(value);
        writeOffset += 4;
    }

    public Integer readInt() {
        if (readOffset + 4 <= writeOffset) {
            ByteBuffer buffer = ByteBuffer.wrap(data, readOffset, 4);
            int value = buffer.getInt();
            readOffset += 4;
            return value;
        } else {
            System.err.println("Not enough data to read an integer.");
            return null;
        }
    }

    public void writeByte(byte value) {
        ensureCapacity(1);
        data[writeOffset] = value;
        writeOffset++;
    }

    public Byte readByte() {
        if (readOffset < writeOffset) {
            byte value = data[readOffset];
            readOffset++;
            return value;
        } else {
            System.err.println("Not enough data to read a byte.");
            return null;
        }
    }

    public void writeBoolean(boolean value) {
        writeByte((byte) (value ? 1 : 0));
    }

    public Boolean readBoolean() {
        Byte byteValue = readByte();
        return byteValue != null ? byteValue != 0 : null;
    }

    public void writeShort(short value) {
        ensureCapacity(2);
        ByteBuffer buffer = ByteBuffer.wrap(data, writeOffset, 2);
        buffer.putShort(value);
        writeOffset += 2;
    }

    public Short readShort() {
        if (readOffset + 2 <= writeOffset) {
            ByteBuffer buffer = ByteBuffer.wrap(data, readOffset, 2);
            short value = buffer.getShort();
            readOffset += 2;
            return value;
        } else {
            System.err.println("Not enough data to read a short.");
            return null;
        }
    }

    public void writeLong(long value) {
        ensureCapacity(8);
        ByteBuffer buffer = ByteBuffer.wrap(data, writeOffset, 8);
        buffer.putLong(value);
        writeOffset += 8;
    }

    public Long readLong() {
        if (readOffset + 8 <= writeOffset) {
            ByteBuffer buffer = ByteBuffer.wrap(data, readOffset, 8);
            long value = buffer.getLong();
            readOffset += 8;
            return value;
        } else {
            System.err.println("Not enough data to read a long.");
            return null;
        }
    }

    public void writeFloat(float value) {
        ensureCapacity(4);
        ByteBuffer buffer = ByteBuffer.wrap(data, writeOffset, 4);
        buffer.putFloat(value);
        writeOffset += 4;
    }

    public Float readFloat() {
        if (readOffset + 4 <= writeOffset) {
            ByteBuffer buffer = ByteBuffer.wrap(data, readOffset, 4);
            float value = buffer.getFloat();
            readOffset += 4;
            return value;
        } else {
            System.err.println("Not enough data to read a float.");
            return null;
        }
    }

    public void writeDouble(double value) {
        ensureCapacity(8);
        ByteBuffer buffer = ByteBuffer.wrap(data, writeOffset, 8);
        buffer.putDouble(value);
        writeOffset += 8;
    }

    public Double readDouble() {
        if (readOffset + 8 <= writeOffset) {
            ByteBuffer buffer = ByteBuffer.wrap(data, readOffset, 8);
            double value = buffer.getDouble();
            readOffset += 8;
            return value;
        } else {
            System.err.println("Not enough data to read a double.");
            return null;
        }
    }

    public void writeChar(char value) {
        ensureCapacity(2);
        ByteBuffer buffer = ByteBuffer.wrap(data, writeOffset, 2);
        buffer.putChar(value);
        writeOffset += 2;
    }

    public Character readChar() {
        if (readOffset + 2 <= writeOffset) {
            ByteBuffer buffer = ByteBuffer.wrap(data, readOffset, 2);
            char value = buffer.getChar();
            readOffset += 2;
            return value;
        } else {
            System.err.println("Not enough data to read a char.");
            return null;
        }
    }

    public void writeBytes(byte[] bytes) {
        writeInt(bytes.length);
        ensureCapacity(bytes.length);
        System.arraycopy(bytes, 0, data, writeOffset, bytes.length);
        writeOffset += bytes.length;
    }

    public byte[] readBytes() {
        Integer length = readInt();
        if (length != null && readOffset + length <= writeOffset) {
            byte[] result = Arrays.copyOfRange(data, readOffset, readOffset + length);
            readOffset += length;
            return result;
        } else {
            System.err.println("Not enough data to read the byte array.");
            return null;
        }
    }

    // Write a string to the byte array
    public void writeString(String value) {
        byte[] stringBytes = value.getBytes(StandardCharsets.UTF_8);
        writeInt(stringBytes.length);
        ensureCapacity(stringBytes.length);
        System.arraycopy(stringBytes, 0, data, writeOffset, stringBytes.length);
        writeOffset += stringBytes.length;
    }

    // Read a string from the byte array
    public String readString() {
        Integer length = readInt();
        if (length != null && readOffset + length <= writeOffset) {
            byte[] stringBytes = Arrays.copyOfRange(data, readOffset, readOffset + length);
            readOffset += length;
            return new String(stringBytes, StandardCharsets.UTF_8);
        } else {
            System.err.println("Not enough data to read the string.");
            return null;
        }
    }

    public <E extends Enum> void writeEnum(E Enum) {
        writeInt(Enum.ordinal());
    }

    public <E extends Enum> E readEnum(Class<E> enumClass) {
        int ordinal = readInt();
        E[] enumConstants = enumClass.getEnumConstants();
        if (ordinal >= 0 && ordinal < enumConstants.length) {
            return enumConstants[ordinal];
        }
        return null;
    }

    public byte[] toBytes() {
        return Arrays.copyOf(data, data.length);
    }



    public static void main(String[] args) {
        SimpleByteBuffer betterByteBuffer = new SimpleByteBuffer();
        betterByteBuffer.writeInt(18384334);
        betterByteBuffer.writeString("TESTING 1");
        betterByteBuffer.writeEnum(Side.SERVER);

        SimpleByteBuffer c = new SimpleByteBuffer();
        c.writeBytes(betterByteBuffer.toBytes());

        SimpleByteBuffer d = new SimpleByteBuffer(c.readBytes());
        System.out.println(d.readInt());
        System.out.println(d.readString());
        System.out.println(d.readEnum(Side.class));
    }
}