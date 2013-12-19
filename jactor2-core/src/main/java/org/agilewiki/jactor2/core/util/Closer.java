package org.agilewiki.jactor2.core.util;

public interface Closer {

    boolean addCloseable(final Closeable _closeable) throws Exception;
    boolean removeCloseable(final Closeable _closeable);
}
