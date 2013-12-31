package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.impl.BlockingReactorImpl;
import org.agilewiki.jactor2.core.impl.ReactorImpl;
import org.agilewiki.jactor2.core.plant.BasicPlant;
import org.agilewiki.jactor2.core.plant.Scheduler;
import org.agilewiki.jactor2.core.util.Recovery;

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
public class BlockingReactor extends ReactorBase implements CommonReactor {

    public BlockingReactor() throws Exception {
        this(BasicPlant.getSingleton().getReactor());
    }

    public BlockingReactor(final Reactor _parentReactor) throws Exception {
        this(_parentReactor, _parentReactor.asReactorImpl().initialBufferSize,
                _parentReactor.asReactorImpl().initialLocalQueueSize, null);
    }

    public BlockingReactor(final Runnable _onIdle)
            throws Exception {
        this(BasicPlant.getSingleton().getReactor(), _onIdle);
    }

    public BlockingReactor(final Reactor _parentReactor, final Runnable _onIdle)
            throws Exception {
        this(_parentReactor, _parentReactor.asReactorImpl().initialBufferSize,
                _parentReactor.asReactorImpl().initialLocalQueueSize, _onIdle);
    }

    public BlockingReactor(final int _initialOutboxSize, final int _initialLocalQueueSize,
                              final Runnable _onIdle) throws Exception {
        this(BasicPlant.getSingleton().getReactor(), _initialOutboxSize, _initialLocalQueueSize, _onIdle);
    }

    public BlockingReactor(final Reactor _parentReactor,
                              final int _initialOutboxSize, final int _initialLocalQueueSize,
                              final Runnable _onIdle) throws Exception {
        this(_parentReactor.asReactorImpl(), _initialOutboxSize, _initialLocalQueueSize,
                _parentReactor.asReactorImpl().recovery, _parentReactor.asReactorImpl().scheduler, _onIdle);
    }

    public BlockingReactor(final ReactorImpl _parentReactorImpl,
                              final int _initialOutboxSize, final int _initialLocalQueueSize,
                              final Recovery _recovery, final Scheduler _scheduler,
                              final Runnable _onIdle) throws Exception {
        super(new BlockingReactorImpl(_parentReactorImpl, _initialOutboxSize, _initialLocalQueueSize,
                _recovery, _scheduler, _onIdle));
    }
}
