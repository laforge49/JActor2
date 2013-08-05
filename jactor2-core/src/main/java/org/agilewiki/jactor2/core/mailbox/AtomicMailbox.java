package org.agilewiki.jactor2.core.mailbox;

import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.context.MigrationException;
import org.agilewiki.jactor2.core.messaging.Message;

/**
 * A mailbox which processes each request to completion, and which should be used by actors
 * which perform long computations, I/O, or otherwise block the thread.
 * <p>
 * For thread safety, the processing of each message is atomic, but when a message sends
 * a request, other messages may be processed before a response to that request is
 * received. However, an atomic mailbox will not process another request until a response is
 * returned for the prior request. This does not however preclude the processing of
 * event messages.
 * </p>
 * <p>
 * Request messages are buffered rather than being sent immediately. These messages are
 * disbursed to their destinations when the processing of each incoming message is
 * complete.
 * </p>
 * <p>
 * The Inbox used by AtomicMailbox is AtomicInbox.
 * </p>
 */
public class AtomicMailbox extends UnboundMailbox {

    /**
     * Create an atomic mailbox.
     *
     * @param _jaContext The context of the mailbox.
     */
    public AtomicMailbox(JAContext _jaContext) {
        super(_jaContext, _jaContext.getInitialBufferSize(),
                _jaContext.getInitialLocalMessageQueueSize(), null);
    }

    /**
     * Create an atomic mailbox.
     *
     * @param _jaContext The context of the mailbox.
     * @param _onIdle    Object to be run when the inbox is emptied, or null.
     */
    public AtomicMailbox(JAContext _jaContext, Runnable _onIdle) {
        super(_jaContext, _jaContext.getInitialBufferSize(),
                _jaContext.getInitialLocalMessageQueueSize(), _onIdle);
    }

    /**
     * Create an atomic mailbox.
     *
     * @param _jaContext             The context of the mailbox.
     * @param _initialBufferSize     Initial size of the outbox for each unique message destination.
     * @param _initialLocalQueueSize The initial number of slots in the local queue.
     * @param _onIdle                Object to be run when the inbox is emptied, or null.
     */
    public AtomicMailbox(JAContext _jaContext,
                         int _initialBufferSize,
                         final int _initialLocalQueueSize,
                         Runnable _onIdle) {
        super(_jaContext, _initialBufferSize, _initialLocalQueueSize, _onIdle);
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
