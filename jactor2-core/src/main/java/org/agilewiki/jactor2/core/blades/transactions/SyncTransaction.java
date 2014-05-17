package org.agilewiki.jactor2.core.blades.transactions;

import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;

/**
 * A synchronous operation to be applied to an ImmutableReference.
 * The new immutable reference resulting from the update should be stored
 * in this.immutable.
 *
 * @param <IMMUTABLE> The type of immutable data structure.
 */
abstract public class SyncTransaction<IMMUTABLE> extends Transaction<IMMUTABLE> {
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
    public SyncTransaction(Transaction<IMMUTABLE> _parent) {
        super(_parent);
    }

    /**
     * Updates the immutable data structure.
     *
     * @param source The Transaction or ImmutableReference holding the immutable to be operated on.
     */
    abstract protected void update(ImmutableSource<IMMUTABLE> source) throws Exception;

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
            trace = transaction.trace;
        } else {
            trace = new StringBuffer("");
        }
        trace.insert(0, "\nTRACE: " + getClass().getName());
        getReactor().asReactorImpl().setExceptionHandler(exceptionHandler());
        update(_source);
        _dis.processAsyncResponse(null);
    }
}
