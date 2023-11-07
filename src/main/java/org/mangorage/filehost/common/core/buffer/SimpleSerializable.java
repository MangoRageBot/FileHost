package org.mangorage.filehost.common.core.buffer;

public interface SimpleSerializable<T> {
    T deserialize(SimpleByteBuffer buffer);

    void serialize(T object, SimpleByteBuffer buffer);
}
