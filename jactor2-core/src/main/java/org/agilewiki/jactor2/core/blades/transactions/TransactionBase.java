package org.agilewiki.jactor2.core.blades.transactions;

import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.requests.AOp;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.ExceptionHandler;
import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;

/**
 * A composable transaction for updating an ImmutableReference.
 * Transactions are serially reusable, but not thread safe.
 *
 * @param <IMMUTABLE> The type of immutable data structure.
 */
abstract public class TransactionBase<IMMUTABLE> implements
        Transaction<IMMUTABLE> {

    /**
     * The blade's reactor.
     */
    protected IsolationReactor reactor;

    private final Transaction<IMMUTABLE> parent;

    /**
     * The immutable data structure to be operated on.
     */
    protected IMMUTABLE immutable;

    /**
     * Holds the trace in reverse chronological order.
     */
    protected StringBuffer trace;

    /**
     * The request which updates operate under.
     */
    protected AsyncRequestImpl<IMMUTABLE> applyAReq;

    /**
     * Compose a Transaction.
     *
     * @param _parent The transaction to be applied before this one.
     */
    TransactionBase(final Transaction<IMMUTABLE> _parent) {
        parent = _parent;
    }

    @Override
    public IsolationReactor getReactor() {
        return reactor;
    }

    @Override
    public IMMUTABLE getImmutable() {
        return immutable;
    }

    /**
     * Updates the reference in the ImmutableReference.
     *
     * @param _immutableReference The ImmutableReference to which the transaction is applied.
     */
    protected void updateImmutableReference(
            final ImmutableReference<IMMUTABLE> _immutableReference) {
        _immutableReference.immutable = immutable;
    }

    protected AsyncResponseProcessor<Void> evalResponseProcessor(
            final ImmutableReference<IMMUTABLE> _immutableReference,
            final AsyncResponseProcessor<IMMUTABLE> dis) {
        return new AsyncResponseProcessor<Void>() {
            @Override
            public void processAsyncResponse(final Void _response)
                    throws Exception {
                updateImmutableReference(_immutableReference);
                dis.processAsyncResponse(immutable);
                applyAReq = null;
            }
        };
    }

    protected void eval(
            final ImmutableReference<IMMUTABLE> _immutableReference,
            final AsyncRequestImpl<IMMUTABLE> request,
            final AsyncResponseProcessor<IMMUTABLE> dis) throws Exception {
        _eval(_immutableReference,
                request,
                evalResponseProcessor(_immutableReference,
                        new AsyncResponseProcessor<IMMUTABLE>() {
                            @Override
                            public void processAsyncResponse(
                                    final IMMUTABLE _response) throws Exception {
                                evalResponseProcessor(_immutableReference, dis)
                                        .processAsyncResponse(null);
                            }
                        }));
    }

    /**
     * Create a request to apply the transaction to an ImmutableReference.
     *
     * @param _immutableReference The ImmutableReference to which the transaction is to be applied.
     * @return The new request.
     */
    public AOp<IMMUTABLE> applyAOp(
            final ImmutableReference<IMMUTABLE> _immutableReference) {
        return new AOp<IMMUTABLE>("apply", _immutableReference.getReactor()) {
            @Override
            public void processAsyncOperation(AsyncRequestImpl _asyncRequestImpl,
                                              AsyncResponseProcessor<IMMUTABLE> _asyncResponseProcessor)
                    throws Exception {
                eval(_immutableReference, _asyncRequestImpl, _asyncResponseProcessor);
            }
        };
    }

    /**
     * Create a request to evaluate the transaction against an ImmutableReference without changing the ImmutableReference.
     *
     * @param _immutableReference The ImmutableReference to which the transaction is to be applied.
     * @return The new request.
     */
    public AOp<IMMUTABLE> evalAOp(
            final ImmutableReference<IMMUTABLE> _immutableReference) {
        return new AOp<IMMUTABLE>("eval", _immutableReference.getReactor()) {
            @Override
            public void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl,
                                              final AsyncResponseProcessor<IMMUTABLE> _asyncResponseProcessor)
                    throws Exception {
                AsyncResponseProcessor<Void> _evalResponseProcessor = new AsyncResponseProcessor<Void>() {
                    @Override
                    public void processAsyncResponse(final Void _response)
                            throws Exception {
                        _asyncResponseProcessor.processAsyncResponse(immutable);
                        applyAReq = null;
                    }
                };

                _eval(_immutableReference, _asyncRequestImpl, _evalResponseProcessor);
            }
        };
    }

    protected ExceptionHandler<IMMUTABLE> exceptionHandler() {
        return new ExceptionHandler<IMMUTABLE>() {
            /**
             * Process an exception or rethrow it.
             *
             * @param e The exception to be processed.
             */
            @Override
            public IMMUTABLE processException(final Exception e)
                    throws Exception {
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
    abstract protected void _apply(final ImmutableSource<IMMUTABLE> _source,
            final AsyncResponseProcessor<Void> _dis) throws Exception;

    /**
     * Update applied with a source of ImmutableReference.
     */
    protected void applySourceReference(
            final ImmutableReference<IMMUTABLE> _reference) {
        trace = new StringBuffer("");
    }

    /**
     * Update applied with a source of another transaction.
     *
     * @param _transaction The other transaction.
     */
    protected void applySourceTransaction(
            final TransactionBase<IMMUTABLE> _transaction) {
        trace = _transaction.trace;
    }

    @Override
    public void _eval(final ImmutableReference<IMMUTABLE> _root,
            final AsyncRequestImpl<IMMUTABLE> _applyAReq,
            final AsyncResponseProcessor<Void> _dis) throws Exception {
        reactor = _root.reactor;
        applyAReq = _applyAReq;
        if (parent == null) {
            if (_root.getImmutable() == null) {
                immutable = null;
                _dis.processAsyncResponse(null);
            } else {
                _apply(_root, _dis);
            }
        } else {
            parent._eval(_root, applyAReq, new AsyncResponseProcessor<Void>() {
                @Override
                public void processAsyncResponse(final Void _response)
                        throws Exception {
                    if (parent.getImmutable() == null) {
                        immutable = null;
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
     * @param _immutable    The immutable prior to the update being applied.
     */
    protected boolean precheck(final IMMUTABLE _immutable) throws Exception {
        return true;
    }
}
