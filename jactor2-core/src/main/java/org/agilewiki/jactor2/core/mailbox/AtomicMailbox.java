package org.agilewiki.jactor2.core.mailbox;

import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.context.MigrationException;
import org.agilewiki.jactor2.core.messaging.Message;

public class AtomicMailbox extends UnboundMailbox {

    /**
     * Create a mailbox.
     *
     * @param _onIdle                Object to be run when the inbox is emptied, or null.
     * @param _factory               The factory of this object.
     * @param _initialBufferSize     Initial size of the outbox for each unique message destination.
     * @param _initialLocalQueueSize The initial number of slots in the local queue.
     */
    public AtomicMailbox(Runnable _onIdle,
                         JAContext _factory,
                         int _initialBufferSize,
                         final int _initialLocalQueueSize) {
        super(_onIdle, _factory, _initialBufferSize, _initialLocalQueueSize);
    }

    @Override
    protected Inbox createInbox(int _initialLocalQueueSize) {
        return new AtomicInbox(_initialLocalQueueSize);
    }

    @Override
    protected void processMessage(final Message message) {
        message.eval(this);
        try {
            flush(true);
        } catch (final MigrationException me) {
            throw me;
        } catch (Exception e) {
            log.error("Exception thrown by onIdle", e);
        }
    }
}
