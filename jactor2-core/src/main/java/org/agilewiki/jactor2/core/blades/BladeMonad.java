package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.SyncRequest;

public class BladeMonad<Immutable> extends BladeBase {
    private Immutable immutable = null;

    public Immutable eval(final Reactor _sourceReactor) {
        directCheck(_sourceReactor);
        return immutable;
    }

    public BladeMonad<Immutable> bind(final MonadicFunction<Immutable> mf) {
        return mf.f(this);
    }

    public SyncRequest<Immutable> evalSReq(final BladeMonad<Immutable> _bladeMonad) {
        return new SyncBladeRequest<Immutable>() {
            @Override
            public Immutable processSyncRequest() throws Exception {
                return _bladeMonad.eval(getSourceReactor());
            }
        };
    }

    public AsyncRequest<BladeMonad<Immutable>> evalAReq(final BladeMonad<Immutable> _bladeMonad) {
        return new AsyncBladeRequest<BladeMonad<Immutable>>() {
            @Override
            public void processAsyncRequest() throws Exception {
                processAsyncResponse(BladeMonad.this);
            }
        };
    }
}
