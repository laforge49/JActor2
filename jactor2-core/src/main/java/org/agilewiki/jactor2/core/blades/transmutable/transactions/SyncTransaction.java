package org.agilewiki.jactor2.core.blades.transmutable.transactions;

import org.agilewiki.jactor2.core.blades.transmutable.Transmutable;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;

/**
 * A synchronous operation to be applied to a TransmutableReference.
 */
abstract public class SyncTransaction<DATATYPE, TRANSMUTABLE extends Transmutable<DATATYPE>>
        extends Transaction<DATATYPE, TRANSMUTABLE> {
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
    SyncTransaction(Transaction<DATATYPE, TRANSMUTABLE> _parent) {
        super(_parent);
    }

    /**
     * Updates the immutable data structure.
     *
     * @param source The Transaction or TransmutableReference holding the immutable to be operated on.
     */
    abstract protected void update(TransmutableSource<DATATYPE, TRANSMUTABLE> source)
            throws Exception;

    @Override
    protected void _apply(TransmutableSource<DATATYPE, TRANSMUTABLE> _source, AsyncResponseProcessor<Void> _dis)
            throws Exception {
        if (_source instanceof Transaction) {
            final Transaction<DATATYPE, TRANSMUTABLE> transaction = (Transaction<DATATYPE, TRANSMUTABLE>) _source;
            applySourceTransaction(transaction);
        } else {
            applySourceReference((TransmutableReference<DATATYPE, TRANSMUTABLE>) _source);
        }
        updateTrace();
        getReactor().asReactorImpl().setExceptionHandler(exceptionHandler());
        if (!precheck(_source.getTransmutable())) {
            transmutable = null;
            _dis.processAsyncResponse(null);
            return;
        }
        update(_source);
        _dis.processAsyncResponse(null);
    }
}
