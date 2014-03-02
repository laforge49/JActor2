package org.agilewiki.jactor2.core.requests;

import org.agilewiki.jactor2.core.reactors.impl.ReactorImpl;
import org.agilewiki.jactor2.core.requests.impl.RequestSource;
import org.agilewiki.jactor2.core.requests.impl.SyncRequestImpl;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.reactors.ReactorClosedException;

/**
 * A sync request performs an operation safely within the thread context of the target reactor.
 *
 * @param <RESPONSE_TYPE> The type of response value.
 */
abstract public class SyncRequest<RESPONSE_TYPE> implements Request<RESPONSE_TYPE> {

    private final SyncRequestImpl<RESPONSE_TYPE> syncRequestImpl;

    /**
     * Create a SyncRequest.
     *
     * @param _targetReactor The targetReactor where this SyncRequest object is passed for processing.
     *                       The thread owned by this targetReactor will process this SyncRequest.
     */
    public SyncRequest(final Reactor _targetReactor) {
        syncRequestImpl = new SyncRequestImpl<RESPONSE_TYPE> (this, _targetReactor);
    }

    /**
     * The processSyncRequest method will be invoked by the target Reactor on its own thread.
     *
     * @return The value returned by the target blades.
     */
    abstract public RESPONSE_TYPE processSyncRequest() throws Exception;

    @Override
    public SyncRequestImpl<RESPONSE_TYPE> asRequestImpl() {
        return syncRequestImpl;
    }

    @Override
    public Reactor getTargetReactor() {
        return syncRequestImpl.getTargetReactor();
    }

    @Override
    public Reactor getSourceReactor() {
        RequestSource requestSource = asRequestImpl().getRequestSource();
        if (requestSource instanceof ReactorImpl)
            return ((ReactorImpl) requestSource).asReactor();
        return null;
    }

    @Override
    public void signal() {
        syncRequestImpl.signal();
    }

    @Override
    public RESPONSE_TYPE call() throws Exception {
        return syncRequestImpl.call();
    }

    @Override
    public boolean isCanceled() throws ReactorClosedException {
        return syncRequestImpl.isCanceled();
    }
}
