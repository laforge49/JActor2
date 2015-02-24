package org.agilewiki.jactor2.core.impl.mtMessages;

import org.agilewiki.jactor2.core.messages.alt.SyncNativeRequest;
import org.agilewiki.jactor2.core.messages.SyncOperation;
import org.agilewiki.jactor2.core.messages.impl.RequestImpl;
import org.agilewiki.jactor2.core.plant.impl.MetricsTimer;
import org.agilewiki.jactor2.core.reactors.Reactor;

/**
 * Internal implementation of a SyncRequest.
 *
 * @param <RESPONSE_TYPE> The response value type.
 */
public class SyncRequestMtImpl<RESPONSE_TYPE> extends
        RequestMtImpl<RESPONSE_TYPE> implements SyncNativeRequest<RESPONSE_TYPE> {

    private final SyncOperation<RESPONSE_TYPE> syncOperation;

    /**
     * Create a SyncRequestMtImpl and bind it to its operation and target reactor.
     *
     * @param _syncOperation The request being implemented.
     * @param _targetReactor The target reactor.
     */
    public SyncRequestMtImpl(final SyncOperation<RESPONSE_TYPE> _syncOperation,
                             final Reactor _targetReactor) {
        super(_targetReactor);
        syncOperation = _syncOperation;
    }

    public SyncRequestMtImpl(final Reactor _targetReactor) {
        super(_targetReactor);
        syncOperation = this;
    }

    @Override
    public String getOpName() {
        return asOperation().getOpName();
    }

    @Override
    public SyncOperation<RESPONSE_TYPE> asOperation() {
        return syncOperation;
    }

    @Override
    protected void processRequestMessage() throws Exception {
        final MetricsTimer timer = targetReactor.getMetricsTimer("SOp."+getOpName());
        final long start = timer.nanos();
        boolean success = false;
        final RESPONSE_TYPE result;
        try {
            result = syncOperation.doSync(this);
            success = true;
        } finally {
            timer.updateNanos(timer.nanos() - start, success);
        }

        processObjectResponse(result);
    }

    @Override
    public RESPONSE_TYPE doSync(final RequestImpl _requestImpl) throws Exception {
        if (!_requestImpl.getTargetReactor().asReactorImpl().isRunning())
            throw new IllegalStateException(
                    "Not thread safe: not called from within an active request");
        return processSyncOperation(_requestImpl);
    }

    protected RESPONSE_TYPE processSyncOperation(final RequestImpl _requestImpl) throws Exception {
        throw new IllegalStateException();
    }
}
