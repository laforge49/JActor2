package org.agilewiki.jactor2.core.impl;

import org.agilewiki.jactor2.core.blades.ExceptionHandler;
import org.agilewiki.jactor2.core.plant.PoolThread;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.requests.Request;
import org.agilewiki.jactor2.core.requests.SyncRequest;

public class SyncRequestImpl<RESPONSE_TYPE> extends
        RequestImplBase<RESPONSE_TYPE> {

    private final SyncRequest<RESPONSE_TYPE> syncRequest;

    public SyncRequestImpl(final SyncRequest _syncRequest, final Reactor _targetReactor) {
        super(_targetReactor);
        syncRequest = _syncRequest;
    }

    public Request asRequest() {
        return syncRequest;
    }

    @Override
    protected void processRequestMessage() throws Exception {
        processObjectResponse(syncRequest.processSyncRequest());
    }
}
