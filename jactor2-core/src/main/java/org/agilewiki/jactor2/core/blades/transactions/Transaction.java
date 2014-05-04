package org.agilewiki.jactor2.core.blades.transactions;

import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.ExceptionHandler;

public class Transaction<IMMUTABLE> extends ImmutableReference<IMMUTABLE> {
    private final Transaction<IMMUTABLE> parent;
    private final SyncUpdate<IMMUTABLE> syncUpdate;
    private final AsyncUpdate<IMMUTABLE> asyncUpdate;
    public String trace;

    public Transaction(final SyncUpdate _syncUpdate) {
        super();
        parent = null;
        syncUpdate = _syncUpdate;
        asyncUpdate = null;
    }

    public Transaction(final AsyncUpdate _asyncUpdate) {
        super();
        parent = null;
        syncUpdate = null;
        asyncUpdate = _asyncUpdate;
    }

    public Transaction(final Transaction<IMMUTABLE> _parent, final SyncUpdate _syncUpdate) {
        super(null);
        parent = _parent;
        syncUpdate = _syncUpdate;
        asyncUpdate = null;
    }

    public Transaction(final Transaction<IMMUTABLE> _parent, final AsyncUpdate _asyncUpdate) {
        super(null);
        parent = _parent;
        syncUpdate = null;
        asyncUpdate = _asyncUpdate;
    }

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

    private ExceptionHandler<IMMUTABLE> exceptionHandler() {
        return new ExceptionHandler() {
            /**
             * Process an exception or rethrow it.
             *
             * @param e The exception to be processed.
             */
            @Override
            public Object processException(Exception e) throws Exception {
                System.err.println(trace);
                if (e instanceof RuntimeException)
                    e = new RuntimeWrapperException((RuntimeException) e);
                throw e;
            }
        };
    }

    protected void _apply(final ImmutableReference<IMMUTABLE> _source,
                          final String oldTrace,
                          final AsyncResponseProcessor<Void> _dis)
            throws Exception {
        if (asyncUpdate != null) {
            trace = "TRACE: " + asyncUpdate.getClass().getName() + oldTrace;
            getReactor().asReactorImpl().setExceptionHandler(exceptionHandler());
            asyncUpdate.update(_source, Transaction.this, new AsyncResponseProcessor<IMMUTABLE>() {
                @Override
                public void processAsyncResponse(IMMUTABLE _response) throws Exception {
                    immutable = _response;
                }
            });
        } else if (syncUpdate != null) {
            trace = "TRACE: " + syncUpdate.getClass().getName() + oldTrace;
            getReactor().asReactorImpl().setExceptionHandler(exceptionHandler());
            immutable = syncUpdate.update(_source, Transaction.this);
            _dis.processAsyncResponse(null);
        } else {
            throw new IllegalStateException();
        }
    }

    protected void _eval(final ImmutableReference<IMMUTABLE> _root, final AsyncResponseProcessor<Void> _dis)
            throws Exception {
        reactor = _root.reactor;
        if (parent == null)
            _apply(_root, "", _dis);
        else
            parent._eval(_root, new AsyncResponseProcessor<Void>() {
                @Override
                public void processAsyncResponse(Void _response) throws Exception {
                    _apply(parent, "\n" + parent.trace, _dis);
                }
            });
    }
}
