package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.plant.ServiceClosedException;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Closeable extends BladeBase implements AutoCloseable {
    private Set<Closer> closers = Collections.newSetFromMap(new ConcurrentHashMap<Closer, Boolean>(8, 0.9f, 1));

    private volatile boolean closing;

    public void initialize(final Reactor _reactor) throws Exception {
        _initialize(_reactor);
    }

    public void addCloser(final Closer _closer) throws Exception {
        if (closing)
            throw new ServiceClosedException();
        closers.add(_closer);
    }

    public void removeCloser(final Closer _closer) {
        if (closing)
            return;
        closers.remove(_closer);
    }

    @Override
    public void close() throws Exception {
        closing = true;
        Iterator<Closer> it = closers.iterator();
        while (it.hasNext()) {
            Closer closer = it.next();
            closer.removeCloseable(this);
        }
    }
}
