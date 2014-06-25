package org.agilewiki.jactor2.core.requests;

import org.agilewiki.jactor2.core.blades.Blade;
import org.agilewiki.jactor2.core.plant.impl.PlantImpl;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.reactors.ReactorClosedException;
import org.agilewiki.jactor2.core.requests.impl.RequestImpl;
import org.agilewiki.jactor2.core.util.GwtIncompatible;
import org.agilewiki.jactor2.core.util.Timer;

/**
 * A sync request performs an operation safely within the thread context of the target reactor.
 *
 * @param <RESPONSE_TYPE> The type of response value.
 */
abstract public class SyncRequest<RESPONSE_TYPE> implements
        Request<RESPONSE_TYPE>, SyncOperation<RESPONSE_TYPE> {

    private final RequestImpl<RESPONSE_TYPE> requestImpl;

    /**
     * Create a SyncRequest.
     *
     * @param _targetReactor The targetReactor where this SyncRequest object is passed for processing.
     *                       The thread owned by this targetReactor will process this SyncRequest.
     */
    public SyncRequest(final Reactor _targetReactor) {
        requestImpl = PlantImpl.getSingleton().createSyncRequestImpl(this,
                _targetReactor);
    }

    /**
     * Create a SyncRequest.
     *
     * @param _targetBlade Provides the targetReactor where this SyncRequest object is passed for processing.
     *                       The thread owned by this targetReactor will process this SyncRequest.
     */
    public SyncRequest(final Blade _targetBlade) {
        this(_targetBlade.getReactor());
    }

    /**
     * The processSyncRequest method will be invoked by the target Reactor on its own thread.
     *
     * @return The value returned by the target blades.
     */
    abstract public RESPONSE_TYPE processSyncRequest() throws Exception;

    @Override
    public RequestImpl<RESPONSE_TYPE> asRequestImpl() {
        return requestImpl;
    }

    @Override
    public Reactor getTargetReactor() {
        return requestImpl.getTargetReactor();
    }

    @Override
    public Reactor getSourceReactor() {
        return requestImpl.getSourceReactor();
    }

    @Override
    public void signal() {
        requestImpl.signal();
    }

    @GwtIncompatible
    @Override
    public RESPONSE_TYPE call() throws Exception {
        return requestImpl.call();
    }

    @Override
    public boolean isCanceled() throws ReactorClosedException {
        return requestImpl.isCanceled();
    }

    /** Returns the default Timer. */
    @Override
    public Timer getTimer() {
        return Timer.DEFAULT;
    }

    public <RT> RT syncDirect(final SOp<RT> _sOp)
            throws Exception {
        _sOp.targetReactor.directCheck(getTargetReactor());
        return _sOp.processSyncOperation(this);
    }
}
