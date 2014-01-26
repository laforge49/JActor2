package org.agilewiki.jactor2.core.closeable;

import org.agilewiki.jactor2.core.impl.ReactorImpl;
import org.agilewiki.jactor2.core.reactors.ReactorClosedException;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CloseableImpl1 implements CloseableImpl {
    private final Closeable closeable;

    private Set<ReactorImpl> closers =
            Collections.newSetFromMap(new ConcurrentHashMap<ReactorImpl, Boolean>(8, 0.9f, 1));

    private volatile boolean closing;

    public CloseableImpl1(final Closeable _closeable) {
        closeable = _closeable;
    }

    @Override
    public void addReactor(final ReactorImpl _reactorImpl) throws Exception {
        if (closing)
            throw new ReactorClosedException();
        closers.add(_reactorImpl);
    }

    @Override
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
