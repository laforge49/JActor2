package org.agilewiki.jactor2.core.impl;

import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.plant.MigrationException;
import org.agilewiki.jactor2.core.plant.PoolThread;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Common code for NonBlockingReactor and IsolationReactor, which are not bound to a thread.
 * <p>
 * UnboundReactorImpl supports thread migration only between instances of this class.
 * </p>
 */
abstract public class UnboundReactorImpl extends ReactorImpl {

    /**
     * A reference to the thread that is executing this targetReactor.
     */
    protected final AtomicReference<PoolThread> threadReference = new AtomicReference<PoolThread>();

    /**
     * The object to be run when the inbox is emptied and before the threadReference is cleared.
     */
    private final Runnable onIdle;

    /**
     * Create an unbound targetReactor.
     *
     * @param _facility              The facility of this targetReactor.
     * @param _initialOutboxSize     Initial size of the outbox for each unique message destination.
     * @param _initialLocalQueueSize The initial number of slots in the doLocal queue.
     * @param _onIdle                Object to be run when the inbox is emptied, or null.
     */
    public UnboundReactorImpl(final Facility _facility,
                              final int _initialOutboxSize, final int _initialLocalQueueSize,
                              final Runnable _onIdle) throws Exception {
        super(_facility, _initialOutboxSize, _initialLocalQueueSize);
        onIdle = _onIdle;
    }

    @Override
    public AtomicReference<PoolThread> getThreadReference() {
        return threadReference;
    }

    @Override
    public boolean isIdler() {
        return onIdle != null;
    }

    @Override
    protected void notBusy() throws Exception {
        if ((onIdle != null) && inbox.isIdle()) {
            flush(true);
            onIdle.run();
        }
        flush(true);
    }

    @Override
    protected void afterAdd() throws Exception {
        if (threadReference == null) {
            System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.err.println("this=" + this);
            System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            throw new NullPointerException();
        }
        if (threadReference.get() == null) {
            PlantImpl.getSingleton().asPlantImpl().submit(this);
        }
    }

    /**
     * Flushes buffered messages, if any.
     * Returns true if there was any.
     *
     * @param _mayMigrate True when thread migration is allowed.
     * @return True when one or more buffered request/result was delivered.
     */
    public boolean flush(final boolean _mayMigrate) throws Exception {
        boolean result = false;
        final Iterator<Map.Entry<ReactorImpl, ArrayDeque<RequestImpl>>> iter = outbox
                .getIterator();
        if (iter != null) {
            while (iter.hasNext()) {
                result = true;
                final Map.Entry<ReactorImpl, ArrayDeque<RequestImpl>> entry = iter
                        .next();
                final ReactorImpl target = entry.getKey();
                final ArrayDeque<RequestImpl> messages = entry.getValue();
                iter.remove();
                if (!iter.hasNext() && _mayMigrate
                        && (target instanceof UnboundReactorImpl)) {
                    if (!target.isRunning()) {
                        final PoolThread currentThread = threadReference.get();
                        final UnboundReactorImpl targ = (UnboundReactorImpl) target;
                        final AtomicReference<PoolThread> targetThreadReference = targ
                                .getThreadReference();
                        if ((targetThreadReference.get() == null)
                                && targetThreadReference.compareAndSet(null,
                                currentThread)) {
                            while (!messages.isEmpty()) {
                                final RequestImpl m = messages.poll();
                                targ.unbufferedAddMessage(m, true);
                            }
                            throw new MigrationException(targ);
                        }
                    }
                }
                target.unbufferedAddMessages(messages);
            }
        }
        return result;
    }
}
