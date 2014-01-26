package org.agilewiki.jactor2.core.closeable;

import org.agilewiki.jactor2.core.closeable.CloseableImpl;

public interface Closeable extends AutoCloseable {
    CloseableImpl asCloseableImpl();
}
