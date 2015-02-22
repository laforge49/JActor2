package org.agilewiki.jactor2.core.reactors.closeable.impl;

import org.agilewiki.jactor2.core.reactors.ReactorClosedException;
import org.agilewiki.jactor2.core.reactors.closeable.Closeable;
import org.agilewiki.jactor2.core.reactors.impl.ReactorImpl;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Implements multiple dependencies.
 */
public class CloseableImplImpl implements CloseableImpl {
    private static volatile int nextHash;

    private final Closeable closeable;

    /** Our hashcode. */
    private final int hashCode = nextHash++;

    private final ConcurrentHashMap<ReactorImpl, Boolean> closers = new ConcurrentHashMap<ReactorImpl, Boolean>(
            8, 0.9f, 1);

    private volatile boolean closing;

    /**
     * Create a closeableImpl for a closeable.
     * @param _closeable    The closeable that will hold a reference to this implementation.
     */
    public CloseableImplImpl(final Closeable _closeable) {
        closeable = _closeable;
    }

    /** Redefines the hashcode for a faster hashing. */
    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public void addReactor(final ReactorImpl _reactorImpl) {
        if (closing) {
            throw new ReactorClosedException("Closeable is closed");
        }
        closers.put(_reactorImpl, Boolean.TRUE);
    }

    @Override
    public void removeReactor(final ReactorImpl _reactorImpl) {
        if (closing) {
            return;
        }
        closers.remove(_reactorImpl);
    }

    @Override
    public void close() throws Exception {
        closing = true;
        for (final ReactorImpl reactorImpl : closers.keySet()) {
            reactorImpl.removeCloseable(closeable);
        }
    }
}
