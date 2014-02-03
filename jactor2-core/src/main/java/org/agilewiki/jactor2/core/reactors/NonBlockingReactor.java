package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.impl.NonBlockingReactorImpl;
import org.agilewiki.jactor2.core.impl.PlantImpl;
import org.agilewiki.jactor2.core.plant.Plant;

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
public class NonBlockingReactor extends ReactorBase implements CommonReactor {

    /**
     * Create a reactor with the Plant internal reactor as the parent.
     */
    public NonBlockingReactor()
            throws Exception {
        this(Plant.getInternalReactor());
    }

    /**
     * Create a reactor.
     *
     * @param _parentReactor            The parent reactor.
     */
    public NonBlockingReactor(final NonBlockingReactor _parentReactor)
            throws Exception {
        this(_parentReactor,
                _parentReactor != null ?
                        _parentReactor.asReactorImpl().getInitialBufferSize() :
                        PlantImpl.getSingleton().getInternalReactor() != null ?
                                PlantImpl.getSingleton().getInternalReactor().asReactorImpl().getInitialBufferSize() :
                                PlantImpl.getSingleton().getPlantConfiguration().getInitialBufferSize(),
                _parentReactor != null ?
                        _parentReactor.asReactorImpl().getInitialLocalQueueSize() :
                        PlantImpl.getSingleton().getInternalReactor() != null ?
                                PlantImpl.getSingleton().getInternalReactor().asReactorImpl().getInitialLocalQueueSize() :
                                PlantImpl.getSingleton().getPlantConfiguration().getInitialLocalMessageQueueSize());
    }

    /**
     * Create a reactor with the Plant internal reactor as the parent.
     *
     * @param _initialOutboxSize        Initial size of the list of requests/responses for each destination.
     * @param _initialLocalQueueSize    Initial size of the local input queue.
     */
    public NonBlockingReactor(final int _initialOutboxSize, final int _initialLocalQueueSize) throws Exception {
        this(Plant.getInternalReactor(), _initialOutboxSize, _initialLocalQueueSize);
    }

    /**
     * Create a reactor.
     *
     * @param _parentReactor            The parent reactor.
     * @param _initialOutboxSize        Initial size of the list of requests/responses for each destination.
     * @param _initialLocalQueueSize    Initial size of the local input queue.
     */
    public NonBlockingReactor(final NonBlockingReactor _parentReactor,
                              final int _initialOutboxSize, final int _initialLocalQueueSize) throws Exception {
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
                                                       final int _initialOutboxSize, final int _initialLocalQueueSize)
            throws Exception {
        return new NonBlockingReactorImpl(_parentReactorImpl, _initialOutboxSize, _initialLocalQueueSize);
    }

    @Override
    public NonBlockingReactorImpl asReactorImpl() {
        return (NonBlockingReactorImpl) super.asReactorImpl();
    }

    /**
     * Define the activity which occurs when the input queue is empty.
     *
     * @param _idle    The activity which occurs when the input queue is empty.
     */
    public void setIdle(final Runnable _idle) {
        ((NonBlockingReactorImpl) asReactorImpl()).onIdle = _idle;
    }
}
