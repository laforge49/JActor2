package org.agilewiki.jactor2.core.blades.transactions;

import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.ExceptionHandler;

/**
 * A composable transation for updating an ImmutableReference.
 * Transactions are serially reusable, but not thread safe.
 *
 * @param <IMMUTABLE>    The type of immutable data structure.
 */
abstract public class Transaction<IMMUTABLE> extends ImmutableReference<IMMUTABLE> {
    private final Transaction<IMMUTABLE> parent;

    /**
     * Holds the trace in reverse chronological order.
     */
    protected StringBuffer trace;

    /**
     * The request which updates operate under.
     */
    protected AsyncRequest<Void> applyAReq;

    /**
     * Compose a Transaction.
     *
     * @param _parent        The transaction to be applied before this one.
     */
    Transaction(final Transaction<IMMUTABLE> _parent) {
        parent = _parent;
    }

    /**
     * Create a request to apply the transaction.
     *
     * @param _immutableReference    The ImmutableReference to which the transaction is to be applied.
     * @return The new request.
     */
    public AsyncRequest<Void> applyAReq(final ImmutableReference<IMMUTABLE> _immutableReference) {
        return new AsyncRequest<Void>(_immutableReference.getReactor()) {
            private AsyncResponseProcessor<Void> _evalResponseProcessor = new AsyncResponseProcessor<Void>() {
                @Override
                public void processAsyncResponse(Void _response) throws Exception {
                    _immutableReference.immutable = immutable;
                    applyAReq.processAsyncResponse(null);
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
     * @param _source     The source transaction or immutable reference.
     * @param _dis        Signals completion of the update.
     */
    abstract protected void _apply(final ImmutableReference<IMMUTABLE> _source,
                          final AsyncResponseProcessor<Void> _dis)
            throws Exception;

    private void _eval(final ImmutableReference<IMMUTABLE> _root,
                       final AsyncRequest<Void> _applyAReq,
                       final AsyncResponseProcessor<Void> _dis)
            throws Exception {
        reactor = _root.reactor;
        applyAReq = _applyAReq;
        if (parent == null) {
            _apply(_root, _dis);
        } else {
            parent._eval(_root, applyAReq, new AsyncResponseProcessor<Void>() {
                @Override
                public void processAsyncResponse(Void _response) throws Exception {
                    _apply(parent, _dis);
                }
            });
        }
    }
}
