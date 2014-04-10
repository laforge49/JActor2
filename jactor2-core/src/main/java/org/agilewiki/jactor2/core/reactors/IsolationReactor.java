package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.blades.IsolationBlade;
import org.agilewiki.jactor2.core.impl.plantImpl.PlantBaseImpl;
import org.agilewiki.jactor2.core.impl.reactorsImpl.PoolThreadReactorImpl;
import org.agilewiki.jactor2.core.plant.PlantBase;

/**
 * Processes each request until completion, processing responses and 1-way messages (e.g. signals) in the order received.
 * The next request is only processed when a result is returned for the current request.
 * <p>
 * Requests/responses destined to a different reactor are held until processing is complete
 * for each incoming request/response.
 * </p>
 */
public class IsolationReactor extends ReactorBase implements IsolationBlade {

    /**
     * Create an isolation reactor with the Plant internal reactor as the parent.
     */
    public IsolationReactor() {
        this(PlantBase.getInternalReactor());
    }

    /**
     * Create an isolation reactor.
     *
     * @param _parentReactor            The parent reactor.
     */
    public IsolationReactor(final NonBlockingReactor _parentReactor) {
        this(_parentReactor, _parentReactor.asReactorImpl().getInitialBufferSize(),
                _parentReactor.asReactorImpl().getInitialLocalQueueSize());
    }

    /**
     * Create an isolation reactor with the Plant internal reactor as the parent.
     *
     * @param _initialOutboxSize        Initial size of the list of requests/responses for each destination.
     * @param _initialLocalQueueSize    Initial size of the local input queue.
     */
    public IsolationReactor(final int _initialOutboxSize, final int _initialLocalQueueSize) {
        this(PlantBase.getInternalReactor(), _initialOutboxSize, _initialLocalQueueSize);
    }

    /**
     * Create an isolation reactor.
     *
     * @param _parentReactor            The parent reactor.
     * @param _initialOutboxSize        Initial size of the list of requests/responses for each destination.
     * @param _initialLocalQueueSize    Initial size of the local input queue.
     */
    public IsolationReactor(final NonBlockingReactor _parentReactor,
                           final int _initialOutboxSize, final int _initialLocalQueueSize) {
        initialize(PlantBaseImpl.getSingleton().createIsolationReactorImpl(
                _parentReactor, _initialOutboxSize, _initialLocalQueueSize));
    }

    /**
     * Define the activity which occurs when the input queue is empty.
     * By default, nothing is done.
     *
     * @param _idle    The activity which occurs when the input queue is empty.
     */
    public void setIdle(final Runnable _idle) {
        ((PoolThreadReactorImpl) asReactorImpl()).onIdle = _idle;
    }

    @Override
    public IsolationReactor getReactor() {
        return this;
    }
}
