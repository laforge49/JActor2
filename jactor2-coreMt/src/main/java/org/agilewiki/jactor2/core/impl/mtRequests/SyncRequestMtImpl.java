package org.agilewiki.jactor2.core.impl.mtRequests;

import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.requests.SyncRequest;
import org.agilewiki.jactor2.core.util.Timer;

/**
 * Internal implementation of a SyncRequest.
 *
 * @param <RESPONSE_TYPE> The response value type.
 */
public class SyncRequestMtImpl<RESPONSE_TYPE> extends
        RequestMtImpl<RESPONSE_TYPE> {

    private final SyncRequest<RESPONSE_TYPE> syncRequest;

    /**
     * Create a SyncRequestMtImpl and bind it to its target reactor.
     *
     * @param _syncRequest   The request being implemented.
     * @param _targetReactor The target reactor.
     */
    public SyncRequestMtImpl(final SyncRequest<RESPONSE_TYPE> _syncRequest,
            final Reactor _targetReactor) {
        super(_targetReactor);
        syncRequest = _syncRequest;
    }

    @Override
    public SyncRequest<RESPONSE_TYPE> asRequest() {
        return syncRequest;
    }

    @Override
    protected void processRequestMessage() throws Exception {
        final Timer timer = syncRequest.getTimer();
        final long start = timer.nanos();
        boolean success = false;
        final RESPONSE_TYPE result;
        try {
            result = syncRequest.processSyncOperation(this);
            success = true;
        } finally {
            timer.updateNanos(timer.nanos() - start, success);
        }

        processObjectResponse(result);
    }
}
