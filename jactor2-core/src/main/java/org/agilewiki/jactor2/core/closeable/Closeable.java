package org.agilewiki.jactor2.core.closeable;

public interface Closeable extends AutoCloseable {
    CloseableImpl asCloseableImpl();
}
