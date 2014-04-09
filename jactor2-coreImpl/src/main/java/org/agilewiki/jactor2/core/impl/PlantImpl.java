package org.agilewiki.jactor2.core.impl;

import org.agilewiki.jactor2.core.impl.plantImpl.PlantImplBase;
import org.agilewiki.jactor2.core.impl.reactorsImpl.NonBlockingReactorImpl;
import org.agilewiki.jactor2.core.impl.reactorsImpl.ReactorImpl;
import org.agilewiki.jactor2.core.plant.PlantConfiguration;

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

    public ReactorImpl createSwingBoundReactorImpl(final NonBlockingReactorImpl _parentReactorImpl,
                                                  final int _initialOutboxSize, final int _initialLocalQueueSize) {
        return new SwingBoundReactorImpl(_parentReactorImpl, _initialOutboxSize, _initialLocalQueueSize);
    }
}
