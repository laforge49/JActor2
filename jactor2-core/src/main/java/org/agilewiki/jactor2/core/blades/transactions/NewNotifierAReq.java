package org.agilewiki.jactor2.core.blades.transactions;

import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

abstract public class NewNotifierAReq<STATE, STATE_WRAPPER extends AutoCloseable, IMMUTABLE_CHANGES, IMMUTABLE_STATE>
        extends AsyncRequest<ChangeSubscription<IMMUTABLE_CHANGES>> {
    private AsyncRequest<ChangeSubscription<IMMUTABLE_CHANGES>> disReq = this;
    private final TransactionProcessor<STATE, STATE_WRAPPER, IMMUTABLE_CHANGES, IMMUTABLE_STATE> transactionProcessor;

    public NewNotifierAReq(
            final NonBlockingReactor _targetReactor,
            final TransactionProcessor<STATE, STATE_WRAPPER, IMMUTABLE_CHANGES, IMMUTABLE_STATE>
                    _transactionProcessor) {
        super(_targetReactor);
        transactionProcessor = _transactionProcessor;
    }

    abstract protected String notifyChange(IMMUTABLE_CHANGES _immutableChanges, AsyncResponseProcessor<Void> rp)
            throws Exception;

    @Override
    protected void processAsyncRequest() throws Exception {
        ChangeNotificationSubscriber<IMMUTABLE_CHANGES> noteTran =
                new ChangeNotificationSubscriber<IMMUTABLE_CHANGES>() {
                    @Override
                    public AsyncRequest<Void> changeNotificationAReq(final IMMUTABLE_CHANGES _immutableChanges) {
                        return new AsyncRequest<Void>(disReq.getTargetReactor()) {
                            @Override
                            protected void processAsyncRequest() throws Exception {
                                notifyChange(_immutableChanges, this);
                            }
                        };
                    }

                    @Override
                    public Reactor getReactor() {
                        return disReq.getTargetReactor();
                    }
                };
        send(transactionProcessor.addChangeNotificationSubscriberAReq(noteTran), this);
    }
}
