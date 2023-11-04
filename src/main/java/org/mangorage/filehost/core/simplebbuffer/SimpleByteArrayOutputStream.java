package org.mangorage.filehost.core.simplebbuffer;


import java.io.ByteArrayOutputStream;
import java.util.function.BiConsumer;

public class SimpleByteArrayOutputStream extends ByteArrayOutputStream {
    private byte[] array;
    private final BiConsumer<byte[], Integer> updated;
    public SimpleByteArrayOutputStream(byte[] arr, BiConsumer<byte[], Integer> updated) {
        this.buf = arr;
        this.array = arr;
        this.updated = updated;
    }

    protected void check() {
        if (this.buf.length >= array.length) {
            this.array = this.buf;
            updated.accept(this.buf, this.count);
        }
    }
}
