package org.agilewiki.jactor2.core.impl;

import org.agilewiki.jactor2.core.plant.ServiceClosedException;
import org.agilewiki.jactor2.core.reactors.Closeable;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public interface CloseableImpl extends AutoCloseable {
    public void addReactor(final ReactorImpl _reactorImpl) throws Exception;
    public void removeReactor(final ReactorImpl _reactorImpl);
}
