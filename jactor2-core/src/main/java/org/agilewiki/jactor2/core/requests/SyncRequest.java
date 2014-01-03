package org.agilewiki.jactor2.core.requests;

import org.agilewiki.jactor2.core.impl.MessageSource;
import org.agilewiki.jactor2.core.impl.ReactorImpl;
import org.agilewiki.jactor2.core.impl.SyncRequestImpl;
import org.agilewiki.jactor2.core.reactors.Reactor;

abstract public class SyncRequest<RESPONSE_TYPE> implements Request<RESPONSE_TYPE> {

    private final SyncRequestImpl<RESPONSE_TYPE> syncRequestImpl;

    /**
     * Create a SyncRequest.
     *
     * @param _targetReactor The targetReactor where this SyncRequest Objects is passed for processing.
     *                       The thread owned by this targetReactor will process this SyncRequest.
     */
    public SyncRequest(final Reactor _targetReactor) {
        syncRequestImpl = new SyncRequestImpl<RESPONSE_TYPE> (this, _targetReactor);
    }

    /**
     * The processSyncRequest method will be invoked by the target Reactor on its own thread
     * when the SyncRequest is dequeued from the target inbox for processing.
     *
     * @return The value returned by the target blades.
     */
    abstract public RESPONSE_TYPE processSyncRequest() throws Exception;

    public SyncRequestImpl<RESPONSE_TYPE> asRequestImpl() {
        return syncRequestImpl;
    }

    @Override
    public Reactor getTargetReactor() {
        return syncRequestImpl.getTargetReactor();
    }

    @Override
    public void signal() throws Exception {
        syncRequestImpl.signal();
    }

    @Override
    public RESPONSE_TYPE call() throws Exception {
        return syncRequestImpl.call();
    }
}
