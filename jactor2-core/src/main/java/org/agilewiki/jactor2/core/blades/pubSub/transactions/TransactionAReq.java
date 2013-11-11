package org.agilewiki.jactor2.core.blades.pubSub.transactions;

import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.reactors.CommonReactor;

/**
 * <p>
 * An AsyncRequest for processing transactions.
 * </p>
 * <p>
 * One of the update methods must be overridden or the transaction will throw an UnimplementedOperationException.
 * </p>
 *
 * @param <CHANGE_MANAGER>    Used when processing a transaction to update the state.
 * @param <IMMUTABLE_STATE>   The type of state.
 * @param <IMMUTABLE_CHANGES> The transaction changes passed to the subscribers of the validation and
 *                            change RequestBus instances.
 */
abstract public class TransactionAReq<CHANGE_MANAGER extends AutoCloseable, IMMUTABLE_STATE, IMMUTABLE_CHANGES>
        extends AsyncRequest<Void> {
    private final CommonReactor updateReactor;
    private final TransactionProcessor<CHANGE_MANAGER, IMMUTABLE_STATE, IMMUTABLE_CHANGES> transactionProcessor;
    final AsyncResponseProcessor<Void> dis = this;
    private IMMUTABLE_CHANGES immutableChanges;

    /**
     * Create a transaction request.
     *
     * @param _updateReactor        The reactor to be used when updating the change manager.
     * @param _transactionProcessor The transaction processor to be used.
     */
    protected TransactionAReq(final CommonReactor _updateReactor,
                              final TransactionProcessor<CHANGE_MANAGER, IMMUTABLE_STATE, IMMUTABLE_CHANGES> _transactionProcessor) {
        super(_transactionProcessor.getReactor());
        updateReactor = _updateReactor;
        transactionProcessor = _transactionProcessor;
    }

    /**
     * Updates the change manager.
     * By default, this method simply throws an UnsupportedOperationException.
     *
     * @param _contentManager The change manager to be updated by the transaction.
     * @throws UnsupportedOperationException Thrown by default.
     */
    protected void update(final CHANGE_MANAGER _contentManager) throws Exception {
        throw new UnsupportedOperationException(
                "The processContent method was not overridden.");
    }

    /**
     * Updates the change manager.
     * By default, this method simply calls the update(CHANGE_MANAGER) method.
     *
     * @param _contentManager         The change manager to be updated by the transaction.
     * @param _asyncResponseProcessor The response processor to be used when the change is complete.
     * @throws UnsupportedOperationException Thrown by default.
     */
    protected void update(final CHANGE_MANAGER _contentManager,
                          final AsyncResponseProcessor<Void> _asyncResponseProcessor)
            throws Exception {
        update(_contentManager);
        _asyncResponseProcessor.processAsyncResponse(null);
    }

    @Override
    final protected void processAsyncRequest() throws Exception {

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
