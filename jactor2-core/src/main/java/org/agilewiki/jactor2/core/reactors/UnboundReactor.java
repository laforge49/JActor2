package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.facilities.MigrationException;
import org.agilewiki.jactor2.core.facilities.PoolThread;
import org.agilewiki.jactor2.core.messages.Message;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Common code for NonBlockingReactor and IsolationReactor, which are not bound to a thread.
 * <p>
 * UnboundReactor supports thread migration only between instances of this class.
 * </p>
 */
abstract public class UnboundReactor extends ReactorBase {

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
    public UnboundReactor(final Facility _facility,
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
    public boolean isRunning() {
        return threadReference.get() != null;
    }

    @Override
    protected void afterAdd() throws Exception {
        if (threadReference.get() == null) {
            facility.getPlant().submit(this);
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
        final Iterator<Map.Entry<ReactorBase, ArrayDeque<Message>>> iter = outbox
                .getIterator();
        if (iter != null) {
            while (iter.hasNext()) {
                result = true;
                final Map.Entry<ReactorBase, ArrayDeque<Message>> entry = iter
                        .next();
                final ReactorBase target = entry.getKey();
                final ArrayDeque<Message> messages = entry.getValue();
                iter.remove();
                if (!iter.hasNext() && _mayMigrate
                        && (target instanceof UnboundReactor)) {
                    if (!target.isRunning()) {
                        final PoolThread currentThread = threadReference.get();
                        final UnboundReactor targ = (UnboundReactor) target;
                        final AtomicReference<PoolThread> targetThreadReference = targ
                                .getThreadReference();
                        if ((targetThreadReference.get() == null)
                                && targetThreadReference.compareAndSet(null,
                                        currentThread)) {
                            while (!messages.isEmpty()) {
                                final Message m = messages.poll();
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

    @Override
    public void run() {
        while (true) {
            final Message message = inbox.poll();
            if (message == null) {
                try {
                    notBusy();
                } catch (final MigrationException me) {
                    throw me;
                } catch (final Exception e) {
                    log.error("Exception thrown by onIdle", e);
                }
                if (hasWork()) {
                    continue;
                }
                return;
            }
            processMessage(message);
        }
    }
}
