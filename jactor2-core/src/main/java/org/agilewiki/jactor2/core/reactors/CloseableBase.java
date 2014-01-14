package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.impl.CloserImpl;
import org.agilewiki.jactor2.core.plant.ServiceClosedException;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CloseableBase extends BladeBase implements AutoCloseable {
    private Set<CloserImpl> closers = Collections.newSetFromMap(new ConcurrentHashMap<CloserImpl, Boolean>(8, 0.9f, 1));

    private volatile boolean closing;

    public void initialize(final Reactor _reactor) throws Exception {
        _initialize(_reactor);
    }

    public void addCloser(final CloserImpl _closer) throws Exception {
        if (closing)
            throw new ServiceClosedException();
        closers.add(_closer);
    }

    public void removeCloser(final CloserImpl _closer) {
        if (closing)
            return;
        closers.remove(_closer);
    }

    @Override
    public void close() throws Exception {
        closing = true;
        Iterator<CloserImpl> it = closers.iterator();
        while (it.hasNext()) {
            CloserImpl closer = it.next();
            closer.removeCloseable(this);
        }
    }
}
