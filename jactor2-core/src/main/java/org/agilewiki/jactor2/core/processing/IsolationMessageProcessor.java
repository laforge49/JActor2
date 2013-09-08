package org.agilewiki.jactor2.core.processing;

import org.agilewiki.jactor2.core.messaging.Message;
import org.agilewiki.jactor2.core.threading.MigrationException;
import org.agilewiki.jactor2.core.threading.ModuleContext;

/**
 * A message processor which processes each request to completion, and which should be used by actors
 * which perform long computations, I/O, or otherwise block the thread. And unlike other types of
 * message processors, an IsolationMessageProcessor should usually be used only by a single actor.
 * <p>
 * For thread safety, the processing of each message is done in isolation from other messages, but when the processing of a message
 * results in the sending of a request message to another actor, other messages may be processed before a
 * response to that request message is received. However, an isolation message processor will not process a
 * request until a response is returned for the prior request. This does not however preclude
 * the processing of event messages.
 * </p>
 * <p>
 * AsyncRequest/Response messages which are destined to a different message processor are buffered rather
 * than being sent immediately. These messages are disbursed to their destinations when the
 * processing of each incoming message is complete.
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
 * The Inbox used by IsolationMessageProcessor is IsolationInbox.
 * </p>
 */
public class IsolationMessageProcessor extends UnboundMessageProcessor {

    /**
     * Create an isolation message processor.
     *
     * @param _moduleContext The context of the message processor.
     */
    public IsolationMessageProcessor(ModuleContext _moduleContext) {
        super(_moduleContext, _moduleContext.getInitialBufferSize(),
                _moduleContext.getInitialLocalMessageQueueSize(), null);
    }

    /**
     * Create an isolation message processor.
     *
     * @param _moduleContext The context of the message processor.
     * @param _onIdle        Object to be run when the inbox is emptied, or null.
     */
    public IsolationMessageProcessor(ModuleContext _moduleContext, Runnable _onIdle) {
        super(_moduleContext, _moduleContext.getInitialBufferSize(),
                _moduleContext.getInitialLocalMessageQueueSize(), _onIdle);
    }

    /**
     * Create an isolation message processor.
     *
     * @param _moduleContext         The context of the message processor.
     * @param _initialOutboxSize     Initial size of the outbox for each unique message destination.
     * @param _initialLocalQueueSize The initial number of slots in the local queue.
     * @param _onIdle                Object to be run when the inbox is emptied, or null.
     */
    public IsolationMessageProcessor(ModuleContext _moduleContext,
                                     int _initialOutboxSize,
                                     final int _initialLocalQueueSize,
                                     Runnable _onIdle) {
        super(_moduleContext, _initialOutboxSize, _initialLocalQueueSize, _onIdle);
    }

    @Override
    protected Inbox createInbox(int _initialLocalQueueSize) {
        return new IsolationInbox(_initialLocalQueueSize);
    }

    @Override
    protected void processMessage(final Message message) {
        message.eval();
        try {
            flush(true);
        } catch (final MigrationException me) {
            throw me;
        } catch (Exception e) {
            log.error("Exception thrown by onIdle", e);
        }
    }
}
