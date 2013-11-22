package org.agilewiki.jactor2.core.util;

import com.google.common.collect.MapMaker;
import org.agilewiki.jactor2.core.facilities.Plant;
import org.agilewiki.jactor2.core.facilities.ServiceClosedException;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.slf4j.Logger;

import java.util.*;

abstract public class CloserBase extends CloseableBase implements Closer {

    /**
     * A set of Closeable objects.
     * Can only be accessed via a request to the facility.
     */
    private Set<Closeable> closeables;

    /**
     * Returns true when the first phase of closing has begun.
     *
     * @return True when the first phase of closing has begun.
     */
    abstract protected boolean startedClosing();

    /**
     * Performs the second phase of closing.
     */
    abstract protected void close2() throws Exception;

    /**
     * Returns the logger.
     *
     * @return A logger.
     */
    abstract public Logger getLog();

    /**
     * Returns the CloseableSet. Creates it if needed.
     */
    protected final Set<Closeable> getCloseableSet() {
        if (closeables == null) {
            closeables = Collections.newSetFromMap((Map)
                    new MapMaker().concurrencyLevel(1).weakKeys().makeMap());
        }
        return closeables;
    }

    @Override
    public boolean addCloseable(final Closeable _closeable) throws ServiceClosedException {
        if (startedClosing())
            throw new ServiceClosedException();
        if (!getCloseableSet().add(_closeable))
            return false;
        _closeable.addCloser(CloserBase.this);
        return true;
    }

    @Override
    public boolean removeCloseable(final Closeable _closeable) {
        if (closeables == null)
            return false;
        if (!closeables.remove(_closeable))
            return false;
        _closeable.removeCloser(CloserBase.this);
        return true;
    }

    protected void closeAll() throws Exception {
        if (closeables == null) {
            close2();
            return;
        }
        Iterator<Closeable> it = closeables.iterator();
        while (it.hasNext()) {
            Closeable closeable = it.next();
            try {
                closeable.close();
            } catch (final Throwable t) {
                if (closeable != null && Plant.DEBUG) {
                    getLog().warn("Error closing a " + closeable.getClass().getName(), t);
                }
            }
        }
        if (closeables.isEmpty())
            close2();
        else
            throw new IllegalStateException();
    }
}
