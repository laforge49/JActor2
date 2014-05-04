package org.agilewiki.jactor2.core.blades.transactions;

import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;

public class Transaction<IMMUTABLE> extends ImmutableReference<IMMUTABLE> {
    private final Transaction<IMMUTABLE> parent;
    private final SyncUpdate<IMMUTABLE> syncUpdate;
    private final AsyncUpdate<IMMUTABLE> asyncUpdate;

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

    protected void _apply(final ImmutableReference<IMMUTABLE> _source, final AsyncResponseProcessor<Void> _dis)
            throws Exception {
        if (asyncUpdate != null) {
            asyncUpdate.update(_source, Transaction.this, new AsyncResponseProcessor<IMMUTABLE>() {
                @Override
                public void processAsyncResponse(IMMUTABLE _response) throws Exception {
                    immutable = _response;
                    _dis.processAsyncResponse(null);
                }
            });
        } else if (syncUpdate != null) {
            immutable = syncUpdate.update(_source, Transaction.this);
            _dis.processAsyncResponse(null);
        } else {
            throw new IllegalStateException();
        }
    }

    protected void _eval(final ImmutableReference<IMMUTABLE> _root, final AsyncResponseProcessor<Void> _dis)
            throws Exception {
        if (parent == null)
            _apply(_root, _dis);
        else
            parent._eval(_root, new AsyncResponseProcessor<Void>() {
                @Override
                public void processAsyncResponse(Void _response) throws Exception {
                    _apply(parent, _dis);
                }
            });
    }
}
