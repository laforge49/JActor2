package org.agilewiki.jactor2.core.blades.transmutable.transactions;

import org.agilewiki.jactor2.core.blades.transmutable.Transmutable;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.requests.AOp;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;

/**
 * A composable transaction for updating a TransmutableReference.
 * Transactions are serially reusable, but not thread safe.
 */
public abstract class TransactionBase<DATATYPE, TRANSMUTABLE extends Transmutable<DATATYPE>>
        extends TransmutableSource<DATATYPE, TRANSMUTABLE>
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
    protected TRANSMUTABLE getTransmutable() {
        return transmutable;
    }

    protected AsyncResponseProcessor<Void> evalResponseProcessor(
            final TransmutableReference<DATATYPE, TRANSMUTABLE> _transmutableReference,
            final AsyncResponseProcessor<Void> dis) {
        return new AsyncResponseProcessor<Void>() {
            @Override
            public void processAsyncResponse(final Void _response)
                    throws Exception {
                _transmutableReference.updateUnmodifiable();
                applyAReq = null;
                dis.processAsyncResponse(null);
            }
        };
    }

    /**
     * Evaluate the transaction.
     *
     * @param _transmutableReference    The transmutable reference.
     * @param request                   The request.
     * @param dis                       The async response processor.
     */
    protected void eval(
            final TransmutableReference<DATATYPE, TRANSMUTABLE> _transmutableReference,
            final AsyncRequestImpl<TRANSMUTABLE> request,
            final AsyncResponseProcessor<Void> dis) throws Exception {
        _eval(_transmutableReference,
                request,
                evalResponseProcessor(_transmutableReference,
                        dis));
    }

    /**
     * Creates a request to apply the transaction to a transmutable reference.
     *
     * @param _transmutableReference    The transmutable reference.
     * @return The request.
     */
    public AOp<Void> applyAOp(
            final TransmutableReference<DATATYPE, TRANSMUTABLE> _transmutableReference) {
        return new AOp<Void>("apply", _transmutableReference.getReactor()) {
            @Override
            protected void processAsyncOperation(
                    final AsyncRequestImpl _asyncRequestImpl,
                    final AsyncResponseProcessor<Void> _asyncResponseProcessor)
                    throws Exception {
                eval(_transmutableReference, _asyncRequestImpl,
                        _asyncResponseProcessor);
            }
        };
    }

    @Override
    public void _eval(TransmutableReference<DATATYPE, TRANSMUTABLE> _root,
                      AsyncRequestImpl<TRANSMUTABLE> _applyAReq,
                      AsyncResponseProcessor<Void> _dis) throws Exception {

    }
}
