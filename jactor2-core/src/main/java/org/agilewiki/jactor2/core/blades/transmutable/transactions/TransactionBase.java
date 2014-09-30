package org.agilewiki.jactor2.core.blades.transmutable.transactions;

import org.agilewiki.jactor2.core.blades.transmutable.Transmutable;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;

/**
 * A composable transaction for updating a TransmutableReference.
 * Transactions are serially reusable, but not thread safe.
 */
public abstract class TransactionBase<DATATYPE, TRANSMUTABLE extends Transmutable<DATATYPE>>
        implements Transaction<DATATYPE, TRANSMUTABLE> {

    /**
     * The blade's reactor.
     */
    protected IsolationReactor reactor;

    /**
     * The parent transaction in the chain of transactions.
     */
    private final Transaction<DATATYPE, TRANSMUTABLE> parent;

    /**
     * The transmutable data structure to be operated on.
     */
    protected TRANSMUTABLE transmutable;

    /**
     * Holds the trace in reverse chronological order.
     */
    protected StringBuffer trace;

    /**
     * The request which updates operate under.
     */
    protected AsyncRequestImpl<TRANSMUTABLE> applyAReq;

    /**
     * Compose a Transaction.
     *
     * @param _parent The transaction to be applied before this one.
     */
    TransactionBase(final Transaction<DATATYPE, TRANSMUTABLE> _parent) {
        parent = _parent;
    }

    @Override
    public IsolationReactor getReactor() {
        return reactor;
    }

    @Override
    public TRANSMUTABLE getTransmutable() {
        return transmutable;
    }

    @Override
    public void _eval(TransmutableReference<DATATYPE, TRANSMUTABLE> _root, AsyncRequestImpl<TRANSMUTABLE> _applyAReq, AsyncResponseProcessor<Void> _dis) throws Exception {

    }
}
