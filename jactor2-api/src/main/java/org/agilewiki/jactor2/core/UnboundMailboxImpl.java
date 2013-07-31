package org.agilewiki.jactor2.core;

import org.slf4j.Logger;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Implements mailbox that is not bound to a single thread.
 */
abstract public class UnboundMailboxImpl extends JAMailboxImpl implements UnboundMailbox {

    /**
     * A reference to the thread that is executing this mailbox.
     */
    protected final AtomicReference<Thread> threadReference = new AtomicReference<Thread>();

    /**
     * The object to be run when the mailbox is emptied and before the threadReference is cleared.
     */
    private final Runnable onIdle;

    /**
     * Create a mailbox.
     *
     * @param _onIdle                Object to be run when the inbox is emptied, or null.
     * @param _factory               The factory of this object.
     * @param _log                   The Mailbox log.
     * @param _initialBufferSize     Initial size of the outbox for each unique message destination.
     * @param _initialLocalQueueSize The initial number of slots in the local queue.
     */
    public UnboundMailboxImpl(Runnable _onIdle,
                              JAMailboxFactory _factory,
                              Logger _log,
                              int _initialBufferSize,
                              final int _initialLocalQueueSize) {
        super(_factory, _log, _initialBufferSize, _initialLocalQueueSize);
        onIdle = _onIdle;
    }

    @Override
    public AtomicReference<Thread> getThreadReference() {
        return threadReference;
    }

    @Override
    public boolean isIdler() {
        return onIdle != null;
    }

    @Override
    protected void notBusy() throws Exception {
        if (onIdle != null && isIdle()) {
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
        /**
         * The compareAndSet method is a moderately expensive operation,
         * so we use a guard expression to reduce the number of times it is called.
         */
        if (threadReference.get() == null && inbox.hasWork()) {
            mailboxFactory.submit(this);
        }
    }

    @Override
    public final boolean flush() throws Exception {
        return flush(false);
    }

    /**
     * Flushes buffered messages, if any.
     * Returns true if there was any.
     *
     * @param _mayMigrate True when thread migration is allowed.
     * @return True when one or more buffered request/result was delivered.
     */
    public boolean flush(boolean _mayMigrate) throws Exception {
        boolean result = false;
        if (sendBuffer != null) {
            final Iterator<Map.Entry<JAMailbox, ArrayDeque<Message>>> iter = sendBuffer
                    .entrySet().iterator();
            while (iter.hasNext()) {
                result = true;
                final Map.Entry<JAMailbox, ArrayDeque<Message>> entry = iter.next();
                final JAMailbox target = entry.getKey();
                final ArrayDeque<Message> messages = entry.getValue();
                iter.remove();
                if (!iter.hasNext() &&
                        _mayMigrate &&
                        getMailboxFactory() == target.getMailboxFactory() &&
                        target instanceof UnboundMailbox &&
                        !target.isRunning()) {
                    Thread currentThread = threadReference.get();
                    UnboundMailbox targ = (UnboundMailbox) target;
                    AtomicReference<Thread> targetThreadReference = targ.getThreadReference();
                    if (targetThreadReference.get() == null &&
                            targetThreadReference.compareAndSet(null, currentThread)) {
                        while (!messages.isEmpty()) {
                            Message m = messages.poll();
                            targ.unbufferedAddMessages(m, true);
                        }
                        throw new MigrationException(targ);
                    }
                }
                target.unbufferedAddMessages(messages);
            }
        }
        return result;
    }
}
