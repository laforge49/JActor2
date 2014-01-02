package org.agilewiki.jactor2.core.requests;

import org.agilewiki.jactor2.core.impl.MessageSource;
import org.agilewiki.jactor2.core.impl.ReactorImpl;
import org.agilewiki.jactor2.core.impl.SyncRequestImpl;
import org.agilewiki.jactor2.core.reactors.Reactor;

abstract public class SyncRequest<RESPONSE_TYPE> implements Request<RESPONSE_TYPE> {

    /**
     * Process the request immediately.
     *
     * @param _source         The targetReactor on whose thread this method was invoked and which
     *                        must be the same as the targetReactor of the target.
     * @param _syncRequest    The request to be processed.
     * @param <RESPONSE_TYPE> The type of value returned.
     * @return The value returned by the target blades.
     */
    public static <RESPONSE_TYPE> RESPONSE_TYPE doLocal(final ReactorImpl _source,
            final SyncRequest<RESPONSE_TYPE> _syncRequest) throws Exception {
        return _syncRequest.asRequestImpl().doLocal(_source);
    }

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

    public Reactor getSourceReactor() {
        MessageSource messageSource = asRequestImpl().getMessageSource();
        if (messageSource instanceof Reactor)
            return (Reactor) messageSource;
        return null;
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

    @Override
    public <RT> RT local(final SyncRequest<RT> _syncRequest)
            throws Exception {
        return syncRequestImpl.local(_syncRequest);
    }
}
