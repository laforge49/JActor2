package org.agilewiki.jactor2.core.requests;

import org.agilewiki.jactor2.core.plant.impl.PlantImpl;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.reactors.ReactorBase;
import org.agilewiki.jactor2.core.requests.impl.RequestImpl;
import org.agilewiki.jactor2.core.util.GwtIncompatible;
import org.agilewiki.jactor2.core.util.Timer;

/**
 * A synchronous operation, optionally used to define a SyncRequest.
 */
public abstract class SOp<RESPONSE_TYPE> implements
        SyncOperation<RESPONSE_TYPE> {
    public final String opName;
    public final ReactorBase targetReactor;

    /**
     * Creata a synchronous operation.
     *
     * @param _opName           The operation name.
     * @param _targetReactor    The reactor whose thread will process the operation.
     */
    public SOp(final String _opName, final Reactor _targetReactor) {
        opName = _opName;
        targetReactor = (ReactorBase) _targetReactor;
    }

    @Override
    public RESPONSE_TYPE doSync(final RequestImpl _requestImpl)
            throws Exception {
        if (!_requestImpl.getTargetReactor().asReactorImpl().isRunning())
            throw new IllegalStateException(
                    "Not thread safe: not called from within an active request");
        return processSyncOperation(_requestImpl);
    }

    /**
     * The processSyncRequest method will be invoked by the target Reactor on its own thread.
     *
     * @return The value returned by the target blades.
     */
    protected abstract RESPONSE_TYPE processSyncOperation(
            final RequestImpl _requestImpl) throws Exception;

    public void signal() {
        PlantImpl.getSingleton().createSyncRequestImpl(this, targetReactor)
                .signal();
    }

    @GwtIncompatible
    public RESPONSE_TYPE call() throws Exception {
        return PlantImpl.getSingleton()
                .createSyncRequestImpl(this, targetReactor).call();
    }

    @Override
    public String toString() {
        return opName;
    }

    @Override
    public Timer getTimer() {
        return Timer.NOP;
    }
}
