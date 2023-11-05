package org.mangorage.filehost.core;

import org.mangorage.filehost.networking.Side;

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
        data[writeOffset] = (byte) ((value >> 24) & 0xFF);
        data[writeOffset + 1] = (byte) ((value >> 16) & 0xFF);
        data[writeOffset + 2] = (byte) ((value >> 8) & 0xFF);
        data[writeOffset + 3] = (byte) (value & 0xFF);
        writeOffset += 4;
    }

    public Integer readInt() {
        if (readOffset + 4 <= writeOffset) {
            int value = ((data[readOffset] & 0xFF) << 24) |
                    ((data[readOffset + 1] & 0xFF) << 16) |
                    ((data[readOffset + 2] & 0xFF) << 8) |
                    (data[readOffset + 3] & 0xFF);
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
        data[writeOffset] = (byte) ((value >> 8) & 0xFF);
        data[writeOffset + 1] = (byte) (value & 0xFF);
        writeOffset += 2;
    }

    public Short readShort() {
        if (readOffset + 2 <= writeOffset) {
            short value = (short)(((data[readOffset] & 0xFF) << 8) | (data[readOffset + 1] & 0xFF));
            readOffset += 2;
            return value;
        } else {
            System.err.println("Not enough data to read a short.");
            return null;
        }
    }

    public void writeLong(long value) {
        ensureCapacity(8);
        data[writeOffset] = (byte) ((value >> 56) & 0xFF);
        data[writeOffset + 1] = (byte) ((value >> 48) & 0xFF);
        data[writeOffset + 2] = (byte) ((value >> 40) & 0xFF);
        data[writeOffset + 3] = (byte) ((value >> 32) & 0xFF);
        data[writeOffset + 4] = (byte) ((value >> 24) & 0xFF);
        data[writeOffset + 5] = (byte) ((value >> 16) & 0xFF);
        data[writeOffset + 6] = (byte) ((value >> 8) & 0xFF);
        data[writeOffset + 7] = (byte) (value & 0xFF);
        writeOffset += 8;
    }

    public Long readLong() {
        if (readOffset + 8 <= writeOffset) {
            long value = ((long)data[readOffset] << 56) |
                    ((long)(data[readOffset + 1] & 0xFF) << 48) |
                    ((long)(data[readOffset + 2] & 0xFF) << 40) |
                    ((long)(data[readOffset + 3] & 0xFF) << 32) |
                    ((long)(data[readOffset + 4] & 0xFF) << 24) |
                    ((long)(data[readOffset + 5] & 0xFF) << 16) |
                    ((long)(data[readOffset + 6] & 0xFF) << 8) |
                    ((long)(data[readOffset + 7] & 0xFF));
            readOffset += 8;
            return value;
        } else {
            System.err.println("Not enough data to read a long.");
            return null;
        }
    }

    public void writeFloat(float value) {
        ensureCapacity(4);
        int intBits = Float.floatToIntBits(value);
        data[writeOffset] = (byte) ((intBits >> 24) & 0xFF);
        data[writeOffset + 1] = (byte) ((intBits >> 16) & 0xFF);
        data[writeOffset + 2] = (byte) ((intBits >> 8) & 0xFF);
        data[writeOffset + 3] = (byte) (intBits & 0xFF);
        writeOffset += 4;
    }

    public Float readFloat() {
        if (readOffset + 4 <= writeOffset) {
            int intBits = ((data[readOffset] & 0xFF) << 24) |
                    ((data[readOffset + 1] & 0xFF) << 16) |
                    ((data[readOffset + 2] & 0xFF) << 8) |
                    (data[readOffset + 3] & 0xFF);
            readOffset += 4;
            return Float.intBitsToFloat(intBits);
        } else {
            System.err.println("Not enough data to read a float.");
            return null;
        }
    }

    public void writeDouble(double value) {
        ensureCapacity(8);
        long longBits = Double.doubleToLongBits(value);
        data[writeOffset] = (byte) ((longBits >> 56) & 0xFF);
        data[writeOffset + 1] = (byte) ((longBits >> 48) & 0xFF);
        data[writeOffset + 2] = (byte) ((longBits >> 40) & 0xFF);
        data[writeOffset + 3] = (byte) ((longBits >> 32) & 0xFF);
        data[writeOffset + 4] = (byte) ((longBits >> 24) & 0xFF);
        data[writeOffset + 5] = (byte) ((longBits >> 16) & 0xFF);
        data[writeOffset + 6] = (byte) ((longBits >> 8) & 0xFF);
        data[writeOffset + 7] = (byte) (longBits & 0xFF);
        writeOffset += 8;
    }

    public Double readDouble() {
        if (readOffset + 8 <= writeOffset) {
            long longBits = ((long)data[readOffset] << 56) |
                    ((long)(data[readOffset + 1] & 0xFF) << 48) |
                    ((long)(data[readOffset + 2] & 0xFF) << 40) |
                    ((long)(data[readOffset + 3] & 0xFF) << 32) |
                    ((long)(data[readOffset + 4] & 0xFF) << 24) |
                    ((long)(data[readOffset + 5] & 0xFF) << 16) |
                    ((long)(data[readOffset + 6] & 0xFF) << 8) |
                    ((long)(data[readOffset + 7] & 0xFF));
            readOffset += 8;
            return Double.longBitsToDouble(longBits);
        } else {
            System.err.println("Not enough data to read a double.");
            return null;
        }
    }

    public void writeChar(char value) {
        ensureCapacity(2);
        data[writeOffset] = (byte) ((value >> 8) & 0xFF);
        data[writeOffset + 1] = (byte) (value & 0xFF);
        writeOffset += 2;
    }

    public Character readChar() {
        if (readOffset + 2 <= writeOffset) {
            char value = (char)(((data[readOffset] & 0xFF) << 8) | (data[readOffset + 1] & 0xFF));
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
        return Arrays.copyOf(data, writeOffset);
    }

    public void resetReadPosition() {
        readOffset = 0;
    }

    public void resetWritePosition() {
        writeOffset = 0;
    }

    public void clearAll() {
        resetReadPosition();
        resetWritePosition();
        data = new byte[32];
    }


    public static void main(String[] args) {
        SimpleByteBuffer betterByteBuffer = new SimpleByteBuffer(2_000_000_000);
        betterByteBuffer.writeInt(18384334);
        betterByteBuffer.writeString("TESTING 1");
        betterByteBuffer.writeEnum(Side.SERVER);
        betterByteBuffer.writeBoolean(false);
        betterByteBuffer.writeLong(1032L);
        short a = Short.MAX_VALUE;
        betterByteBuffer.writeShort(a);
        betterByteBuffer.writeFloat(10.02f);
        betterByteBuffer.writeDouble(1.42D);
        betterByteBuffer.writeChar('C');
        betterByteBuffer.writeString("WOOP!");

        SimpleByteBuffer c = new SimpleByteBuffer();
        c.writeBytes(betterByteBuffer.toBytes());

        SimpleByteBuffer d = new SimpleByteBuffer(c.readBytes());
        System.out.println(d.readInt());
        System.out.println(d.readString());
        System.out.println(d.readEnum(Side.class));
        System.out.println(d.readBoolean());
        System.out.println(d.readLong());
        System.out.println(d.readShort());
        System.out.println(d.readFloat());
        System.out.println(d.readDouble());
        System.out.println(d.readChar());
        System.out.println(d.readString());
        d.resetReadPosition();
        System.out.println(d.readInt());
        System.out.println(d.readString());
        System.out.println(d.readEnum(Side.class));
        System.out.println(d.readBoolean());
        d.resetWritePosition();
        d.resetReadPosition();
        d.writeInt(10);
        d.writeString("TEST");
        System.out.println(d.readInt());

        SimpleByteBuffer e = new SimpleByteBuffer(d.toBytes());
        System.out.println(e.readInt());
        System.out.println(e.readString());

        e.clearAll();
        e.writeString("OK");
        System.out.println(e.readString());
    }
}