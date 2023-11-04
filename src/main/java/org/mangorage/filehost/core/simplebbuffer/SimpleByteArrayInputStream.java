package org.mangorage.filehost.core.simplebbuffer;

import java.io.ByteArrayInputStream;
import java.util.Arrays;

public class SimpleByteArrayInputStream extends ByteArrayInputStream {

    public SimpleByteArrayInputStream(byte[] buf) {
        super(buf);
    }

    protected void update(byte[] buf, int count) {
        this.buf = buf;
        this.count = count;
    }

    public synchronized byte[] toByteArray() {
        return Arrays.copyOf(buf, count);
    }
}
