package org.mangorage.filehost.common.core;

import org.mangorage.filehost.common.core.buffer.SimpleSerializable;

import java.util.HashMap;

public class ObjectSerializationManager {
    /**
    public record Container(int value) {}
    public record Example(String result, Container container) {}


    private static final HashMap<Class<?>, SimpleSerializable<?>> SERIALIZERS = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> SimpleSerializable<T> getSerializer(Class<T> clazz) {
        return (SimpleSerializable<T>) SERIALIZERS.get(clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T> SimpleSerializable<T> getSerializer(T object) {
        return (SimpleSerializable<T>) getSerializer(object.getClass());
    }

    public static synchronized <T> void registerSerializer(Class<T> clazz, SimpleSerializable<T> serializable) {
        if (SERIALIZERS.containsKey(clazz))
            throw new IllegalStateException("Already have a Serializer for %s".formatted(clazz.getName()));
        SERIALIZERS.put(clazz, serializable);
    }


    static {
        registerSerializer(Example.class, new SimpleSerializable<>() {
            @Override
            public Example deserialize(SimpleByteBuffer buffer) {
                return new Example(buffer.readString(), buffer.readObject(Container.class));
            }

            @Override
            public void serialize(Example object, SimpleByteBuffer buffer) {
                buffer.writeString(object.result());
                buffer.writeObject(object.container());
            }
        });

        registerSerializer(Container.class, new SimpleSerializable<>() {

            @Override
            public Container deserialize(SimpleByteBuffer buffer) {
                return new Container(buffer.readInt());
            }

            @Override
            public void serialize(Container object, SimpleByteBuffer buffer) {
                buffer.writeInt(object.value());
            }
        });
    }

    public static void main(String[] args) {
        SimpleByteBuffer buffer = new SimpleByteBuffer();
        Example example = new Example("SimpleBotExample", new Container(1000));
        buffer.writeObject(example);

        Example cool = buffer.readObject(Example.class);
        System.out.println(cool.result());
        System.out.println(cool.container().value());
    }
    **/
}
