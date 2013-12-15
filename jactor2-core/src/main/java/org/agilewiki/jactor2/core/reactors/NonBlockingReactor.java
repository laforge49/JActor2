package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.plant.Plant;

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
public class NonBlockingReactor extends UnboundReactor implements CommonReactor {

    public NonBlockingReactor(final Plant _plant) throws Exception {
        this(_plant.facility());
    }

    public NonBlockingReactor(final Facility _facility) throws Exception {
        super(_facility, _facility.getInitialBufferSize(), _facility
                .getInitialLocalMessageQueueSize(), null);
    }

    public NonBlockingReactor(final Plant _plant, final Runnable _onIdle)
            throws Exception {
        this(_plant.facility(), _onIdle);
    }

    public NonBlockingReactor(final Facility _facility, final Runnable _onIdle)
            throws Exception {
        super(_facility, _facility.getInitialBufferSize(), _facility
                .getInitialLocalMessageQueueSize(), _onIdle);
    }

    public NonBlockingReactor(final Plant _plant,
                              final int _initialOutboxSize, final int _initialLocalQueueSize,
                              final Runnable _onIdle) throws Exception {
        this(_plant.facility(), _initialOutboxSize, _initialLocalQueueSize, _onIdle);
    }

    public NonBlockingReactor(final Facility _facility,
                              final int _initialOutboxSize, final int _initialLocalQueueSize,
                              final Runnable _onIdle) throws Exception {
        super(_facility, _initialOutboxSize, _initialLocalQueueSize, _onIdle);
    }

    @Override
    protected Inbox createInbox(final int _initialLocalQueueSize) {
        return new NonBlockingInbox(_initialLocalQueueSize);
    }
}
