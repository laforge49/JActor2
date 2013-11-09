package org.agilewiki.jactor2.core.blades.pubSub.transactions;

import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.reactors.CommonReactor;

public class TransactionAReq<CHANGE_MANAGER extends AutoCloseable, IMMUTABLE_STATE, IMMUTABLE_CHANGES>
        extends AsyncRequest<Void> {
    private final CommonReactor updateReactor;
    private final TransactionProcessor<CHANGE_MANAGER, IMMUTABLE_STATE, IMMUTABLE_CHANGES> transactionProcessor;
    final AsyncResponseProcessor<Void> dis = this;
    private IMMUTABLE_CHANGES immutableChanges;

    public TransactionAReq(final CommonReactor _updateReactor,
                           final TransactionProcessor<CHANGE_MANAGER, IMMUTABLE_STATE, IMMUTABLE_CHANGES> _transactionProcessor) {
        super(_transactionProcessor.getReactor());
        updateReactor = _updateReactor;
        transactionProcessor = _transactionProcessor;
    }

    protected void update(final CHANGE_MANAGER _contentManager) throws Exception {
        throw new UnsupportedOperationException(
                "The processContent method was not overridden.");
    }

    protected void update(final CHANGE_MANAGER _contentManager,
                          final AsyncResponseProcessor<Void> _asyncResponseProcessor)
            throws Exception {
        update(_contentManager);
        _asyncResponseProcessor.processAsyncResponse(null);
    }

    @Override
    protected void processAsyncRequest() throws Exception {

        final CHANGE_MANAGER changeManager = transactionProcessor.newChangeManager();

        final AsyncResponseProcessor<Void> validatorsResponseProcessor = new AsyncResponseProcessor<Void>() {
            @Override
            public void processAsyncResponse(Void _response) throws Exception {
                transactionProcessor.newImmutableState();
                send(transactionProcessor.changeBus.signalsContentSReq(immutableChanges), dis);
            }
        };

        final AsyncResponseProcessor<Void> updateResponseProcessor = new AsyncResponseProcessor<Void>() {
            @Override
            public void processAsyncResponse(Void _response) throws Exception {
                changeManager.close();
                immutableChanges = transactionProcessor.newChanges();
                send(transactionProcessor.validationBus.sendsContentAReq(immutableChanges),
                        validatorsResponseProcessor);
            }
        };

        final AsyncRequest<Void> updateAReq = new AsyncRequest<Void>(updateReactor) {
            @Override
            protected void processAsyncRequest() throws Exception {
                update(changeManager, this);
            }
        };

        send(updateAReq, updateResponseProcessor);
    }
}
