package org.agilewiki.jactor2.core.mt;

import org.agilewiki.jactor2.core.plant.PlantImpl;
import org.agilewiki.jactor2.core.reactors.ReactorImpl;
import org.agilewiki.jactor2.core.requests.AsyncRequestImpl;
import org.agilewiki.jactor2.core.requests.RequestImpl;
import org.agilewiki.jactor2.core.mt.mtReactors.*;
import org.agilewiki.jactor2.core.mt.mtRequests.AsyncRequestMtImpl;
import org.agilewiki.jactor2.core.mt.mtRequests.SyncRequestMtImpl;
import org.agilewiki.jactor2.core.plant.PlantConfiguration;
import org.agilewiki.jactor2.core.plant.ReactorPoolThread;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.SyncRequest;

public class PlantMtImpl extends PlantImpl {

    /**
     * Create the singleton with a default configuration.
     */
    public PlantMtImpl() {
        super();
    }

    /**
     * Create the singleton with the given thread pool size.
     *
     * @param _threadCount The size of the thread pool.
     */
    public PlantMtImpl(final int _threadCount) {
        super(_threadCount);
    }

    /**
     * Create the singleton with the given configuration.
     *
     * @param _plantConfiguration The configuration to be used by the singleton.
     */
    public PlantMtImpl(final PlantConfiguration _plantConfiguration) {
        super(_plantConfiguration);
    }

    public void removeThreadBoundReactor() {
        ThreadBoundReactorMtImpl.removeReactor();
    }

    public ReactorImpl getCurrentReactorImpl() {
        Thread thread = Thread.currentThread();
        if (thread instanceof ReactorPoolThread)
            return ((ReactorPoolThread) thread).getCurrentReactorImpl();
        return ThreadBoundReactorMtImpl.threadReactor();
    }

    public void validateCall() {
        if (Thread.currentThread() instanceof ReactorPoolThread) {
            throw new UnsupportedOperationException(
                    "Use of call on a ReactorPoolThread can result in a deadlock");
        } else if (ThreadBoundReactorMtImpl.threadReactor() != null)
            throw new UnsupportedOperationException(
                    "Use of call on a Thread bound to a reactor can result in a deadlock " + ThreadBoundReactorMtImpl.threadReactor());

    }

    public ReactorImpl createNonBlockingReactorImpl(final NonBlockingReactor _parentReactor,
                                                    final int _initialOutboxSize, final int _initialLocalQueueSize) {
        return new NonBlockingReactorMtImpl(_parentReactor, _initialOutboxSize, _initialLocalQueueSize);
    }

    public ReactorImpl createBlockingReactorImpl(final NonBlockingReactor _parentReactor,
                                                 final int _initialOutboxSize, final int _initialLocalQueueSize) {
        return new BlockingReactorMtImpl(_parentReactor, _initialOutboxSize, _initialLocalQueueSize);
    }

    public ReactorImpl createIsolationReactorImpl(final NonBlockingReactor _parentReactor,
                                                  final int _initialOutboxSize, final int _initialLocalQueueSize) {
        return new IsolationReactorMtImpl(_parentReactor, _initialOutboxSize, _initialLocalQueueSize);
    }

    public ReactorImpl createSwingBoundReactorImpl(final NonBlockingReactor _parentReactor,
                                                   final int _initialOutboxSize, final int _initialLocalQueueSize) {
        return new SwingBoundReactorMtImpl(_parentReactor, _initialOutboxSize, _initialLocalQueueSize);
    }

    public ReactorImpl createThreadBoundReactorImpl(final NonBlockingReactor _parentReactor,
                                                    final int _initialOutboxSize, final int _initialLocalQueueSize,
                                                    final Runnable _boundProcessor) {
        return new ThreadBoundReactorMtImpl(_parentReactor, _initialOutboxSize, _initialLocalQueueSize, _boundProcessor);
    }

    @Override
    public <RESPONSE_TYPE> RequestImpl<RESPONSE_TYPE> createSyncRequestImpl(SyncRequest<RESPONSE_TYPE> _syncRequest, Reactor _targetReactor) {
        return new SyncRequestMtImpl<RESPONSE_TYPE>(_syncRequest, _targetReactor);
    }

    @Override
    public <RESPONSE_TYPE> AsyncRequestImpl<RESPONSE_TYPE> createAsyncRequestImpl(AsyncRequest<RESPONSE_TYPE> _asyncRequest, Reactor _targetReactor) {
        return new AsyncRequestMtImpl<RESPONSE_TYPE>(_asyncRequest, _targetReactor);
    }
}
