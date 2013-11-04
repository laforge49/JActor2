package org.agilewiki.jactor2.core.blades.transactions;

import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

abstract public class NewValidatorAReq<STATE, STATE_WRAPPER extends AutoCloseable, IMMUTABLE_CHANGES extends ImmutableChanges, IMMUTABLE_STATE>
        extends
        AsyncRequest<ValidationSubscription<STATE, STATE_WRAPPER, IMMUTABLE_CHANGES, IMMUTABLE_STATE>> {
    private final AsyncRequest<ValidationSubscription<STATE, STATE_WRAPPER, IMMUTABLE_CHANGES, IMMUTABLE_STATE>> disReq = this;
    private final TransactionProcessor<STATE, STATE_WRAPPER, IMMUTABLE_CHANGES, IMMUTABLE_STATE> transactionProcessor;
    private final String prefix;

    public NewValidatorAReq(
            final NonBlockingReactor _targetReactor,
            final TransactionProcessor<STATE, STATE_WRAPPER, IMMUTABLE_CHANGES, IMMUTABLE_STATE> _transactionProcessor,
            final String _prefix) {
        super(_targetReactor);
        transactionProcessor = _transactionProcessor;
        prefix = _prefix;
    }

    abstract protected void validateChange(IMMUTABLE_CHANGES _immutableChanges,
            AsyncResponseProcessor<Void> rp) throws Exception;

    @Override
    protected void processAsyncRequest() throws Exception {
        final Validator<IMMUTABLE_CHANGES> valTran = new Validator<IMMUTABLE_CHANGES>() {
            @Override
            public String getPrefix() {
                return prefix;
            }

            @Override
            public AsyncRequest<Void> validateAReq(
                    final IMMUTABLE_CHANGES _immutableChanges) {
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
