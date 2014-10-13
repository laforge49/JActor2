package org.agilewiki.jactor2.core.blades.transmutable.transactions;

import org.agilewiki.jactor2.core.blades.IsolationBlade;
import org.agilewiki.jactor2.core.blades.transmutable.Transmutable;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.requests.AOp;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.ExceptionHandler;
import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;

/**
 * A composable transaction for updating a TransmutableReference.
 * Transactions are serially reusable, but not thread safe.
 */
public abstract class Transaction<DATATYPE, TRANSMUTABLE extends Transmutable<DATATYPE>>
        extends TransmutableSource<DATATYPE, TRANSMUTABLE>
        implements IsolationBlade {

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
     * @param _parent The transaction to be applied before this one, or null.
     */
    public Transaction(final Transaction<DATATYPE, TRANSMUTABLE> _parent) {
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
                applyAReq = null;
                dis.processAsyncResponse(null);
            }
        };
    }

    protected ExceptionHandler<Void> exceptionHandler(
            final TransmutableReference<DATATYPE, TRANSMUTABLE> _transmutableReference) {
        return new ExceptionHandler<Void>() {
            /**
             * Process an exception or rethrow it.
             *
             * @param e The exception to be processed.
             */
            @Override
            public Void processException(final Exception e)
                    throws Exception {
                _transmutableReference.recreate();
                getReactor().error(trace.toString());
                throw e;
            }
        };
    }

    /**
     * Apply the update.
     *
     * @param _source The source transaction or immutable reference.
     * @param _dis    Signals completion of the update.
     */
    abstract protected void _apply(final TransmutableSource<DATATYPE, TRANSMUTABLE> _source,
                                   final AsyncResponseProcessor<Void> _dis) throws Exception;

    /**
     * Update applied with a source of ImmutableReference.
     */
    protected void applySourceReference() {
        trace = new StringBuffer("");
    }

    /**
     * Update applied with a source of another transaction.
     *
     * @param _transaction The other transaction.
     */
    protected void applySourceTransaction(
            final Transaction<DATATYPE, TRANSMUTABLE> _transaction) {
        trace = _transaction.trace;
    }

    /**
     * Evaluate the transaction.
     *
     * @param _root      The root of the transaction chain, a transmutable reference.
     * @param _applyAReq The request to apply.
     * @param _dis       The response processor.
     */
    public void _eval(TransmutableReference<DATATYPE, TRANSMUTABLE> _root,
                      AsyncRequestImpl<TRANSMUTABLE> _applyAReq,
                      final AsyncResponseProcessor<Void> _dis) throws Exception {
        reactor = _root.reactor;
        getReactor().asReactorImpl().setExceptionHandler(exceptionHandler(_root));
        applyAReq = _applyAReq;
        if (parent == null) {
            transmutable = _root.getTransmutable();
            if (transmutable == null) {
                _dis.processAsyncResponse(null);
            } else {
                _apply(_root, _dis);
            }
        } else {
            parent._eval(_root, applyAReq, new AsyncResponseProcessor<Void>() {
                @Override
                public void processAsyncResponse(final Void _response)
                        throws Exception {
                    transmutable = parent.getTransmutable();
                    if (transmutable == null) {
                        _dis.processAsyncResponse(null);
                    } else {
                        _apply(parent, _dis);
                    }
                }
            });
        }
    }

    /**
     * Update the trace.
     */
    protected void updateTrace() {
        trace.insert(0, "\nTRACE: " + getClass().getName());
    }

    /**
     * Returns true if the precheck passes.
     *
     * @param _transmutable The transmutable prior to the update being applied.
     */
    protected boolean precheck(final TRANSMUTABLE _transmutable) throws Exception {
        return true;
    }
}
