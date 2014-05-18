package org.agilewiki.jactor2.core.blades.transactions;

import org.agilewiki.jactor2.core.blades.IsolationBlade;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.ExceptionHandler;

/**
 * A composable transation for updating an ImmutableReference.
 * Transactions are serially reusable, but not thread safe.
 *
 * @param <IMMUTABLE> The type of immutable data structure.
 */
abstract public class Transaction<IMMUTABLE> implements IsolationBlade, ImmutableSource<IMMUTABLE> {

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
    protected AsyncRequest<IMMUTABLE> applyAReq;

    /**
     * Compose a Transaction.
     *
     * @param _parent The transaction to be applied before this one.
     */
    Transaction(final Transaction<IMMUTABLE> _parent) {
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
    protected void updateImmutableReference(final ImmutableReference<IMMUTABLE> _immutableReference) {
        _immutableReference.immutable = immutable;
    }

    protected AsyncResponseProcessor<Void> evalResponseProcessor(final ImmutableReference<IMMUTABLE> _immutableReference,
                                                                 final AsyncResponseProcessor<IMMUTABLE> dis) {
        return new AsyncResponseProcessor<Void>() {
            @Override
            public void processAsyncResponse(Void _response) throws Exception {
                updateImmutableReference(_immutableReference);
                dis.processAsyncResponse(immutable);
                applyAReq = null;
            }
        };
    }

    protected void eval(final ImmutableReference<IMMUTABLE> _immutableReference,
                        final AsyncRequest<IMMUTABLE> request,
                        final AsyncResponseProcessor<IMMUTABLE> dis)
            throws Exception {
        _eval(_immutableReference, request, evalResponseProcessor(_immutableReference, new AsyncResponseProcessor<IMMUTABLE>() {
            @Override
            public void processAsyncResponse(IMMUTABLE _response) throws Exception {
                evalResponseProcessor(_immutableReference, dis).processAsyncResponse(null);
            }
        }));
    }

    /**
     * Create a request to apply the transaction to an ImmutableReference.
     *
     * @param _immutableReference The ImmutableReference to which the transaction is to be applied.
     * @return The new request.
     */
    public AsyncRequest<IMMUTABLE> applyAReq(final ImmutableReference<IMMUTABLE> _immutableReference) {
        return new AsyncRequest<IMMUTABLE>(_immutableReference.getReactor()) {
            @Override
            public void processAsyncRequest() throws Exception {
                eval(_immutableReference, this, this);
            }
        };
    }

    /**
     * Create a request to evaluate the transaction against an ImmutableReference without changing the ImmutableReference.
     *
     * @param _immutableReference The ImmutableReference to which the transaction is to be applied.
     * @return The new request.
     */
    public AsyncRequest<IMMUTABLE> evalAReq(final ImmutableReference<IMMUTABLE> _immutableReference) {
        return new AsyncRequest<IMMUTABLE>(_immutableReference.getReactor()) {
            private AsyncResponseProcessor<Void> _evalResponseProcessor = new AsyncResponseProcessor<Void>() {
                @Override
                public void processAsyncResponse(Void _response) throws Exception {
                    applyAReq.processAsyncResponse(immutable);
                    applyAReq = null;
                }
            };

            @Override
            public void processAsyncRequest() throws Exception {
                _eval(_immutableReference, this, _evalResponseProcessor);
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
            public IMMUTABLE processException(Exception e) throws Exception {
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
                                   final AsyncResponseProcessor<Void> _dis)
            throws Exception;

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
    protected void applySourceTransaction(final Transaction _transaction) {
        trace = _transaction.trace;
    }

    private void _eval(final ImmutableReference<IMMUTABLE> _root,
                       final AsyncRequest<IMMUTABLE> _applyAReq,
                       final AsyncResponseProcessor<Void> _dis)
            throws Exception {
        reactor = _root.reactor;
        applyAReq = _applyAReq;
        if (parent == null) {
            precheck(_root.immutable);
            _apply(_root, _dis);
        } else {
            parent._eval(_root, applyAReq, new AsyncResponseProcessor<Void>() {
                @Override
                public void processAsyncResponse(Void _response) throws Exception {
                    precheck(parent.immutable);
                    _apply(parent, _dis);
                }
            });
        }
    }

    /**
     * Throws an exception if an unexpected value is encountered.
     *
     * @param _immutable    The immutable prior to the update being applied.
     */
    protected void precheck(final IMMUTABLE _immutable) throws Exception {
    }
}
