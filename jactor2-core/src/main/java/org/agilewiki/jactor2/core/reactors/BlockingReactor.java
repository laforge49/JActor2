package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.messages.Message;
import org.agilewiki.jactor2.core.plant.BasicPlant;
import org.agilewiki.jactor2.core.plant.MigrationException;

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
public class BlockingReactor extends UnboundReactorImpl implements CommonReactor {

    public BlockingReactor(final BasicPlant _plant) throws Exception {
        this(_plant.asFacility());
    }

    public BlockingReactor(final Facility _facility) throws Exception {
        super(_facility, _facility.asFacilityImpl().getInitialBufferSize(), _facility.asFacilityImpl()
                .getInitialLocalMessageQueueSize(), null);
        initialize(this);
    }

    public BlockingReactor(final BasicPlant _plant, final Runnable _onIdle)
            throws Exception {
        this(_plant.asFacility(), _onIdle);
    }

    public BlockingReactor(final Facility _facility, final Runnable _onIdle)
            throws Exception {
        super(_facility, _facility.asFacilityImpl().getInitialBufferSize(), _facility.asFacilityImpl()
                .getInitialLocalMessageQueueSize(), _onIdle);
        initialize(this);
    }

    public BlockingReactor(final BasicPlant _plant,
                           final int _initialOutboxSize, final int _initialLocalQueueSize,
                           final Runnable _onIdle) throws Exception {
        this(_plant.asFacility(), _initialOutboxSize, _initialLocalQueueSize, _onIdle);
    }

    public BlockingReactor(final Facility _facility,
                           final int _initialOutboxSize, final int _initialLocalQueueSize,
                           final Runnable _onIdle) throws Exception {
        super(_facility, _initialOutboxSize, _initialLocalQueueSize, _onIdle);
        initialize(this);
    }

    @Override
    protected Inbox createInbox(final int _initialLocalQueueSize) {
        return new NonBlockingInbox(_initialLocalQueueSize);
    }

    @Override
    protected void processMessage(final Message message) {
        super.processMessage(message);
        try {
            flush(true);
        } catch (final MigrationException me) {
            throw me;
        } catch (final Exception e) {
            log.error("Exception thrown by flush", e);
        }
    }
}
