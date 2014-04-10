package org.agilewiki.jactor2.core.impl;

import org.agilewiki.jactor2.core.impl.plantImpl.PlantImplBase;
import org.agilewiki.jactor2.core.impl.reactorsImpl.ReactorImpl;
import org.agilewiki.jactor2.core.plant.PlantConfiguration;
import org.agilewiki.jactor2.core.plant.ReactorPoolThread;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class PlantImpl extends PlantImplBase {

    /**
     * Create the singleton with a default configuration.
     */
    public PlantImpl() {
        super();
    }

    /**
     * Create the singleton with the given thread pool size.
     *
     * @param _threadCount The size of the thread pool.
     */
    public PlantImpl(final int _threadCount) {
        super(_threadCount);
    }

    /**
     * Create the singleton with the given configuration.
     *
     * @param _plantConfiguration The configuration to be used by the singleton.
     */
    public PlantImpl(final PlantConfiguration _plantConfiguration) {
        super(_plantConfiguration);
    }

    public void removeThreadBoundReactor() {
        ThreadBoundReactorImpl.removeReactor();
    }

    public ReactorImpl getCurrentReactorImpl() {
        Thread thread = Thread.currentThread();
        if (thread instanceof ReactorPoolThread)
            return ((ReactorPoolThread) thread).getCurrentReactorImpl();
        return ThreadBoundReactorImpl.threadReactor();
    }

    public void validateCall() {
        if (Thread.currentThread() instanceof ReactorPoolThread) {
            throw new UnsupportedOperationException(
                    "Use of call on a ReactorPoolThread can result in a deadlock");
        } else if (ThreadBoundReactorImpl.threadReactor() != null)
            throw new UnsupportedOperationException(
                    "Use of call on a Thread bound to a reactor can result in a deadlock " + ThreadBoundReactorImpl.threadReactor());

    }

    public ReactorImpl createNonBlockingReactorImpl(final NonBlockingReactor _parentReactor,
                                                 final int _initialOutboxSize, final int _initialLocalQueueSize) {
        return new NonBlockingReactorImpl(_parentReactor, _initialOutboxSize, _initialLocalQueueSize);
    }

    public ReactorImpl createBlockingReactorImpl(final NonBlockingReactor _parentReactor,
                                                 final int _initialOutboxSize, final int _initialLocalQueueSize) {
        return new BlockingReactorImpl(_parentReactor, _initialOutboxSize, _initialLocalQueueSize);
    }

    public ReactorImpl createIsolationReactorImpl(final NonBlockingReactor _parentReactor,
                                                 final int _initialOutboxSize, final int _initialLocalQueueSize) {
        return new IsolationReactorImpl(_parentReactor, _initialOutboxSize, _initialLocalQueueSize);
    }

    public ReactorImpl createSwingBoundReactorImpl(final NonBlockingReactor _parentReactor,
                                                   final int _initialOutboxSize, final int _initialLocalQueueSize) {
        return new SwingBoundReactorImpl(_parentReactor, _initialOutboxSize, _initialLocalQueueSize);
    }

    public ReactorImpl createThreadBoundReactorImpl(final NonBlockingReactor _parentReactor,
                                                    final int _initialOutboxSize, final int _initialLocalQueueSize,
                                                    final Runnable _boundProcessor) {
        return new ThreadBoundReactorImpl(_parentReactor, _initialOutboxSize, _initialLocalQueueSize, _boundProcessor);
    }
}
