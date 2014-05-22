package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.blades.IsolationBlade;
import org.agilewiki.jactor2.core.plant.PlantBase;
import org.agilewiki.jactor2.core.plant.PlantImpl;

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
    public IsolationReactor() throws Exception {
        this(PlantBase.getInternalFacility());
    }

    /**
     * Create an isolation reactor.
     *
     * @param _parentReactor            The parent reactor.
     */
    public IsolationReactor(final NonBlockingReactor _parentReactor) throws Exception {
        this(_parentReactor, _parentReactor.asReactorImpl().getInitialBufferSize(),
                _parentReactor.asReactorImpl().getInitialLocalQueueSize());
    }

    /**
     * Create an isolation reactor with the Plant internal reactor as the parent.
     *
     * @param _initialOutboxSize        Initial size of the list of requests/responses for each destination.
     * @param _initialLocalQueueSize    Initial size of the local input queue.
     */
    public IsolationReactor(final int _initialOutboxSize, final int _initialLocalQueueSize) throws Exception {
        this(PlantBase.getInternalFacility(), _initialOutboxSize, _initialLocalQueueSize);
    }

    /**
     * Create an isolation reactor.
     *
     * @param _parentReactor            The parent reactor.
     * @param _initialOutboxSize        Initial size of the list of requests/responses for each destination.
     * @param _initialLocalQueueSize    Initial size of the local input queue.
     */
    public IsolationReactor(final NonBlockingReactor _parentReactor,
                           final int _initialOutboxSize, final int _initialLocalQueueSize) throws Exception {
        initialize(PlantImpl.getSingleton().createIsolationReactorImpl(
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
    public IsolationReactor getReactor() {
        return this;
    }
}
