package org.agilewiki.jactor2.core.blades.transactions;

import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.ExceptionHandler;

/**
 * A composable transation for updating an ImmutableReference.
 *
 * @param <IMMUTABLE>    The type of immutable data structure.
 */
abstract public class Transaction<IMMUTABLE> extends ImmutableReference<IMMUTABLE> {
    private final Transaction<IMMUTABLE> parent;
    protected StringBuffer trace;

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
            AsyncRequest<Void> dis = this;

            private AsyncResponseProcessor<Void> _evalResponseProcessor = new AsyncResponseProcessor<Void>() {
                @Override
                public void processAsyncResponse(Void _response) throws Exception {
                    _immutableReference.immutable = immutable;
                    dis.processAsyncResponse(null);
                }
            };

            @Override
            public void processAsyncRequest() throws Exception {
                _eval(_immutableReference, _evalResponseProcessor);
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

    private void _eval(final ImmutableReference<IMMUTABLE> _root, final AsyncResponseProcessor<Void> _dis)
            throws Exception {
        reactor = _root.reactor;
        if (parent == null) {
            trace = new StringBuffer("");
            _apply(_root, _dis);
        } else {
            trace = parent.trace;
            parent._eval(_root, new AsyncResponseProcessor<Void>() {
                @Override
                public void processAsyncResponse(Void _response) throws Exception {
                    _apply(parent, _dis);
                }
            });
        }
    }
}
