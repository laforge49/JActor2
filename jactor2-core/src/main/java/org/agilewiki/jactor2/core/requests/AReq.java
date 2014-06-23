package org.agilewiki.jactor2.core.requests;

import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.reactors.ReactorBase;

/**
 * An asynchronous operation, optionally used to define an AsyncRequest.
 */
public abstract class AReq<RESPONSE_TYPE> implements Op<RESPONSE_TYPE> {
    public final ReactorBase targetReactor;

    public AReq(final Reactor _targetReactor) {
        targetReactor = (ReactorBase) _targetReactor;
    }

    /**
     * The processAsyncRequest method will be invoked by the target Reactor on its own thread.
     *
     * @param _asyncRequest           The request context--may be of a different RESPONSE_TYPE.
     * @param _asyncResponseProcessor Handles the response.
     */
    abstract protected void processAsyncRequest(final AsyncRequest _asyncRequest,
                                                final AsyncResponseProcessor<RESPONSE_TYPE> _asyncResponseProcessor)
            throws Exception;

    @Override
    public void signal() {
        AsyncRequest<RESPONSE_TYPE> asyncRequest = new AsyncRequest<RESPONSE_TYPE>(targetReactor) {
            @Override
            public void processAsyncRequest() throws Exception {
                AReq.this.processAsyncRequest(this, this);
            }
        };
        asyncRequest.signal();
    }

    @Override
    public RESPONSE_TYPE call() throws Exception {
        AsyncRequest<RESPONSE_TYPE> asyncRequest = new AsyncRequest<RESPONSE_TYPE>(targetReactor) {
            @Override
            public void processAsyncRequest() throws Exception {
                AReq.this.processAsyncRequest(this, this);
            }
        };
        return asyncRequest.call();
    }
}
