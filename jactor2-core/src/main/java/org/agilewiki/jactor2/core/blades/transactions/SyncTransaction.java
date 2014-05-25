package org.agilewiki.jactor2.core.blades.transactions;

import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;

/**
 * A synchronous operation to be applied to an ImmutableReference.
 * The new immutable reference resulting from the update should be stored
 * in this.immutable.
 *
 * @param <IMMUTABLE> The type of immutable data structure.
 */
abstract public class SyncTransaction<IMMUTABLE> extends TransactionBase<IMMUTABLE> {
    /**
     * Create a Transaction.
     */
    public SyncTransaction() {
        super(null);
    }

    /**
     * Compose a Transaction.
     *
     * @param _parent The transaction to be applied before this one.
     */
    public SyncTransaction(final Transaction<IMMUTABLE> _parent) {
        super(_parent);
    }

    /**
     * Updates the immutable data structure.
     *
     * @param source The Transaction or ImmutableReference holding the immutable to be operated on.
     */
    abstract protected void update(ImmutableSource<IMMUTABLE> source)
            throws Exception;

    /**
     * Apply the update.
     *
     * @param _source The source transaction or immutable reference.
     * @param _dis    Signals completion of the update.
     */
    @Override
    protected void _apply(final ImmutableSource<IMMUTABLE> _source,
            final AsyncResponseProcessor<Void> _dis) throws Exception {
        if (_source instanceof TransactionBase) {
            final TransactionBase<IMMUTABLE> transaction = (TransactionBase<IMMUTABLE>) _source;
            applySourceTransaction(transaction);
        } else {
            applySourceReference((ImmutableReference<IMMUTABLE>) _source);
        }
        updateTrace();
        getReactor().asReactorImpl().setExceptionHandler(exceptionHandler());
        if (!precheck(_source.getImmutable())) {
            immutable = null;
            _dis.processAsyncResponse(null);
            return;
        }
        update(_source);
        _dis.processAsyncResponse(null);
    }
}
