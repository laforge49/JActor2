package org.agilewiki.jactor2.core.blades.ismTransactions;

import org.agilewiki.jactor2.core.blades.transactions.ISMap;
import org.agilewiki.jactor2.core.blades.transactions.ImmutableReference;
import org.agilewiki.jactor2.core.blades.transactions.SyncTransaction;
import org.agilewiki.jactor2.core.blades.transactions.TransactionBase;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;

/**
 * Base class for isMap sync transactions.
 */
abstract public class ISMSyncTransaction<VALUE> extends
        SyncTransaction<ISMap<VALUE>> implements ISMSource<VALUE>,
        ISMTransaction<VALUE> {

    protected ImmutableChangeManager<VALUE> immutableChangeManager;

    private ImmutableChanges<VALUE> immutableChanges;

    /**
     * Create a PropertyTransaction.
     */
    public ISMSyncTransaction() {
        super(null);
    }

    /**
     * Compose a Transaction.
     *
     * @param _parent The PropertyTransaction to be applied before this one.
     */
    public ISMSyncTransaction(final ISMTransaction<VALUE> _parent) {
        super(_parent);
    }

    @Override
    public ImmutableChangeManager<VALUE> getImmutableChangeManager() {
        return immutableChangeManager;
    }

    @Override
    protected void updateImmutableReference(
            final ImmutableReference<ISMap<VALUE>> _immutableReference) {
    }

    @Override
    public AsyncRequest<ISMap<VALUE>> applyAReq(
            final ImmutableReference<ISMap<VALUE>> _immutableReference) {
        return new AsyncRequest<ISMap<VALUE>>(_immutableReference.getReactor()) {
            private final AsyncResponseProcessor<ISMap<VALUE>> dis = this;

            private final ISMReference<VALUE> ismReference = (ISMReference<VALUE>) _immutableReference;

            private final AsyncResponseProcessor<Void> validationResponseProcessor = new AsyncResponseProcessor<Void>() {
                @Override
                public void processAsyncResponse(final Void _response)
                        throws Exception {
                    ISMSyncTransaction.super
                            .updateImmutableReference(_immutableReference);
                    immutableChangeManager.close();
                    send(ismReference.changeBus
                            .sendsContentAOp(immutableChanges),
                            dis, immutable);
                }
            };

            private final AsyncResponseProcessor<ISMap<VALUE>> superResponseProcessor = new AsyncResponseProcessor<ISMap<VALUE>>() {
                @Override
                public void processAsyncResponse(final ISMap<VALUE> _response)
                        throws Exception {
                    immutableChanges = new ImmutableChanges<VALUE>(
                            immutableChangeManager);
                    send(ismReference.validationBus
                            .sendsContentAOp(immutableChanges),
                            validationResponseProcessor);
                }
            };

            @Override
            public void processAsyncRequest() throws Exception {
                immutableChangeManager = new ImmutableChangeManager<VALUE>(
                        ismReference.getImmutable());
                eval(_immutableReference, this, superResponseProcessor);
            }
        };
    }

    @Override
    protected void applySourceReference(
            final ImmutableReference<ISMap<VALUE>> _immutableReference) {
        super.applySourceReference(_immutableReference);
        immutableChangeManager = new ImmutableChangeManager<VALUE>(
                _immutableReference.getImmutable());
    }

    @Override
    protected void applySourceTransaction(
            final TransactionBase<ISMap<VALUE>> _transaction) {
        super.applySourceTransaction(_transaction);
        @SuppressWarnings("unchecked")
        final ISMTransaction<VALUE> ismTransaction = (ISMTransaction<VALUE>) _transaction;
        immutableChangeManager = ismTransaction.getImmutableChangeManager();
    }
}
