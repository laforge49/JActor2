package org.agilewiki.jactor2.core.blades.transactions;

import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

abstract public class TransactionAReq<STATE, STATE_WRAPPER extends AutoCloseable, IMMUTABLE_CHANGES extends ImmutableChanges, IMMUTABLE_STATE>
        extends AsyncRequest<Void> {
    private final AsyncRequest<Void> disReq = this;
    private final TransactionProcessor<STATE, STATE_WRAPPER, IMMUTABLE_CHANGES, IMMUTABLE_STATE> transactionProcessor;

    public TransactionAReq(
            final NonBlockingReactor _targetReactor,
            final TransactionProcessor<STATE, STATE_WRAPPER, IMMUTABLE_CHANGES, IMMUTABLE_STATE> _transactionProcessor) {
        super(_targetReactor);
        transactionProcessor = _transactionProcessor;
    }

    abstract protected void evalTransaction(STATE_WRAPPER _stateWrapper,
            AsyncResponseProcessor<Void> rp) throws Exception;

    @Override
    protected void processAsyncRequest() throws Exception {
        final Transaction<STATE_WRAPPER> putTran = new Transaction<STATE_WRAPPER>() {
            @Override
            public AsyncRequest<Void> updateAReq(
                    final STATE_WRAPPER _stateWrapper) {
                return new AsyncRequest<Void>(disReq.getTargetReactor()) {
                    @Override
                    protected void processAsyncRequest() throws Exception {
                        evalTransaction(_stateWrapper, this);
                    }
                };
            }
        };
        send(transactionProcessor.processTransactionAReq(putTran), this);
    }
}
