package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.facilities.MigrationException;
import org.agilewiki.jactor2.core.messages.Message;

/**
 * A reactor which processes each request to completion, and which should be used by blades
 * which perform long computations, I/O, or otherwise block the thread. And unlike other types of
 * reactors, an IsolationReactor should usually be used only by a single blade.
 * <p>
 * For thread safety, the processing of each message is done in isolation from other messages, but when the processing of a message
 * results in the sending of a request message to another blade, other messages may be processed before a
 * response to that request message is received. However, an isolation reactor will not process a
 * request until a response is returned for the prior request. This does not however preclude
 * the processing of event messages.
 * </p>
 * <p>
 * AsyncRequest/Response messages which are destined to a different reactor are buffered rather
 * than being sent immediately. These messages are disbursed to their destinations when the
 * processing of each incoming message is complete.
 * </p>
 * <p>
 * When the last block of buffered messages is being disbursed, if the destination is not
 * a thread-bound reactor, the destination reactor has no associated thread and the
 * facility of the current reactor is the same as the destination reactor, then the
 * current thread migrates with the message block. By this means the message block is
 * often kept in the hardware thread's high-speed memory cache, which means much faster
 * execution.
 * </p>
 * <p>
 * The Inbox used by IsolationReactor is IsolationInbox.
 * </p>
 */
public class IsolationReactor extends UnboundReactor {

    /**
     * Create an isolation reactor.
     *
     * @param _facility The facility of the reactor.
     */
    public IsolationReactor(Facility _facility) {
        super(_facility, _facility.getInitialBufferSize(),
                _facility.getInitialLocalMessageQueueSize(), null);
    }

    /**
     * Create an isolation reactor.
     *
     * @param _facility The facility of the reactor.
     * @param _onIdle   Object to be run when the inbox is emptied, or null.
     */
    public IsolationReactor(Facility _facility, Runnable _onIdle) {
        super(_facility, _facility.getInitialBufferSize(),
                _facility.getInitialLocalMessageQueueSize(), _onIdle);
    }

    /**
     * Create an isolation reactor.
     *
     * @param _facility              The facility of the reactor.
     * @param _initialOutboxSize     Initial size of the outbox for each unique message destination.
     * @param _initialLocalQueueSize The initial number of slots in the local queue.
     * @param _onIdle                Object to be run when the inbox is emptied, or null.
     */
    public IsolationReactor(Facility _facility,
                            int _initialOutboxSize,
                            final int _initialLocalQueueSize,
                            Runnable _onIdle) {
        super(_facility, _initialOutboxSize, _initialLocalQueueSize, _onIdle);
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
