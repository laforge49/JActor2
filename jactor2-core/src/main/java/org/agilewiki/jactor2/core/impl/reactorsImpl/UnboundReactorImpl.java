package org.agilewiki.jactor2.core.impl.reactorsImpl;

import org.agilewiki.jactor2.core.impl.plantImpl.PlantImplBase;
import org.agilewiki.jactor2.core.impl.requestsImpl.RequestImpl;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Common code for BlockingReactor, NonBlockingReactor and IsolationReactor, which are not bound to a thread.
 * <p>
 * UnboundReactorImpl supports thread migration only between instances of this class.
 * </p>
 */
abstract public class UnboundReactorImpl extends ReactorImpl {

    /**
     * The object to be run when the inbox is emptied and before the threadReference is cleared.
     */
    public Runnable onIdle;

    /**
     * Create an UnboundReactor.
     *
     * @param _parentReactorImpl        The parent reactor.
     * @param _initialOutboxSize        The initial buffer size for outgoing messages.
     * @param _initialLocalQueueSize    The initial local queue size.
     */
    public UnboundReactorImpl(final NonBlockingReactorImpl _parentReactorImpl,
                              final int _initialOutboxSize, final int _initialLocalQueueSize) {
        super(_parentReactorImpl, _initialOutboxSize, _initialLocalQueueSize);
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
    protected void afterAdd() {
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
            PlantImplBase.getSingleton().submit(this);
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
                        final Thread currentThread = threadReference.get();
                        final UnboundReactorImpl targ = (UnboundReactorImpl) target;
                        final AtomicReference<Thread> targetThreadReference = targ
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
