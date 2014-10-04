package org.agilewiki.jactor2.core.blades.transmutable.tssmTransactions;

import org.agilewiki.jactor2.core.blades.transmutable.transactions.SyncTransaction;
import org.agilewiki.jactor2.core.blades.transmutable.transactions.Transaction;
import org.agilewiki.jactor2.core.blades.transmutable.transactions.TransmutableReference;
import org.agilewiki.jactor2.core.requests.AOp;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;

import java.util.SortedMap;

/**
 * A TSSMap transaction.
 */
abstract public class TSSMTransaction<VALUE>
        extends SyncTransaction<SortedMap<String, VALUE>, TSSMap<VALUE>> {

    protected TSSMChangeManager<VALUE> tssmChangeManager;

    /**
     * Create a Transaction.
     */
    TSSMTransaction() {
        super(null);
    }

    /**
     * Compose a Transaction.
     *
     * @param _parent The transaction to be applied before this one, or null.
     */
    TSSMTransaction(final TSSMTransaction<VALUE> _parent) {
        super(_parent);
    }

    public TSSMChangeManager<VALUE> getTSSMChangeManager() {
        return tssmChangeManager;
    }

    @Override
    public AOp<Void> applyAOp(
            final TransmutableReference<SortedMap<String, VALUE>, TSSMap<VALUE>> _transmutableReference) {
        return new AOp<Void>("apply", _transmutableReference.getReactor()) {

            private TSSMChanges<VALUE> tssmChanges;

            @Override
            protected void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl,
                                                 final AsyncResponseProcessor<Void> _asyncResponseProcessor)
                    throws Exception {
                final TSSMReference<VALUE> tssmReference = (TSSMReference<VALUE>) _transmutableReference;

                final AsyncResponseProcessor<Void> validationResponseProcessor = new AsyncResponseProcessor<Void>() {
                    @Override
                    public void processAsyncResponse(final Void _response)
                            throws Exception {
                        tssmChangeManager.close();
                        _asyncRequestImpl.send(tssmReference.changeBus
                                        .sendsContentAOp(tssmChanges),
                                _asyncResponseProcessor, transmutable);
                    }
                };

                final AsyncResponseProcessor<Void> superResponseProcessor =
                        new AsyncResponseProcessor<Void>() {
                            @Override
                            public void processAsyncResponse(final Void _response)
                                    throws Exception {
                                tssmChanges = new TSSMChanges<VALUE>(
                                        tssmChangeManager);
                                _asyncRequestImpl.send(tssmReference.validationBus
                                                .sendsContentAOp(tssmChanges),
                                        validationResponseProcessor);
                            }
                        };

                eval(_transmutableReference, _asyncRequestImpl, superResponseProcessor);
            }
        };
    }

    @Override
    protected void applySourceReference() {
        super.applySourceReference();
        tssmChangeManager = new TSSMChangeManager<VALUE>(transmutable);
    }

    @Override
    protected void applySourceTransaction(
            final Transaction<SortedMap<String, VALUE>, TSSMap<VALUE>> _transaction) {
        super.applySourceTransaction(_transaction);
        @SuppressWarnings("unchecked")
        final TSSMTransaction<VALUE> tssmTransaction = (TSSMTransaction<VALUE>) _transaction;
        tssmChangeManager = tssmTransaction.getTSSMChangeManager();
    }
}
