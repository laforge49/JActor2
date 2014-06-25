package org.agilewiki.jactor2.core.requests;

import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.reactors.ReactorBase;
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

    /**
     * The processAsyncOperation method will be invoked by the target Reactor on its own thread.
     *
     * @param _asyncRequest           The request context--may be of a different RESPONSE_TYPE.
     * @param _asyncResponseProcessor Handles the response.
     */
    abstract protected void processAsyncOperation(final AsyncRequest _asyncRequest,
                                                final AsyncResponseProcessor<RESPONSE_TYPE> _asyncResponseProcessor)
            throws Exception;

    @Override
    public void signal() {
        AsyncRequest<RESPONSE_TYPE> asyncRequest = new AsyncRequest<RESPONSE_TYPE>(targetReactor) {
            @Override
            public void processAsyncRequest() throws Exception {
                AOp.this.processAsyncOperation(this, this);
            }

            @Override
            public String toString() {
                return AOp.this.toString();
            }
        };
        asyncRequest.signal();
    }

    @GwtIncompatible
    @Override
    public RESPONSE_TYPE call() throws Exception {
        AsyncRequest<RESPONSE_TYPE> asyncRequest = new AsyncRequest<RESPONSE_TYPE>(targetReactor) {
            @Override
            public void processAsyncRequest() throws Exception {
                AOp.this.processAsyncOperation(this, this);
            }

            @Override
            public String toString() {
                return AOp.this.toString();
            }
        };
        return asyncRequest.call();
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
