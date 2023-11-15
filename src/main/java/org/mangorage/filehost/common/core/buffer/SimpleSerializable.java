package org.mangorage.filehost.common.core.buffer;

public interface SimpleSerializable<T> {
    T deserialize(SimpleByteBuf buffer);

    void serialize(T object, SimpleByteBuf buffer);
}
