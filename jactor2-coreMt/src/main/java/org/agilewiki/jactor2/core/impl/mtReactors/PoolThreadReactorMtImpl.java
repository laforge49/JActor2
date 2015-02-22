package org.agilewiki.jactor2.core.impl.mtReactors;

import org.agilewiki.jactor2.core.impl.mtMessages.RequestMtImpl;
import org.agilewiki.jactor2.core.impl.mtPlant.PlantMtImpl;
import org.agilewiki.jactor2.core.impl.mtPlant.ReactorPoolThread;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.impl.PoolThreadReactorImpl;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

abstract public class PoolThreadReactorMtImpl extends ReactorMtImpl implements
        PoolThreadReactorImpl {
    private static volatile int nextHash;

    private Runnable onIdle;

    /** Our hashcode. */
    private final int hashCode = nextHash++;

    /**
     * Create an PoolThreadReactorMtImpl.
     *
     * @param _parentReactor         The parent reactor.
     * @param _initialOutboxSize     The initial buffer size for outgoing messages.
     * @param _initialLocalQueueSize The initial local queue size.
     */
    public PoolThreadReactorMtImpl(final IsolationReactor _parentReactor,
            final int _initialOutboxSize, final int _initialLocalQueueSize) {
        super(_parentReactor, _initialOutboxSize, _initialLocalQueueSize);
    }

    /** Redefines the hashcode for a faster hashing. */
    @Override
    public int hashCode() {
        return hashCode;
    }

    /**
     * Returns true when there is code to be executed when the inbox is emptied.
     *
     * @return True when there is code to be executed when the inbox is emptied.
     */
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
    protected void afterAdd() {
        if (threadReference == null) {
            System.err
                    .println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.err
                    .println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.err
                    .println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.err.println("this=" + this);
            System.err
                    .println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.err
                    .println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.err
                    .println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            throw new NullPointerException();
        }
        if (threadReference.get() == null) {
            PlantMtImpl.getSingleton().submit(this);
        }
    }

    /**
     * Flushes buffered messages, if any.
     * Returns true if there was any.
     *
     * @param _mayMigrate True when thread migration is allowed.
     * @return True when one or more buffered request/result was delivered.
     */
    protected boolean flush(final boolean _mayMigrate) throws Exception {
        boolean result = false;
        final Iterator<Map.Entry<ReactorMtImpl, ArrayDeque<RequestMtImpl<?>>>> iter = outbox
                .getIterator();
        if (iter != null) {
            while (iter.hasNext()) {
                result = true;
                final Map.Entry<ReactorMtImpl, ArrayDeque<RequestMtImpl<?>>> entry = iter
                        .next();
                final ReactorMtImpl target = entry.getKey();
                final ArrayDeque<RequestMtImpl<?>> messages = entry.getValue();
                iter.remove();
                if (!iter.hasNext() && _mayMigrate
                        && (target instanceof PoolThreadReactorImpl)) {
                    if (!target.isRunning()) {
                        final ReactorPoolThread currentThread = (ReactorPoolThread) threadReference.get();
                        if (currentThread.checkMigrationCount()) {
                            final PoolThreadReactorMtImpl targ = (PoolThreadReactorMtImpl) target;
                            final AtomicReference<Thread> targetThreadReference = targ
                                    .getThreadReference();
                            if ((targetThreadReference.get() == null)
                                    && targetThreadReference.compareAndSet(null,
                                    currentThread)) {
                                while (!messages.isEmpty()) {
                                    final RequestMtImpl<?> m = messages.poll();
                                    targ.unbufferedAddMessage(m, true);
                                }
                                currentThread.incMigrationCount();
                                throw new MigrationException(targ);
                            }
                        }
                    }
                }
                target.unbufferedAddMessages(messages);
            }
        }
        return result;
    }

    /**
     * The object to be run when the inbox is emptied and before the threadReference is cleared.
     */
    @Override
    public Runnable getOnIdle() {
        return onIdle;
    }

    @Override
    public void setOnIdle(final Runnable onIdle) {
        this.onIdle = onIdle;
    }
}
