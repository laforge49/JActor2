package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.blades.ThreadBoundBlade;
import org.agilewiki.jactor2.core.impl.NonBlockingReactorImpl;
import org.agilewiki.jactor2.core.impl.ReactorImpl;
import org.agilewiki.jactor2.core.impl.ThreadBoundReactorImpl;
import org.agilewiki.jactor2.core.plant.Plant;

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
public class ThreadBoundReactor extends ReactorBase
        implements CommonReactor, Runnable, ThreadBoundBlade {

    /**
     * Create a thread-bound reactor with the Plant internal reactor as the parent.
     */
    public ThreadBoundReactor() {
        this(Plant.getInternalReactor());
    }

    /**
     * Create a thread-bound reactor.
     *
     * @param _parentReactor            The parent reactor.
     */
    public ThreadBoundReactor(final NonBlockingReactor _parentReactor) {
        this(_parentReactor, _parentReactor.asReactorImpl().getInitialBufferSize(),
                _parentReactor.asReactorImpl().getInitialLocalQueueSize(), null);
    }

    /**
     * Create a thread-bound reactor with the Plant internal reactor as the parent.
     *
     * @param _boundProcessor           The Runnable that is called when there are requests/responses
     *                                  to be processed.
     */
    public ThreadBoundReactor(final Runnable _boundProcessor) {
        this(Plant.getInternalReactor(), _boundProcessor);
    }

    /**
     * Create a thread-bound reactor.
     *
     * @param _parentReactor            The parent reactor.
     * @param _boundProcessor           The Runnable that is called when there are requests/responses
     *                                  to be processed.
     */
    public ThreadBoundReactor(final NonBlockingReactor _parentReactor, final Runnable _boundProcessor) {
        this(_parentReactor, _parentReactor.asReactorImpl().getInitialBufferSize(),
                _parentReactor.asReactorImpl().getInitialLocalQueueSize(), _boundProcessor);
    }

    /**
     * Create a thread-bound reactor with the Plant internal reactor as the parent.
     *
     * @param _initialOutboxSize        Initial size of the list of requests/responses for each destination.
     * @param _initialLocalQueueSize    Initial size of the local input queue.
     * @param _boundProcessor           The Runnable that is called when there are requests/responses
     *                                  to be processed.
     */
    public ThreadBoundReactor(final int _initialOutboxSize, final int _initialLocalQueueSize,
                           final Runnable _boundProcessor) {
        this(Plant.getInternalReactor(), _initialOutboxSize, _initialLocalQueueSize, _boundProcessor);
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
    public ThreadBoundReactor(final NonBlockingReactor _parentReactor,
                           final int _initialOutboxSize, final int _initialLocalQueueSize,
                           final Runnable _boundProcessor) {
        initialize(createReactorImpl(_parentReactor.asReactorImpl(), _initialOutboxSize, _initialLocalQueueSize,
                _boundProcessor));
    }

    /**
     * Create the object used to implement the reactor.
     *
     * @param _parentReactorImpl        The parent reactor impl object.
     * @param _initialOutboxSize        Initial size of the list of requests/responses for each destination.
     * @param _initialLocalQueueSize    Initial size of the local input queue.
     * @param _boundProcessor           The Runnable that is called when there are requests/responses
     *                                  to be processed.
     * @return The object used to implement the reactor.
     */
    protected ReactorImpl createReactorImpl(final NonBlockingReactorImpl _parentReactorImpl,
                                            final int _initialOutboxSize, final int _initialLocalQueueSize,
                                            final Runnable _boundProcessor) {
        return new ThreadBoundReactorImpl(_parentReactorImpl, _initialOutboxSize, _initialLocalQueueSize,
                _boundProcessor);
    }

    @Override
    public ThreadBoundReactorImpl asReactorImpl() {
        return (ThreadBoundReactorImpl) super.asReactorImpl();
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
