package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.GwtIncompatible;
import org.agilewiki.jactor2.core.blades.BlockingBlade;
import org.agilewiki.jactor2.core.plant.impl.PlantBase;
import org.agilewiki.jactor2.core.plant.impl.PlantImpl;
import org.agilewiki.jactor2.core.reactors.impl.PoolThreadReactorImpl;

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
@GwtIncompatible
public class BlockingReactor extends ReactorBase implements CommonReactor,
        BlockingBlade {

    /**
     * Create a blocking reactor with the Plant internal reactor as the parent.
     */
    public BlockingReactor() throws Exception {
        this(PlantBase.getInternalFacility());
    }

    /**
     * Create a blocking reactor.
     *
     * @param _parentReactor            The parent reactor.
     */
    public BlockingReactor(final IsolationReactor _parentReactor)
            throws Exception {
        this(_parentReactor, _parentReactor.asReactorImpl()
                .getInitialBufferSize(), _parentReactor.asReactorImpl()
                .getInitialLocalQueueSize());
    }

    /**
     * Create a blocking reactor with the Plant internal reactor as the parent.
     *
     * @param _initialOutboxSize        Initial size of the list of requests/responses for each destination.
     * @param _initialLocalQueueSize    Initial size of the local input queue.
     */
    public BlockingReactor(final int _initialOutboxSize,
            final int _initialLocalQueueSize) throws Exception {
        this(PlantBase.getInternalFacility(), _initialOutboxSize,
                _initialLocalQueueSize);
    }

    /**
     * Create a blocking reactor.
     *
     * @param _parentReactor            The parent reactor.
     * @param _initialOutboxSize        Initial size of the list of requests/responses for each destination.
     * @param _initialLocalQueueSize    Initial size of the local input queue.
     */
    public BlockingReactor(final IsolationReactor _parentReactor,
            final int _initialOutboxSize, final int _initialLocalQueueSize)
            throws Exception {
        initialize(PlantImpl.getSingleton().createBlockingReactorImpl(
                _parentReactor, _initialOutboxSize, _initialLocalQueueSize));
    }

    /**
     * Define the activity which occurs when the input queue is empty.
     * By default, nothing is done.
     *
     * @param _idle    The activity which occurs when the input queue is empty.
     */
    public void setIdle(final Runnable _idle) {
        ((PoolThreadReactorImpl) asReactorImpl()).setOnIdle(_idle);
    }

    @Override
    public BlockingReactor getReactor() {
        return this;
    }
}
