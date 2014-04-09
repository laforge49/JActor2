package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.blades.NonBlockingBlade;
import org.agilewiki.jactor2.core.impl.plantImpl.PlantImplBase;
import org.agilewiki.jactor2.core.plant.PlantBase;
import org.agilewiki.jactor2.core.impl.reactorsImpl.NonBlockingReactorImpl;

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
public class NonBlockingReactor extends ReactorBase implements CommonReactor, NonBlockingBlade {

    /**
     * Create a non-blocking reactor with the Plant internal reactor as the parent.
     */
    public NonBlockingReactor() {
        this(PlantBase.getInternalReactor());
    }

    /**
     * Create a non-blocking reactor.
     *
     * @param _parentReactor            The parent reactor.
     */
    public NonBlockingReactor(final NonBlockingReactor _parentReactor) {
        this(_parentReactor,
                _parentReactor != null ?
                        _parentReactor.asReactorImpl().getInitialBufferSize() :
                        PlantImplBase.getSingleton().getInternalReactor() != null ?
                                PlantImplBase.getSingleton().getInternalReactor().asReactorImpl().getInitialBufferSize() :
                                PlantImplBase.getSingleton().getPlantConfiguration().getInitialBufferSize(),
                _parentReactor != null ?
                        _parentReactor.asReactorImpl().getInitialLocalQueueSize() :
                        PlantImplBase.getSingleton().getInternalReactor() != null ?
                                PlantImplBase.getSingleton().getInternalReactor().asReactorImpl().getInitialLocalQueueSize() :
                                PlantImplBase.getSingleton().getPlantConfiguration().getInitialLocalMessageQueueSize());
    }

    /**
     * Create a non-blocking reactor with the Plant internal reactor as the parent.
     *
     * @param _initialOutboxSize        Initial size of the list of requests/responses for each destination.
     * @param _initialLocalQueueSize    Initial size of the local input queue.
     */
    public NonBlockingReactor(final int _initialOutboxSize, final int _initialLocalQueueSize) {
        this(PlantBase.getInternalReactor(), _initialOutboxSize, _initialLocalQueueSize);
    }

    /**
     * Create a non-blocking reactor.
     *
     * @param _parentReactor            The parent reactor.
     * @param _initialOutboxSize        Initial size of the list of requests/responses for each destination.
     * @param _initialLocalQueueSize    Initial size of the local input queue.
     */
    public NonBlockingReactor(final NonBlockingReactor _parentReactor,
                              final int _initialOutboxSize, final int _initialLocalQueueSize) {
        initialize(createReactorImpl(_parentReactor == null ? null : _parentReactor.asReactorImpl(),
                _initialOutboxSize, _initialLocalQueueSize));
    }

    /**
     * Create the object used to implement the reactor.
     *
     * @param _parentReactorImpl        The parent reactor impl object.
     * @param _initialOutboxSize        Initial size of the list of requests/responses for each destination.
     * @param _initialLocalQueueSize    Initial size of the local input queue.
     * @return The object used to implement the reactor.
     */
    protected NonBlockingReactorImpl createReactorImpl(final NonBlockingReactorImpl _parentReactorImpl,
                                                       final int _initialOutboxSize, final int _initialLocalQueueSize) {
        return new NonBlockingReactorImpl(_parentReactorImpl, _initialOutboxSize, _initialLocalQueueSize);
    }

    @Override
    public NonBlockingReactorImpl asReactorImpl() {
        return (NonBlockingReactorImpl) super.asReactorImpl();
    }

    /**
     * Define the activity which occurs when the input queue is empty.
     * By default, nothing is done.
     *
     * @param _idle    The activity which occurs when the input queue is empty.
     */
    public void setIdle(final Runnable _idle) {
        ((NonBlockingReactorImpl) asReactorImpl()).onIdle = _idle;
    }

    @Override
    public NonBlockingReactor getReactor() {
        return this;
    }
}
