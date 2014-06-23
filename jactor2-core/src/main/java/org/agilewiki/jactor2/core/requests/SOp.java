package org.agilewiki.jactor2.core.requests;

import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.reactors.ReactorBase;

/**
 * A synchronous operation, optionally used to define a SyncRequest.
 */
public abstract class SOp<RESPONSE_TYPE> implements Op<RESPONSE_TYPE> {
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

    /**
     * The processSyncRequest method will be invoked by the target Reactor on its own thread.
     *
     * @param _request              The request context.
     */
    abstract protected RESPONSE_TYPE processSyncOperation(final Request _request)
            throws Exception;

    @Override
    public void signal() {
        SyncRequest<RESPONSE_TYPE> syncRequest = new SyncRequest<RESPONSE_TYPE>(targetReactor) {
            @Override
            public RESPONSE_TYPE processSyncRequest() throws Exception {
                return SOp.this.processSyncOperation(this);
            }

            @Override
            public String toString() {
                return SOp.this.toString();
            }
        };
        syncRequest.signal();
    }

    @Override
    public RESPONSE_TYPE call() throws Exception {
        SyncRequest<RESPONSE_TYPE> syncRequest = new SyncRequest<RESPONSE_TYPE>(targetReactor) {
            @Override
            public RESPONSE_TYPE processSyncRequest() throws Exception {
                return SOp.this.processSyncOperation(this);
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
}
