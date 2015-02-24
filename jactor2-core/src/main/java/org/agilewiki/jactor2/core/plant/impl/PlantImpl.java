package org.agilewiki.jactor2.core.plant.impl;

import org.agilewiki.jactor2.core.messages.AsyncOperation;
import org.agilewiki.jactor2.core.messages.alt.AsyncRequestImplWithData;
import org.agilewiki.jactor2.core.messages.SyncOperation;
import org.agilewiki.jactor2.core.messages.impl.AsyncRequestImpl;
import org.agilewiki.jactor2.core.messages.impl.RequestImpl;
import org.agilewiki.jactor2.core.messages.impl.RequestImplWithData;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.reactors.closeable.Closeable;
import org.agilewiki.jactor2.core.reactors.closeable.impl.CloseableImpl;
import org.agilewiki.jactor2.core.reactors.facilities.Facility;
import org.agilewiki.jactor2.core.reactors.impl.ReactorImpl;

/**
 * Internal implementation for Plant.
 */
abstract public class PlantImpl {

    public static final String PLANT_INTERNAL_FACILITY_NAME = "PlantInternalFacility";

    private static volatile PlantImpl singleton;

    /**
     * Returns this singleton.
     *
     * @return This singleton.
     */
    public static PlantImpl getSingleton() {
        return singleton;
    }

    /**
     * Create the singleton.
     */
    public PlantImpl() {
        if (singleton != null) {
            throw new IllegalStateException("the singleton already exists");
        }
        singleton = this;
    }

    /**
     * Returns the Plant's internal reactor.
     *
     * @return The reactor belonging to the singleton.
     */
    abstract public Facility getInternalFacility();

    /**
     * Close the Plant.
     */
    public void close() throws Exception {
        singleton = null;
    }

    abstract public ReactorImpl getCurrentReactorImpl();

    abstract public ReactorImpl createNonBlockingReactorImpl(
            final IsolationReactor _parentReactor, int _initialOutboxSize,
            int _initialLocalQueueSize);

    abstract public ReactorImpl createBlockingReactorImpl(
            final IsolationReactor _parentReactor, int _initialOutboxSize,
            int _initialLocalQueueSize);

    abstract public ReactorImpl createIsolationReactorImpl(
            final IsolationReactor _parentReactor, int _initialOutboxSize,
            int _initialLocalQueueSize);

    abstract public ReactorImpl createSwingBoundReactorImpl(
            final IsolationReactor _parentReactor, int _initialOutboxSize,
            int _initialLocalQueueSize);

    abstract public ReactorImpl createThreadBoundReactorImpl(
            final IsolationReactor _parentReactor, int _initialOutboxSize,
            int _initialLocalQueueSize, Runnable _boundProcessor);

    abstract public <RESPONSE_TYPE> RequestImpl<RESPONSE_TYPE> createSyncRequestImpl(
            SyncOperation<RESPONSE_TYPE> _syncOperation, Reactor _targetReactor);

    abstract public <RESPONSE_TYPE> AsyncRequestImpl<RESPONSE_TYPE> createAsyncRequestImpl(
            AsyncOperation<RESPONSE_TYPE> _asyncOperation,
            Reactor _targetReactor);

    abstract public <RESPONSE_TYPE> RequestImplWithData<RESPONSE_TYPE> createSyncRequestImplWithData(
            SyncOperation<RESPONSE_TYPE> _syncOperation, Reactor _targetReactor);

    abstract public <RESPONSE_TYPE> AsyncRequestImplWithData<RESPONSE_TYPE> createAsyncRequestImplWithData(
            AsyncOperation<RESPONSE_TYPE> _asyncOperation,
            Reactor _targetReactor);

    abstract public CloseableImpl createCloseableImpl(Closeable _closeable);

    /**
     * Returns 16.
     *
     * @return The reactor default initial local message queue size.
     */
    abstract public int getInitialLocalMessageQueueSize();

    /**
     * Returns 16.
     *
     * @return The reactor default initial buffer size.
     */
    abstract public int getInitialBufferSize();

    /**
     * Return the scheduler that is a part of the Plant's configuration.
     *
     * @return The scheduler.
     */
    abstract public PlantScheduler getPlantScheduler();
}
