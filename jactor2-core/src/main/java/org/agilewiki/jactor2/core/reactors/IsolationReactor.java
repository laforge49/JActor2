package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.messages.Message;
import org.agilewiki.jactor2.core.plant.BasicPlant;
import org.agilewiki.jactor2.core.plant.MigrationException;

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
public class IsolationReactor extends UnboundReactorImpl implements Reactor {

    public IsolationReactor(final BasicPlant _plant) throws Exception {
        this(_plant.asFacility());
    }

    public IsolationReactor(final Facility _facility) throws Exception {
        super(_facility, _facility.asFacilityImpl().getInitialBufferSize(), _facility.asFacilityImpl()
                .getInitialLocalMessageQueueSize(), null);
        initialize(this);
    }

    public IsolationReactor(final BasicPlant _plant, final Runnable _onIdle)
            throws Exception {
        this(_plant.asFacility(), _onIdle);
    }

    public IsolationReactor(final Facility _facility, final Runnable _onIdle)
            throws Exception {
        super(_facility, _facility.asFacilityImpl().getInitialBufferSize(), _facility.asFacilityImpl()
                .getInitialLocalMessageQueueSize(), _onIdle);
        initialize(this);
    }

    public IsolationReactor(final BasicPlant _plant,
                            final int _initialOutboxSize, final int _initialLocalQueueSize,
                            final Runnable _onIdle) throws Exception {
        this(_plant.asFacility(), _initialOutboxSize, _initialLocalQueueSize, _onIdle);
    }

    public IsolationReactor(final Facility _facility,
                            final int _initialOutboxSize, final int _initialLocalQueueSize,
                            final Runnable _onIdle) throws Exception {
        super(_facility, _initialOutboxSize, _initialLocalQueueSize, _onIdle);
        initialize(this);
    }

    @Override
    protected Inbox createInbox(final int _initialLocalQueueSize) {
        return new IsolationInbox(_initialLocalQueueSize);
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
