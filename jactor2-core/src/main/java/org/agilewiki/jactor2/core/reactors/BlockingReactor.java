package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.blades.BlockingBlade;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.impl.reactorsImpl.BlockingReactorImpl;
import org.agilewiki.jactor2.core.impl.reactorsImpl.NonBlockingReactorImpl;

/**
 * Process requests/responses which may block the thread or tie it up with a long computation.
 * <p>
 * Requests/responses are processed one at a time in the order received, except that
 * requests/responses from the same reactor are given preference.
 * </p>
 * <p>
 * Requests/responses destined to a different reactor are held until processing is complete
 * for each incoming request/response.
 * </p>
 */
public class BlockingReactor extends ReactorBase implements CommonReactor, BlockingBlade {

    /**
     * Create a blocking reactor with the Plant internal reactor as the parent.
     */
    public BlockingReactor() {
        this(Plant.getInternalReactor());
    }

    /**
     * Create a blocking reactor.
     *
     * @param _parentReactor            The parent reactor.
     */
    public BlockingReactor(final NonBlockingReactor _parentReactor) {
        this(_parentReactor, _parentReactor.asReactorImpl().getInitialBufferSize(),
                _parentReactor.asReactorImpl().getInitialLocalQueueSize());
    }

    /**
     * Create a blocking reactor with the Plant internal reactor as the parent.
     *
     * @param _initialOutboxSize        Initial size of the list of requests/responses for each destination.
     * @param _initialLocalQueueSize    Initial size of the local input queue.
     */
    public BlockingReactor(final int _initialOutboxSize, final int _initialLocalQueueSize) {
        this(Plant.getInternalReactor(), _initialOutboxSize, _initialLocalQueueSize);
    }

    /**
     * Create a blocking reactor.
     *
     * @param _parentReactor            The parent reactor.
     * @param _initialOutboxSize        Initial size of the list of requests/responses for each destination.
     * @param _initialLocalQueueSize    Initial size of the local input queue.
     */
    public BlockingReactor(final NonBlockingReactor _parentReactor,
                              final int _initialOutboxSize, final int _initialLocalQueueSize) {
        this(_parentReactor.asReactorImpl(), _initialOutboxSize, _initialLocalQueueSize);
    }

    /**
     * Create a blocking reactor.
     *
     * @param _parentReactorImpl        The parent reactor impl object.
     * @param _initialOutboxSize        Initial size of the list of requests/responses for each destination.
     * @param _initialLocalQueueSize    Initial size of the local input queue.
     */
    private BlockingReactor(final NonBlockingReactorImpl _parentReactorImpl,
                              final int _initialOutboxSize, final int _initialLocalQueueSize) {
        initialize(new BlockingReactorImpl(_parentReactorImpl, _initialOutboxSize, _initialLocalQueueSize));
    }

    @Override
    public BlockingReactorImpl asReactorImpl() {
        return (BlockingReactorImpl) super.asReactorImpl();
    }

    /**
     * Define the activity which occurs when the input queue is empty.
     * By default, nothing is done.
     *
     * @param _idle    The activity which occurs when the input queue is empty.
     */
    public void setIdle(final Runnable _idle) {
        ((BlockingReactorImpl) asReactorImpl()).onIdle = _idle;
    }

    @Override
    public BlockingReactor getReactor() {
        return this;
    }
}
