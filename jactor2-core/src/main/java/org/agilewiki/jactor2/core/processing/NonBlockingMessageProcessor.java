package org.agilewiki.jactor2.core.processing;

import org.agilewiki.jactor2.core.threading.ModuleContext;

/**
 * A message processor for actors which process messages quickly and without blocking the thread.
 * <p>
 * For thread safety, the processing of each message is done in isolation, but when the processing of a
 * message results in the sending of a request, other messages may be processed before a
 * response to that request is received.
 * </p>
 * <p>
 * AsyncRequest/Response messages which are destined to a different message processors are buffered rather
 * than being sent immediately. These messages are disbursed to their destinations when all
 * incoming messages have been processed.
 * </p>
 * <p>
 * When the last block of buffered messages is being disbursed, if the destination is not
 * a thread-bound message processor, the destination message processor has no associated thread and the
 * context of the current message processor is the same as the destination message processor, then the
 * current thread migrates with the message block. By this means the message block is
 * often kept in the hardware thread's high-speed memory cache, which means much faster
 * execution.
 * </p>
 * <p>
 * The Inbox used by NonBlockingMessageProcessor is NonBlockingInbox.
 * </p>
 */
public class NonBlockingMessageProcessor extends UnboundMessageProcessor {

    /**
     * Create a non-blocking message processor.
     *
     * @param _moduleContext The context of the message processor.
     */
    public NonBlockingMessageProcessor(ModuleContext _moduleContext) {
        super(_moduleContext, _moduleContext.getInitialBufferSize(),
                _moduleContext.getInitialLocalMessageQueueSize(), null);
    }

    /**
     * Create a non-blocking message processor.
     *
     * @param _moduleContext The context of the message processor.
     * @param _onIdle        Object to be run when the inbox is emptied, or null.
     */
    public NonBlockingMessageProcessor(ModuleContext _moduleContext,
                                       Runnable _onIdle) {
        super(_moduleContext, _moduleContext.getInitialBufferSize(),
                _moduleContext.getInitialLocalMessageQueueSize(), _onIdle);
    }

    /**
     * Create a non-blocking message processor.
     *
     * @param _moduleContext         The context of the message processor.
     * @param _initialOutboxSize     Initial size of the outbox for each unique message destination.
     * @param _initialLocalQueueSize The initial number of slots in the local queue.
     * @param _onIdle                Object to be run when the inbox is emptied, or null.
     */
    public NonBlockingMessageProcessor(ModuleContext _moduleContext,
                                       int _initialOutboxSize,
                                       final int _initialLocalQueueSize,
                                       Runnable _onIdle) {
        super(_moduleContext, _initialOutboxSize, _initialLocalQueueSize, _onIdle);
    }

    @Override
    protected Inbox createInbox(int _initialLocalQueueSize) {
        return new NonBlockingInbox(_initialLocalQueueSize);
    }
}
