package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.GwtIncompatible;
import org.agilewiki.jactor2.core.blades.ThreadBoundBlade;
import org.agilewiki.jactor2.core.plant.impl.PlantBase;
import org.agilewiki.jactor2.core.plant.impl.PlantImpl;
import org.agilewiki.jactor2.core.reactors.impl.ReactorImpl;

/**
 * A reactor bound to a pre-existing thread.
 * <p>
 * Requests/responses are processed one at a time in the order received, except that
 * requests/responses from the same reactor are given preference.
 * </p>
 * <p>
 * Requests/responses destined to a different reactor are held until all
 * incoming messages have been processed.
 * </p>
 */
@GwtIncompatible
public class ThreadBoundReactor extends ReactorBase implements CommonReactor,
        Runnable, ThreadBoundBlade {

    /**
     * Create a thread-bound reactor with the Plant internal reactor as the parent.
     */
    public ThreadBoundReactor() throws Exception {
        this(PlantBase.getInternalFacility());
    }

    /**
     * Create a thread-bound reactor.
     *
     * @param _parentReactor            The parent reactor.
     */
    public ThreadBoundReactor(final IsolationReactor _parentReactor)
            throws Exception {
        this(_parentReactor, _parentReactor.asReactorImpl()
                .getInitialBufferSize(), _parentReactor.asReactorImpl()
                .getInitialLocalQueueSize(), null);
    }

    /**
     * Create a thread-bound reactor with the Plant internal reactor as the parent.
     *
     * @param _boundProcessor           The Runnable that is called when there are requests/responses
     *                                  to be processed.
     */
    public ThreadBoundReactor(final Runnable _boundProcessor) throws Exception {
        this(PlantBase.getInternalFacility(), _boundProcessor);
    }

    /**
     * Create a thread-bound reactor.
     *
     * @param _parentReactor            The parent reactor.
     * @param _boundProcessor           The Runnable that is called when there are requests/responses
     *                                  to be processed.
     */
    public ThreadBoundReactor(final IsolationReactor _parentReactor,
            final Runnable _boundProcessor) throws Exception {
        this(_parentReactor, _parentReactor.asReactorImpl()
                .getInitialBufferSize(), _parentReactor.asReactorImpl()
                .getInitialLocalQueueSize(), _boundProcessor);
    }

    /**
     * Create a thread-bound reactor with the Plant internal reactor as the parent.
     *
     * @param _initialOutboxSize        Initial size of the list of requests/responses for each destination.
     * @param _initialLocalQueueSize    Initial size of the local input queue.
     * @param _boundProcessor           The Runnable that is called when there are requests/responses
     *                                  to be processed.
     */
    public ThreadBoundReactor(final int _initialOutboxSize,
            final int _initialLocalQueueSize, final Runnable _boundProcessor)
            throws Exception {
        this(PlantBase.getInternalFacility(), _initialOutboxSize,
                _initialLocalQueueSize, _boundProcessor);
    }

    /**
     * Create a thread-bound reactor.
     *
     * @param _parentReactor            The parent reactor.
     * @param _initialOutboxSize        Initial size of the list of requests/responses for each destination.
     * @param _initialLocalQueueSize    Initial size of the local input queue.
     * @param _boundProcessor           The Runnable that is called when there are requests/responses
     *                                  to be processed.
     */
    public ThreadBoundReactor(final IsolationReactor _parentReactor,
            final int _initialOutboxSize, final int _initialLocalQueueSize,
            final Runnable _boundProcessor) throws Exception {
        initialize(createReactorImpl(_parentReactor, _initialOutboxSize,
                _initialLocalQueueSize, _boundProcessor));
    }

    /**
     * Create the object used to implement the reactor.
     *
     * @param _parentReactor        The parent reactor object.
     * @param _initialOutboxSize        Initial size of the list of requests/responses for each destination.
     * @param _initialLocalQueueSize    Initial size of the local input queue.
     * @param _boundProcessor           The Runnable that is called when there are requests/responses
     *                                  to be processed.
     * @return The object used to implement the reactor.
     */
    protected ReactorImpl createReactorImpl(
            final IsolationReactor _parentReactor,
            final int _initialOutboxSize, final int _initialLocalQueueSize,
            final Runnable _boundProcessor) {
        return PlantImpl.getSingleton().createThreadBoundReactorImpl(
                _parentReactor, _initialOutboxSize, _initialLocalQueueSize,
                _boundProcessor);
    }

    /**
     * Call by the bound thread to process the requests/responses in the input queue.
     */
    @Override
    public void run() {
        asReactorImpl().run();
    }

    @Override
    public ThreadBoundReactor getReactor() {
        return this;
    }
}
