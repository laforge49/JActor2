package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.impl.NonBlockingReactorImpl;
import org.agilewiki.jactor2.core.impl.ReactorImpl;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.plant.Scheduler;
import org.agilewiki.jactor2.core.util.Recovery;

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
public class NonBlockingReactor extends ReactorBase implements CommonReactor {

    public NonBlockingReactor()
            throws Exception {
        this(Plant.getReactor());
    }

    public NonBlockingReactor(final NonBlockingReactor _parentReactor)
            throws Exception {
        this(_parentReactor, _parentReactor.asReactorImpl().initialBufferSize,
                _parentReactor.asReactorImpl().initialLocalQueueSize);
    }

    public NonBlockingReactor(final int _initialOutboxSize, final int _initialLocalQueueSize) throws Exception {
        this(Plant.getReactor(), _initialOutboxSize, _initialLocalQueueSize);
    }

    public NonBlockingReactor(final NonBlockingReactor _parentReactor,
                              final int _initialOutboxSize, final int _initialLocalQueueSize) throws Exception {
        this(_parentReactor, _initialOutboxSize, _initialLocalQueueSize,
                _parentReactor.asReactorImpl().recovery, _parentReactor.asReactorImpl().scheduler);
    }

    public NonBlockingReactor(final int _initialOutboxSize, final int _initialLocalQueueSize,
                              final Recovery _recovery, final Scheduler _scheduler) throws Exception {
        this(Plant.getReactor(), _initialOutboxSize, _initialLocalQueueSize, _recovery, _scheduler);
    }

    public NonBlockingReactor(final NonBlockingReactor _parentReactor,
                              final int _initialOutboxSize, final int _initialLocalQueueSize,
                              final Recovery _recovery, final Scheduler _scheduler) throws Exception {
        initialize(createReactorImpl(_parentReactor == null ? null : _parentReactor.asReactorImpl(),
                _initialOutboxSize, _initialLocalQueueSize, _recovery, _scheduler));
    }

    protected NonBlockingReactorImpl createReactorImpl(final NonBlockingReactorImpl _parentReactorImpl,
                                                       final int _initialOutboxSize, final int _initialLocalQueueSize,
                                                       final Recovery _recovery, final Scheduler _scheduler)
            throws Exception {
        return new NonBlockingReactorImpl(_parentReactorImpl, _initialOutboxSize, _initialLocalQueueSize,
                _recovery, _scheduler);
    }

    @Override
    public NonBlockingReactorImpl asReactorImpl() {
        return (NonBlockingReactorImpl) asCloserImpl();
    }

    public void setIdle(final Runnable _idle) {
        ((NonBlockingReactorImpl) asReactorImpl()).onIdle = _idle;
    }
}
