package org.agilewiki.jactor2.core.impl;

import com.google.common.collect.MapMaker;
import org.agilewiki.jactor2.core.plant.PlantConfiguration;
import org.agilewiki.jactor2.core.plant.Recovery;
import org.agilewiki.jactor2.core.plant.Scheduler;
import org.agilewiki.jactor2.core.plant.ServiceClosedException;
import org.agilewiki.jactor2.core.reactors.CloseableBase;
import org.agilewiki.jactor2.core.reactors.Closer;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

abstract public class CloserImpl extends CloseableBase implements Closer {
    public Recovery recovery;
    public Scheduler scheduler;

    /**
     * A set of CloseableBase objects.
     * Can only be accessed via a request to the facility.
     */
    private Set<CloseableBase> closeables;

    public CloserImpl(final Recovery _recovery, final Scheduler _scheduler) {
        PlantConfiguration plantConfiguration = PlantImpl.getSingleton().getPlantConfiguration();
        recovery = _recovery == null ? plantConfiguration.getRecovery() : _recovery;
        scheduler = _scheduler == null ? plantConfiguration.getScheduler() : _scheduler;
    }

    @Override
    public CloserImpl asCloserImpl() {
        return this;
    }

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
    abstract public Logger getLogger();

    /**
     * Returns the CloseableSet. Creates it if needed.
     *
     * @return The CloseableSet.
     */
    protected final Set<CloseableBase> getCloseableSet() {
        if (closeables == null) {
            closeables = Collections.newSetFromMap((Map)
                    new MapMaker().concurrencyLevel(1).weakKeys().makeMap());
        }
        return closeables;
    }

    @Override
    public boolean addCloseable(final CloseableBase _closeable) throws Exception {
        if (startedClosing())
            throw new ServiceClosedException();
        if (this == _closeable)
            return false;
        if (!getCloseableSet().add(_closeable))
            return false;
        _closeable.addCloser(CloserImpl.this);
        return true;
    }

    @Override
    public boolean removeCloseable(final CloseableBase _closeable) {
        if (closeables == null)
            return false;
        if (!closeables.remove(_closeable))
            return false;
        _closeable.removeCloser(CloserImpl.this);
        return true;
    }

    protected void closeAll() throws Exception {
        if (closeables == null) {
            close2();
            return;
        }
        Iterator<CloseableBase> it = closeables.iterator();
        while (it.hasNext()) {
            CloseableBase closeable = it.next();
            try {
                closeable.close();
            } catch (final Throwable t) {
                if (closeable != null && PlantImpl.DEBUG) {
                    getLogger().warn("Error closing a " + closeable.getClass().getName(), t);
                }
            }
        }
        it = closeables.iterator();
        while (it.hasNext()) {
            CloseableBase closeable = it.next();
            getLogger().warn("still has closable: " + this + "\n" + closeable);
        }
        close2();
    }
}
