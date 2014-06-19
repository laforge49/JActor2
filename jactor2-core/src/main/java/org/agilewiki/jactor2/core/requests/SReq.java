package org.agilewiki.jactor2.core.requests;

import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.reactors.ReactorBase;

/**
 * The boilerplate-free alternative to AsyncRequest.
 */
public abstract class SReq<RESPONSE_TYPE> {
    public final ReactorBase targetReactor;

    public SReq(final Reactor _targetReactor) {
        targetReactor = (ReactorBase) _targetReactor;
    }

    /**
     * The processSyncRequest method will be invoked by the target Reactor on its own thread.
     *
     * @param _request              The request context.
     */
    abstract protected RESPONSE_TYPE processSyncRequest(final Request _request)
            throws Exception;

    public void signal() {
        SyncRequest<RESPONSE_TYPE> syncRequest = new SyncRequest<RESPONSE_TYPE>(targetReactor) {
            @Override
            public RESPONSE_TYPE processSyncRequest() throws Exception {
                return SReq.this.processSyncRequest(this);
            }
        };
        syncRequest.signal();
    }

    public RESPONSE_TYPE call() throws Exception {
        SyncRequest<RESPONSE_TYPE> syncRequest = new SyncRequest<RESPONSE_TYPE>(targetReactor) {
            @Override
            public RESPONSE_TYPE processSyncRequest() throws Exception {
                return SReq.this.processSyncRequest(this);
            }
        };
        return syncRequest.call();
    }
}
