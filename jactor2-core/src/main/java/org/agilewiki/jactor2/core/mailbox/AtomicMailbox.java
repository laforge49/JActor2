package org.agilewiki.jactor2.core.mailbox;

import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.context.MigrationException;
import org.agilewiki.jactor2.core.messaging.Message;

/**
 * A mailbox which processes each request to completion, and which should be used by actors
 * which perform long computations, I/O, or otherwise block the thread.
 * <p>
 * For thread safety, the processing of each message is atomic, but when the processing of a
 * message results in the sending of a request, other messages may be processed before a
 * response to that request is received. However, an atomic mailbox will not process another
 * request until a response is returned for the prior request. This does not however preclude
 * the processing of event messages.
 * </p>
 * <p>
 * Request/Response messages which are destined to a different mailbox are buffered rather
 * than being sent immediately. These messages are disbursed to their destinations when the
 * processing of each incoming message is complete.
 * </p>
 * <p>
 * When the last block of buffered messages is being disbursed, if the destination is not
 * a thread-bound mailbox, the destination mailbox has no associated thread and the
 * context of the current mailbox is the same as the destination mailbox, then the
 * current thread migrates with the message block. By this means the message block is
 * often kept in the hardware thread's high-speed memory cache, which means much faster
 * execution.
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
     * @param _initialOutboxSize     Initial size of the outbox for each unique message destination.
     * @param _initialLocalQueueSize The initial number of slots in the local queue.
     * @param _onIdle                Object to be run when the inbox is emptied, or null.
     */
    public AtomicMailbox(JAContext _jaContext,
                         int _initialOutboxSize,
                         final int _initialLocalQueueSize,
                         Runnable _onIdle) {
        super(_jaContext, _initialOutboxSize, _initialLocalQueueSize, _onIdle);
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
