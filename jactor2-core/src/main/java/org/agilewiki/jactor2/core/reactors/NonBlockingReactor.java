package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.blades.NonBlockingBlade;
import org.agilewiki.jactor2.core.plant.impl.PlantBase;
import org.agilewiki.jactor2.core.plant.impl.PlantImpl;
import org.agilewiki.jactor2.core.reactors.impl.PoolThreadReactorImpl;
import org.agilewiki.jactor2.core.reactors.impl.ReactorImpl;

/**
 * Process requests/responses quickly and without blocking the thread.
 * <p>
 * Requests/responses are processed one at a time in the order received, except that
 * requests/responses from the same reactor are given preference.
 * </p>
 * <p>
 * Requests/responses destined to a different reactor are held until all
 * incoming messages have been processed.
 * </p>
 */
public class NonBlockingReactor extends ReactorBase implements CommonReactor,
        NonBlockingBlade {

    /**
     * Create a non-blocking reactor with the Plant internal reactor as the parent.
     */
    public NonBlockingReactor() throws Exception {
        this(PlantBase.getInternalFacility());
    }

    /**
     * Create a non-blocking reactor.
     *
     * @param _parentReactor            The parent reactor.
     */
    public NonBlockingReactor(final IsolationReactor _parentReactor)
            throws Exception {
        this(_parentReactor, _parentReactor != null ? _parentReactor
                .asReactorImpl().getInitialBufferSize() : PlantImpl
                .getSingleton().getInternalFacility() != null ? PlantImpl
                .getSingleton().getInternalFacility().asReactorImpl()
                .getInitialBufferSize() : PlantImpl.getSingleton()
                .getInitialBufferSize(),
                _parentReactor != null ? _parentReactor.asReactorImpl()
                        .getInitialLocalQueueSize() : PlantImpl.getSingleton()
                        .getInternalFacility() != null ? PlantImpl
                        .getSingleton().getInternalFacility().asReactorImpl()
                        .getInitialLocalQueueSize() : PlantImpl.getSingleton()
                        .getInitialLocalMessageQueueSize());
    }

    /**
     * Create a non-blocking reactor with the Plant internal reactor as the parent.
     *
     * @param _initialOutboxSize        Initial size of the list of requests/responses for each destination.
     * @param _initialLocalQueueSize    Initial size of the local input queue.
     */
    public NonBlockingReactor(final int _initialOutboxSize,
            final int _initialLocalQueueSize) throws Exception {
        this(PlantBase.getInternalFacility(), _initialOutboxSize,
                _initialLocalQueueSize);
    }

    /**
     * Create a non-blocking reactor.
     *
     * @param _parentReactor            The parent reactor.
     * @param _initialOutboxSize        Initial size of the list of requests/responses for each destination.
     * @param _initialLocalQueueSize    Initial size of the local input queue.
     */
    public NonBlockingReactor(final IsolationReactor _parentReactor,
            final int _initialOutboxSize, final int _initialLocalQueueSize)
            throws Exception {
        initialize(createReactorImpl(_parentReactor, _initialOutboxSize,
                _initialLocalQueueSize));
    }

    /**
     * Create the object used to implement the reactor.
     *
     * @param _parentReactor        The parent reactor impl object.
     * @param _initialOutboxSize        Initial size of the list of requests/responses for each destination.
     * @param _initialLocalQueueSize    Initial size of the local input queue.
     * @return The object used to implement the reactor.
     */
    protected ReactorImpl createReactorImpl(
            final IsolationReactor _parentReactor,
            final int _initialOutboxSize, final int _initialLocalQueueSize) {
        return PlantImpl.getSingleton().createNonBlockingReactorImpl(
                _parentReactor, _initialOutboxSize, _initialLocalQueueSize);
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
    public NonBlockingReactor getReactor() {
        return this;
    }
}
