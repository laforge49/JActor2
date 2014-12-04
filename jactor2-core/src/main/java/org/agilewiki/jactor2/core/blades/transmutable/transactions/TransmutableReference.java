package org.agilewiki.jactor2.core.blades.transmutable.transactions;

import org.agilewiki.jactor2.core.blades.IsolationBlade;
import org.agilewiki.jactor2.core.blades.transmutable.Transmutable;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.requests.AOp;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;

/**
 * An IsolationBlade to which transactions can be applied.
 */
public class TransmutableReference<DATATYPE, TRANSMUTABLE extends Transmutable<DATATYPE>>
        extends TransmutableSource<DATATYPE, TRANSMUTABLE>
        implements IsolationBlade {

    /**
     * The blade's reactor.
     */
    protected IsolationReactor reactor;

    /**
     * The transmutable to be operated on.
     */
    private TRANSMUTABLE transmutable;

    volatile private DATATYPE unmodifiable;

    /**
     * Create an ImmutableReference blade.
     *
     * @param _transmutable The transmutable data structure to be operated on.
     */
    public TransmutableReference(final TRANSMUTABLE _transmutable) throws Exception {
        reactor = new IsolationReactor();
        transmutable = _transmutable;
        unmodifiable = transmutable.createUnmodifiable();
    }

    /**
     * Create an ImmutableReference blade.
     *
     * @param _transmutable The transmutable data structure to be operated on.
     * @param _reactor      The blade's reactor.
     */
    public TransmutableReference(final TRANSMUTABLE _transmutable,
                                 final IsolationReactor _reactor) {
        reactor = _reactor;
        transmutable = _transmutable;
        unmodifiable = transmutable.createUnmodifiable();
    }

    @Override
    public IsolationReactor getReactor() {
        return reactor;
    }

    @Override
    protected TRANSMUTABLE getTransmutable() {
        return transmutable;
    }

    public DATATYPE getUnmodifiable() {
        return unmodifiable;
    }

    protected void updateUnmodifiable() {
        unmodifiable = transmutable.createUnmodifiable();
    }

    protected void recreate() {
        transmutable = (TRANSMUTABLE) transmutable.recreate(unmodifiable);
    }

    /**
     * Creates a request to apply the transaction to a transmutable reference.
     *
     * @param _transaction The transaction to be applied.
     * @return The request.
     */
    public AOp<Void> applyAOp(
            final Transaction<DATATYPE, TRANSMUTABLE> _transaction) {
        return new AOp<Void>("apply", getReactor()) {
            @Override
            protected void processAsyncOperation(
                    final AsyncRequestImpl _asyncRequestImpl,
                    final AsyncResponseProcessor<Void> _asyncResponseProcessor)
                    throws Exception {
                _transaction._eval(TransmutableReference.this, _asyncRequestImpl,
                        new AsyncResponseProcessor<Void>() {
                            @Override
                            public void processAsyncResponse(Void _response) throws Exception {
                                updateUnmodifiable();
                                _asyncResponseProcessor.processAsyncResponse(null);
                            }
                        });
            }
        };
    }

    /**
     * Evaluate the transaction.
     *
     * @param _transaction          The transaction to be evalueated.
     * @param request                The request.
     * @param dis                    The async response processor.
     */
    protected void eval(
            final Transaction<DATATYPE, TRANSMUTABLE> _transaction,
            final AsyncRequestImpl<TRANSMUTABLE> request,
            final AsyncResponseProcessor<Void> dis) throws Exception {
        _transaction._eval(this,
                request,
                dis);
    }
}
