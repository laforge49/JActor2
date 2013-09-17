package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.facilities.Facility;

/**
 * A targetReactor for blades which process messages quickly and without blocking the thread.
 * <p>
 * For thread safety, the processing of each message is done in isolation, but when the processing of a
 * message results in the sending of a request, other messages may be processed before a
 * response to that request is received.
 * </p>
 * <p>
 * AsyncRequest/Response messages which are destined to a different targetReactor are buffered rather
 * than being sent immediately. These messages are disbursed to their destinations when all
 * incoming messages have been processed.
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
 * The Inbox used by NonBlockingReactor is NonBlockingInbox.
 * </p>
 */
public class NonBlockingReactor extends UnboundReactor {

    /**
     * Create a non-blocking targetReactor.
     *
     * @param _facility The facility of the targetReactor.
     */
    public NonBlockingReactor(Facility _facility) {
        super(_facility, _facility.getInitialBufferSize(),
                _facility.getInitialLocalMessageQueueSize(), null);
    }

    /**
     * Create a non-blocking targetReactor.
     *
     * @param _facility The facility of the targetReactor.
     * @param _onIdle   Object to be run when the inbox is emptied, or null.
     */
    public NonBlockingReactor(Facility _facility,
                              Runnable _onIdle) {
        super(_facility, _facility.getInitialBufferSize(),
                _facility.getInitialLocalMessageQueueSize(), _onIdle);
    }

    /**
     * Create a non-blocking targetReactor.
     *
     * @param _facility              The facility of the targetReactor.
     * @param _initialOutboxSize     Initial size of the outbox for each unique message destination.
     * @param _initialLocalQueueSize The initial number of slots in the doLocal queue.
     * @param _onIdle                Object to be run when the inbox is emptied, or null.
     */
    public NonBlockingReactor(Facility _facility,
                              int _initialOutboxSize,
                              final int _initialLocalQueueSize,
                              Runnable _onIdle) {
        super(_facility, _initialOutboxSize, _initialLocalQueueSize, _onIdle);
    }

    @Override
    protected Inbox createInbox(int _initialLocalQueueSize) {
        return new NonBlockingInbox(_initialLocalQueueSize);
    }
}
