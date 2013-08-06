package org.agilewiki.jactor2.core.mailbox;

import org.agilewiki.jactor2.core.context.JAContext;

/**
 * A mailbox for actors which process messages quickly and without blocking the thread.
 * <p>
 * For thread safety, the processing of each message is atomic, but when the processing of a
 * message results in the sending of a request, other messages may be processed before a
 * response to that request is received.
 * </p>
 * <p>
 * Request/Response messages which are destined to a different mailbox are buffered rather
 * than being sent immediately. These messages are disbursed to their destinations when all
 * incoming messages have been processed.
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
 * The Inbox used by NonBlockingMailbox is NonBlockingInbox.
 * </p>
 */
public class NonBlockingMailbox extends UnboundMailbox {

    /**
     * Create a non-blocking mailbox.
     *
     * @param _jaContext The context of the mailbox.
     */
    public NonBlockingMailbox(JAContext _jaContext) {
        super(_jaContext, _jaContext.getInitialBufferSize(),
                _jaContext.getInitialLocalMessageQueueSize(), null);
    }

    /**
     * Create a non-blocking mailbox.
     *
     * @param _jaContext The context of the mailbox.
     * @param _onIdle    Object to be run when the inbox is emptied, or null.
     */
    public NonBlockingMailbox(JAContext _jaContext,
                              Runnable _onIdle) {
        super(_jaContext, _jaContext.getInitialBufferSize(),
                _jaContext.getInitialLocalMessageQueueSize(), _onIdle);
    }

    /**
     * Create a non-blocking mailbox.
     *
     * @param _jaContext             The context of the mailbox.
     * @param _initialBufferSize     Initial size of the outbox for each unique message destination.
     * @param _initialLocalQueueSize The initial number of slots in the local queue.
     * @param _onIdle                Object to be run when the inbox is emptied, or null.
     */
    public NonBlockingMailbox(JAContext _jaContext,
                              int _initialBufferSize,
                              final int _initialLocalQueueSize,
                              Runnable _onIdle) {
        super(_jaContext, _initialBufferSize, _initialLocalQueueSize, _onIdle);
    }

    @Override
    protected Inbox createInbox(int _initialLocalQueueSize) {
        return new NonBlockingInbox(_initialLocalQueueSize);
    }
}
