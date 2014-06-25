package org.agilewiki.jactor2.core.requests;

import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.reactors.ReactorBase;
import org.agilewiki.jactor2.core.requests.impl.RequestImpl;
import org.agilewiki.jactor2.core.util.GwtIncompatible;
import org.agilewiki.jactor2.core.util.Timer;

/**
 * A synchronous operation, optionally used to define a SyncRequest.
 */
public abstract class SOp<RESPONSE_TYPE> implements SyncOperation<RESPONSE_TYPE> {
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
    public void signal() {
        SyncRequest<RESPONSE_TYPE> syncRequest = new SyncRequest<RESPONSE_TYPE>(targetReactor) {
            @Override
            public RESPONSE_TYPE processSyncRequest() throws Exception {
                return SOp.this.processSyncOperation(asRequestImpl());
            }

            @Override
            public String toString() {
                return SOp.this.toString();
            }
        };
        syncRequest.signal();
    }

    @GwtIncompatible
    @Override
    public RESPONSE_TYPE call() throws Exception {
        SyncRequest<RESPONSE_TYPE> syncRequest = new SyncRequest<RESPONSE_TYPE>(targetReactor) {
            @Override
            public RESPONSE_TYPE processSyncRequest() throws Exception {
                return SOp.this.processSyncOperation(asRequestImpl());
            }

            @Override
            public String toString() {
                return SOp.this.toString();
            }
        };
        return syncRequest.call();
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
