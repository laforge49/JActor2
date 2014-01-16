package org.agilewiki.jactor2.core.impl;

import org.agilewiki.jactor2.core.plant.ServiceClosedException;
import org.agilewiki.jactor2.core.reactors.Closeable;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CloseableImpl implements AutoCloseable {
    private final Closeable closeable;

    private Set<ReactorImpl> closers =
            Collections.newSetFromMap(new ConcurrentHashMap<ReactorImpl, Boolean>(8, 0.9f, 1));

    private volatile boolean closing;

    public CloseableImpl(final Closeable _closeable) {
        closeable = _closeable;
    }

    public void addReactor(final ReactorImpl _reactorImpl) throws Exception {
        if (closing)
            throw new ServiceClosedException();
        closers.add(_reactorImpl);
    }

    public void removeReactor(final ReactorImpl _reactorImpl) {
        if (closing)
            return;
        closers.remove(_reactorImpl);
    }

    @Override
    public void close() throws Exception {
        closing = true;
        Iterator<ReactorImpl> it = closers.iterator();
        while (it.hasNext()) {
            ReactorImpl reactorImpl = it.next();
            reactorImpl.removeCloseable(closeable);
        }
    }
}
