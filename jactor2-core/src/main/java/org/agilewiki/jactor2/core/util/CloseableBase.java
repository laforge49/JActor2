package org.agilewiki.jactor2.core.util;

import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.plant.ServiceClosedException;
import org.agilewiki.jactor2.core.reactors.Reactor;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CloseableBase extends BladeBase implements Closeable {
    private Set<Closer> closers = Collections.newSetFromMap(new ConcurrentHashMap<Closer, Boolean>(8, 0.9f, 1));

    private volatile boolean closing;

    public void initialize(final Reactor _reactor) throws Exception {
        _initialize(_reactor);
    }

    @Override
    public void addCloser(final Closer _closer) throws ServiceClosedException {
        if (closing)
            throw new ServiceClosedException();
        closers.add(_closer);
    }

    @Override
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
