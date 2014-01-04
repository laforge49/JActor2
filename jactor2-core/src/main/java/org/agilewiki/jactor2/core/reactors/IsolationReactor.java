package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.impl.IsolationReactorImpl;
import org.agilewiki.jactor2.core.impl.ReactorImpl;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.plant.Scheduler;
import org.agilewiki.jactor2.core.util.Recovery;

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
public class IsolationReactor extends ReactorBase {

    public IsolationReactor() throws Exception {
        this(Plant.getReactor());
    }

    public IsolationReactor(final Reactor _parentReactor)
            throws Exception {
        this(_parentReactor, _parentReactor.asReactorImpl().initialBufferSize,
                _parentReactor.asReactorImpl().initialLocalQueueSize);
    }

    public IsolationReactor(final int _initialOutboxSize, final int _initialLocalQueueSize) throws Exception {
        this(Plant.getReactor(), _initialOutboxSize, _initialLocalQueueSize);
    }

    public IsolationReactor(final Reactor _parentReactor,
                           final int _initialOutboxSize, final int _initialLocalQueueSize) throws Exception {
        this(_parentReactor.asReactorImpl(), _initialOutboxSize, _initialLocalQueueSize,
                _parentReactor.asReactorImpl().recovery, _parentReactor.asReactorImpl().scheduler);
    }

    public IsolationReactor(final ReactorImpl _parentReactorImpl,
                           final int _initialOutboxSize, final int _initialLocalQueueSize,
                           final Recovery _recovery, final Scheduler _scheduler) throws Exception {
        super(new IsolationReactorImpl(_parentReactorImpl, _initialOutboxSize, _initialLocalQueueSize,
                _recovery, _scheduler));
    }

    public void setIdle(final Runnable _idle) {
        ((IsolationReactorImpl) asReactorImpl()).onIdle = _idle;
    }
}
