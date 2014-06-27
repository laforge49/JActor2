package org.agilewiki.jactor2.core.requests;

import org.agilewiki.jactor2.core.plant.impl.PlantImpl;
import org.agilewiki.jactor2.core.reactors.CommonReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.reactors.ReactorBase;
import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;
import org.agilewiki.jactor2.core.util.GwtIncompatible;
import org.agilewiki.jactor2.core.util.Timer;

/**
 * An asynchronous operation, optionally used to define an AsyncRequest.
 */
public abstract class AOp<RESPONSE_TYPE> implements AsyncOperation<RESPONSE_TYPE> {
    public final String opName;
    public final ReactorBase targetReactor;

    /**
     * Create an asynchronous operation.
     *
     * @param _opName           The name of the operation.
     * @param _targetReactor    The reactor whose thread will process the operation.
     */
    public AOp(final String _opName, final Reactor _targetReactor) {
        opName = _opName;
        targetReactor = (ReactorBase) _targetReactor;
    }

    @Override
    public void signal() {
        PlantImpl.getSingleton().createAsyncRequestImpl(this, targetReactor).signal();
    }

    @GwtIncompatible
    @Override
    public RESPONSE_TYPE call() throws Exception {
        return PlantImpl.getSingleton().createAsyncRequestImpl(this, targetReactor).call();
    }

    @Override
    public String toString() {
        return opName;
    }

    @Override
    public Timer getTimer() {
        return Timer.DEFAULT;
    }

    /**
     * Cancels all outstanding requests.
     * This method is thread safe, so it can be called from any thread.
     */
    public void cancelAll(final AsyncRequestImpl _asyncRequestImpl) {
        _asyncRequestImpl.cancelAll();
    }

    @Override
    public void onCancel(final AsyncRequestImpl _asyncRequestImpl) {
        _asyncRequestImpl.onCancel(_asyncRequestImpl);
    }

    @Override
    public void onClose(final AsyncRequestImpl _asyncRequestImpl) {
        _asyncRequestImpl.onClose(_asyncRequestImpl);
    }
}
