package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.impl.ReactorImpl;
import org.agilewiki.jactor2.core.plant.ServiceClosedException;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CloseableBase extends BladeBase implements AutoCloseable {
    private Set<ReactorImpl> closers = Collections.newSetFromMap(new ConcurrentHashMap<ReactorImpl, Boolean>(8, 0.9f, 1));

    private volatile boolean closing;

    public void initialize(final Reactor _reactor) throws Exception {
        _initialize(_reactor);
    }

    public void addCloser(final ReactorImpl _reactorImpl) throws Exception {
        if (closing)
            throw new ServiceClosedException();
        closers.add(_reactorImpl);
    }

    public void removeCloser(final ReactorImpl _reactorImpl) {
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
            reactorImpl.removeCloseable(this);
        }
    }
}
