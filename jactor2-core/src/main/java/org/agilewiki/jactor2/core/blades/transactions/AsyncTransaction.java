package org.agilewiki.jactor2.core.blades.transactions;

import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;

/**
 * An asynchronous operation to be applied to an ImmutableReference.
 * The new immutable reference resulting from the update should be stored
 * in this.immutable.
 *
 * @param <IMMUTABLE> The type of immutable data structure.
 */
abstract public class AsyncTransaction<IMMUTABLE> extends Transaction<IMMUTABLE> {
    /**
     * Create a Transaction.
     */
    public AsyncTransaction() {
        super(null);
    }

    /**
     * Compose a Transaction.
     *
     * @param _parent The transaction to be applied before this one.
     */
    public AsyncTransaction(Transaction<IMMUTABLE> _parent) {
        super(_parent);
    }

    /**
     * Updates the immutable data structure.
     *
     * @param source                 The Transaction or ImmutableReference holding the immutable to be operated on.
     * @param asyncResponseProcessor Updates the immutable in the target transaction.
     */
    abstract protected void update(ImmutableSource<IMMUTABLE> source,
                         AsyncResponseProcessor<Void> asyncResponseProcessor) throws Exception;

    /**
     * Apply the update.
     *
     * @param _source The source transaction or immutable reference.
     * @param _dis    Signals completion of the update.
     */
    protected void _apply(final ImmutableSource<IMMUTABLE> _source,
                          final AsyncResponseProcessor<Void> _dis)
            throws Exception {
        if (_source instanceof Transaction) {
            Transaction<IMMUTABLE> transaction = (Transaction<IMMUTABLE>) _source;
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
        update(_source, new AsyncResponseProcessor<Void>() {
            @Override
            public void processAsyncResponse(Void _response) throws Exception {
                _dis.processAsyncResponse(null);
            }
        });
    }
}
