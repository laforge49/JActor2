package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.SyncRequest;

public class BladeMonad<Immutable> extends BladeBase {
    private Immutable immutable = null;

    public Immutable getImmutable(final Reactor _sourceReactor) {
        directCheck(_sourceReactor);
        return immutable;
    }

    public BladeMonad<Immutable> bind(final MonadicFunction<Immutable> mf) {
        return mf.f(this);
    }

    protected void transform(final Immutable _immutable) {
        immutable = _immutable;
    }

    public SyncRequest<Immutable> evalSReq(final BladeMonad<Immutable> _bladeMonad) {
        return new SyncBladeRequest<Immutable>() {
            @Override
            public Immutable processSyncRequest() throws Exception {
                transform(_bladeMonad.getImmutable(getSourceReactor()));
                return immutable;
            }
        };
    }

    public AsyncRequest<Immutable> evalAReq(final BladeMonad<Immutable> _bladeMonad) {
        return new AsyncBladeRequest<Immutable>() {
            AsyncRequest<Immutable> dis = this;

            private AsyncResponseProcessor<Immutable> evalResponseProcessor = new AsyncResponseProcessor<Immutable>() {
                @Override
                public void processAsyncResponse(Immutable _response) throws Exception {
                    transform(_response);
                    dis.processAsyncResponse(immutable);
                }
            };

            @Override
            public void processAsyncRequest() throws Exception {
                send(_bladeMonad.evalAReq(BladeMonad.this), evalResponseProcessor);
            }
        };
    }
}
