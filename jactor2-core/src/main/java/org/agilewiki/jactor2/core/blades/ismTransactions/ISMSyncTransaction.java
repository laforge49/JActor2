package org.agilewiki.jactor2.core.blades.ismTransactions;

import org.agilewiki.jactor2.core.blades.transactions.ImmutableReference;
import org.agilewiki.jactor2.core.blades.transactions.SyncTransaction;
import org.agilewiki.jactor2.core.blades.transactions.TransactionBase;
import org.agilewiki.jactor2.core.requests.AOp;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;

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
    public AOp<ISMap<VALUE>> applyAOp(
            final ImmutableReference<ISMap<VALUE>> _immutableReference) {
        return new AOp<ISMap<VALUE>>("apply", _immutableReference.getReactor()) {
            @Override
            protected void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl,
                                              final AsyncResponseProcessor<ISMap<VALUE>> _asyncResponseProcessor)
                    throws Exception {
                final ISMReference<VALUE> ismReference = (ISMReference<VALUE>) _immutableReference;

                final AsyncResponseProcessor<Void> validationResponseProcessor = new AsyncResponseProcessor<Void>() {
                    @Override
                    public void processAsyncResponse(final Void _response)
                            throws Exception {
                        ISMSyncTransaction.super
                                .updateImmutableReference(_immutableReference);
                        immutableChangeManager.close();
                        _asyncRequestImpl.send(ismReference.changeBus
                                        .sendsContentAOp(immutableChanges),
                                _asyncResponseProcessor, immutable);
                    }
                };

                final AsyncResponseProcessor<ISMap<VALUE>> superResponseProcessor =
                        new AsyncResponseProcessor<ISMap<VALUE>>() {
                    @Override
                    public void processAsyncResponse(final ISMap<VALUE> _response)
                            throws Exception {
                        immutableChanges = new ImmutableChanges<VALUE>(
                                immutableChangeManager);
                        _asyncRequestImpl.send(ismReference.validationBus
                                        .sendsContentAOp(immutableChanges),
                                validationResponseProcessor);
                    }
                };

                eval(_immutableReference, _asyncRequestImpl, superResponseProcessor);
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
