package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.facilities.MigrationException;
import org.agilewiki.jactor2.core.messages.Message;

/**
 * A targetReactor which processes each request to completion. And unlike other types of
 * reactors, an IsolationReactor should usually be used only by a single blades.
 * <p>
 * For thread safety, the processing of each message is done in isolation from other messages, but when the processing of a message
 * results in the sending of a request message to another blades, other messages may be processed before a
 * response to that request message is received. However, an isolation targetReactor will not process a
 * request until a response is returned for the prior request. This does not however preclude
 * the processing of event messages.
 * </p>
 * <p>
 * AsyncRequest/Response messages which are destined to a different targetReactor are buffered rather
 * than being sent immediately. These messages are disbursed to their destinations when the
 * processing of each incoming message is complete.
 * </p>
 * <p>
 * When the last block of buffered messages is being disbursed, if the destination is not
 * a thread-bound targetReactor, the destination targetReactor has no associated thread and the
 * facility of the current targetReactor is the same as the destination targetReactor, then the
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
     * Create an isolation targetReactor.
     *
     * @param _facility The facility of the targetReactor.
     */
    public IsolationReactor(final Facility _facility) throws Exception {
        super(_facility, _facility.getInitialBufferSize(), _facility
                .getInitialLocalMessageQueueSize(), null);
    }

    /**
     * Create an isolation targetReactor.
     *
     * @param _facility The facility of the targetReactor.
     * @param _onIdle   Object to be run when the inbox is emptied, or null.
     */
    public IsolationReactor(final Facility _facility, final Runnable _onIdle)
            throws Exception {
        super(_facility, _facility.getInitialBufferSize(), _facility
                .getInitialLocalMessageQueueSize(), _onIdle);
    }

    /**
     * Create an isolation targetReactor.
     *
     * @param _facility              The facility of the targetReactor.
     * @param _initialOutboxSize     Initial size of the outbox for each unique message destination.
     * @param _initialLocalQueueSize The initial number of slots in the doLocal queue.
     * @param _onIdle                Object to be run when the inbox is emptied, or null.
     */
    public IsolationReactor(final Facility _facility,
            final int _initialOutboxSize, final int _initialLocalQueueSize,
            final Runnable _onIdle) throws Exception {
        super(_facility, _initialOutboxSize, _initialLocalQueueSize, _onIdle);
    }

    @Override
    protected Inbox createInbox(final int _initialLocalQueueSize) {
        return new IsolationInbox(_initialLocalQueueSize);
    }

    @Override
    protected void processMessage(final Message message) {
        message.eval();
        try {
            flush(true);
        } catch (final MigrationException me) {
            throw me;
        } catch (final Exception e) {
            log.error("Exception thrown by flush", e);
        }
    }
}
