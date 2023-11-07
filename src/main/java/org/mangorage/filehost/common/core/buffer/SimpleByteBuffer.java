package org.mangorage.filehost.common.core.buffer;

import org.mangorage.filehost.common.core.ObjectSerializationManager;
import org.mangorage.filehost.common.networking.Side;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;


// TODO: Incorporate Data Types so we can tell if what we are about to read is indeed the correct Data Type
// We want to make sure when we are calling readInt() that what we are calling is indeed an Integer
public class SimpleByteBuffer {
    public enum DataType {
        // 0x00, 0x01, 0x02, 0x03, 0x04, 0x05 are all taken. Cannot use these... 0xFF is for Uknown
        INT(0x06),
        DOUBLE(0x07),
        UNDEFINED(0xFE),
        UNKNOWN(0xFF);
        private final byte dataType;
        DataType(int dataType) {
            this.dataType = (byte) dataType;
        }

        public byte getByte() {
            return dataType;
        }

        public boolean isValid(byte b) {
            return dataType == b;
        }

        private static DataType getType(byte b) {
            for (DataType value : DataType.values()) {
                if (value.getByte() == b)
                    return value;
            }

            return UNKNOWN;
        }
    }


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
        writeByte((byte) ((value >> 24) & 0xFF));
        writeByte((byte) ((value >> 16) & 0xFF));
        writeByte((byte) ((value >> 8) & 0xFF));
        writeByte((byte) (value & 0xFF));
    }

    public Integer readInt() {
        if (readOffset + 4 <= writeOffset) {
            return ((readByte() & 0xFF) << 24) |
                    ((readByte() & 0xFF) << 16) |
                    ((readByte() & 0xFF) << 8) |
                    (readByte() & 0xFF);
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
        writeByte((byte) ((value >> 8) & 0xFF));
        writeByte((byte) (value & 0xFF));
    }

    public Short readShort() {
        if (readOffset + 2 <= writeOffset) {
            return (short)(((readByte() & 0xFF) << 8) | (readByte() & 0xFF));
        } else {
            System.err.println("Not enough data to read a short.");
            return null;
        }
    }

    public void writeLong(long value) {
        writeByte((byte) ((value >> 56) & 0xFF));
        writeByte((byte) ((value >> 48) & 0xFF));
        writeByte((byte) ((value >> 40) & 0xFF));
        writeByte((byte) ((value >> 32) & 0xFF));
        writeByte((byte) ((value >> 24) & 0xFF));
        writeByte((byte) ((value >> 16) & 0xFF));
        writeByte((byte) ((value >> 8) & 0xFF));
        writeByte((byte) (value & 0xFF));
    }

    public Long readLong() {
        if (readOffset + 8 <= writeOffset) {
            return  ((long)readByte() << 56) |
                    ((long)(readByte() & 0xFF) << 48) |
                    ((long)(readByte() & 0xFF) << 40) |
                    ((long)(readByte() & 0xFF) << 32) |
                    ((long)(readByte() & 0xFF) << 24) |
                    ((long)(readByte() & 0xFF) << 16) |
                    ((long)(readByte() & 0xFF) << 8) |
                    ((long)(readByte() & 0xFF));
        } else {
            System.err.println("Not enough data to read a long.");
            return null;
        }
    }

    public void writeFloat(float value) {
        int intBits = Float.floatToIntBits(value);
        writeByte((byte) ((intBits >> 24) & 0xFF));
        writeByte((byte) ((intBits >> 16) & 0xFF));
        writeByte((byte) ((intBits >> 8) & 0xFF));
        writeByte((byte) (intBits & 0xFF));
    }

    public Float readFloat() {
        if (readOffset + 4 <= writeOffset) {
            int intBits =
                    ((readByte() & 0xFF) << 24) |
                    ((readByte() & 0xFF) << 16) |
                    ((readByte() & 0xFF) << 8) |
                    (readByte() & 0xFF);
            return Float.intBitsToFloat(intBits);
        } else {
            System.err.println("Not enough data to read a float.");
            return null;
        }
    }

    public void writeDouble(double value) {
        long longBits = Double.doubleToLongBits(value);
        writeByte((byte) ((longBits >> 56) & 0xFF));
        writeByte((byte) ((longBits >> 48) & 0xFF));
        writeByte((byte) ((longBits >> 40) & 0xFF));
        writeByte((byte) ((longBits >> 32) & 0xFF));
        writeByte((byte) ((longBits >> 24) & 0xFF));
        writeByte((byte) ((longBits >> 16) & 0xFF));
        writeByte((byte) ((longBits >> 8) & 0xFF));
        writeByte((byte) (longBits & 0xFF));
    }

    public Double readDouble() {
        if (readOffset + 8 <= writeOffset) {
            long longBits =
                    ((long)readByte() << 56) |
                    ((long)(readByte() & 0xFF) << 48) |
                    ((long)(readByte() & 0xFF) << 40) |
                    ((long)(readByte() & 0xFF) << 32) |
                    ((long)(readByte() & 0xFF) << 24) |
                    ((long)(readByte() & 0xFF) << 16) |
                    ((long)(readByte() & 0xFF) << 8) |
                    ((long)(readByte() & 0xFF));
            return Double.longBitsToDouble(longBits);
        } else {
            System.err.println("Not enough data to read a double.");
            return null;
        }
    }

    public void writeChar(char value) {
        writeByte((byte) ((value >> 8) & 0xFF));
        writeByte((byte) (value & 0xFF));
    }

    public Character readChar() {
        if (readOffset + 2 <= writeOffset) {
            return (char)(((readByte() & 0xFF) << 8) | (readByte() & 0xFF));
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
        writeBytes(stringBytes);
    }

    // Read a string from the byte array
    public String readString() {
        byte[] bytes = readBytes();
        if (bytes != null) {
            return new String(bytes, StandardCharsets.UTF_8);
        } else {
            System.err.println("Not enough data to read string");
        }
        return null;
    }

    public <E extends Enum> void writeEnum(E Enum) {
        writeInt(Enum.ordinal());
    }

    public <E extends Enum> E readEnum(Class<E> enumClass) {
        int ordinal = readInt();
        E[] enumConstants = enumClass.getEnumConstants();
        if (ordinal >= 0 && ordinal < enumConstants.length) {
            return enumConstants[ordinal];
        } else {
            System.err.println("Not enough data to read enum %s".formatted(enumClass.getName()));
        }
        return null;
    }

    public <T> void writeObject(T object) {
        SimpleSerializable<T> serializable = ObjectSerializationManager.getSerializer(object);
        if (serializable != null) {
            serializable.serialize(object, this);
        } else {
            System.err.println("Unable to write Object. No serialization exists");
        }
    }

    public <T> T readObject(Class<T> clazz) {
        SimpleSerializable<T> serializable = ObjectSerializationManager.getSerializer(clazz);
        if (serializable != null) {
            return serializable.deserialize(this);
        } else {
            System.err.println("Unable to write Object. No serialization exists");
        }
        return null;
    }

    public byte[] toBytes() {
        return Arrays.copyOf(data, writeOffset);
    }

    public byte getNextReadableType() {
        return data[readOffset];
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
        SimpleByteBuffer buffer = new SimpleByteBuffer();
        buffer.writeInt(102);
        buffer.writeInt(983);
        buffer.writeInt(99);

        System.out.println(buffer.readInt());

        System.out.println(buffer.readInt());

        System.out.println(buffer.readInt());
    }

    public static void mainOld(String[] args) {
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