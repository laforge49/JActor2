package org.agilewiki.jactor2.core.requests;

import org.agilewiki.jactor2.core.plant.impl.PlantImpl;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.reactors.ReactorBase;
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
        return Timer.DEFAULT;
    }
}
