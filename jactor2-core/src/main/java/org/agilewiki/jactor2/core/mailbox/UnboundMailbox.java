package org.agilewiki.jactor2.core.mailbox;

import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.context.MigrationException;
import org.agilewiki.jactor2.core.messaging.Message;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Common code for NonBlockingMailbox and AtomicMailbox, which are not bound to a thread.
 * <p>
 * UnboundMailbox supports thread migration only between instances of this class.
 * </p>
 */
abstract public class UnboundMailbox extends MailboxBase {

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
     * @param _context               The context of this mailbox.
     * @param _initialOutboxSize     Initial size of the outbox for each unique message destination.
     * @param _initialLocalQueueSize The initial number of slots in the local queue.
     * @param _onIdle                Object to be run when the inbox is emptied, or null.
     */
    public UnboundMailbox(JAContext _context,
                          int _initialOutboxSize,
                          final int _initialLocalQueueSize,
                          Runnable _onIdle) {
        super(_context, _initialOutboxSize, _initialLocalQueueSize);
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
        if (onIdle != null && inbox.isIdle()) {
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
            jaContext.submit(this);
        }
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
            final Iterator<Map.Entry<MailboxBase, ArrayDeque<Message>>> iter = sendBuffer
                    .entrySet().iterator();
            while (iter.hasNext()) {
                result = true;
                final Map.Entry<MailboxBase, ArrayDeque<Message>> entry = iter.next();
                final MailboxBase target = entry.getKey();
                final ArrayDeque<Message> messages = entry.getValue();
                iter.remove();
                if (!iter.hasNext() &&
                        _mayMigrate &&
                        getJAContext() == target.getJAContext() &&
                        target instanceof UnboundMailbox &&
                        !target.isRunning()) {
                    Thread currentThread = threadReference.get();
                    MailboxBase targ = target;
                    AtomicReference<Thread> targetThreadReference = targ.getThreadReference();
                    if (targetThreadReference.get() == null &&
                            targetThreadReference.compareAndSet(null, currentThread)) {
                        while (!messages.isEmpty()) {
                            Message m = messages.poll();
                            targ.unbufferedAddMessage(m, true);
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
