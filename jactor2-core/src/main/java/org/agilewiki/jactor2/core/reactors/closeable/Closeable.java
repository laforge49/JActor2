package org.agilewiki.jactor2.core.reactors.closeable;

import org.agilewiki.jactor2.core.reactors.closeable.impl.CloseableImpl;

/**
 * An object that depends on 1 or more reactors, so that when a reactor is closed the object is also closed.
 * A closeable must carry a reference to its CloseableImpl and when closed should also close its CloseableImpl.
 * A dependency on a reactor is created by calling addCloseable on that reactor.
 */
public interface Closeable extends AutoCloseable {
    /**
     * Returns the closealbe's closeableImpl reference.
     *
     * @return The closeableImpl.
     */
    CloseableImpl asCloseableImpl();
}
