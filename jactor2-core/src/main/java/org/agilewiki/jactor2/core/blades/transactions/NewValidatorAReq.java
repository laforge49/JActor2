package org.agilewiki.jactor2.core.blades.transactions;

import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

abstract public class NewValidatorAReq<STATE, STATE_WRAPPER extends AutoCloseable, IMMUTABLE_CHANGES, IMMUTABLE_STATE>
        extends AsyncRequest<ValidationSubscription<IMMUTABLE_CHANGES>> {
    private AsyncRequest<ValidationSubscription<IMMUTABLE_CHANGES>> disReq = this;
    private final TransactionProcessor<STATE, STATE_WRAPPER, IMMUTABLE_CHANGES, IMMUTABLE_STATE> transactionProcessor;

    public NewValidatorAReq(
            final NonBlockingReactor _targetReactor,
            final TransactionProcessor<STATE, STATE_WRAPPER, IMMUTABLE_CHANGES, IMMUTABLE_STATE>
                    _transactionProcessor) {
        super(_targetReactor);
        transactionProcessor = _transactionProcessor;
    }

    abstract protected void validateChange(IMMUTABLE_CHANGES _immutableChanges, AsyncResponseProcessor<Void> rp)
            throws Exception;

    @Override
    protected void processAsyncRequest() throws Exception {
        Validator<IMMUTABLE_CHANGES> valTran = new Validator<IMMUTABLE_CHANGES>() {
            @Override
            public AsyncRequest<Void> validateAReq(final IMMUTABLE_CHANGES _immutableChanges) {
                return new AsyncRequest<Void>(disReq.getTargetReactor()) {
                    @Override
                    protected void processAsyncRequest() throws Exception {
                        validateChange(_immutableChanges, this);
                    }
                };
            }

            @Override
            public Reactor getReactor() {
                return disReq.getTargetReactor();
            }
        };
        send(transactionProcessor.addValidatorAReq(valTran), this);
    }
}
