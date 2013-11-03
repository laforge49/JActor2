package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.facilities.MigrationException;
import org.agilewiki.jactor2.core.messages.Message;

/**
 * A targetReactor which should be used by blades
 * which perform long computations, I/O, or otherwise block the thread. And unlike other types of
 * reactors, a BlockingReactor should usually be used only by a single blades.
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
 * The Inbox used by BlockingReactor is NonBlockingInbox.
 * </p>
 */
public class BlockingReactor extends UnboundReactor implements CommonReactor {

    /**
     * Create a non-blocking targetReactor.
     *
     * @param _facility The facility of the targetReactor.
     */
    public BlockingReactor(Facility _facility) throws Exception {
        super(_facility, _facility.getInitialBufferSize(),
                _facility.getInitialLocalMessageQueueSize(), null);
    }

    /**
     * Create a non-blocking targetReactor.
     *
     * @param _facility The facility of the targetReactor.
     * @param _onIdle   Object to be run when the inbox is emptied, or null.
     */
    public BlockingReactor(Facility _facility,
                              Runnable _onIdle) throws Exception {
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
    public BlockingReactor(Facility _facility,
                              int _initialOutboxSize,
                              final int _initialLocalQueueSize,
                              Runnable _onIdle) throws Exception {
        super(_facility, _initialOutboxSize, _initialLocalQueueSize, _onIdle);
    }

    @Override
    protected Inbox createInbox(int _initialLocalQueueSize) {
        return new NonBlockingInbox(_initialLocalQueueSize);
    }

    @Override
    protected void processMessage(final Message message) {
        message.eval();
        try {
            flush(true);
        } catch (final MigrationException me) {
            throw me;
        } catch (Exception e) {
            log.error("Exception thrown by flush", e);
        }
    }
}
