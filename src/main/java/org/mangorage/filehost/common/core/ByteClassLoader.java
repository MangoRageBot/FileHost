package org.mangorage.filehost.common.core;

public class ByteClassLoader extends ClassLoader {
    public Class<?> loadClassFromBytes(String className, byte[] classBytes) {
        return defineClass(className, classBytes, 0, classBytes.length);
    }
}