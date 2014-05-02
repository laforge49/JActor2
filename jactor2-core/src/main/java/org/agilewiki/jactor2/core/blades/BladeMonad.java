package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;

public class BladeMonad<Immutable> extends BladeBase {
    private Immutable immutable = null;
    private BladeMonad<Immutable> parent;
    private BladeMonad<Immutable> root;
    protected MonadicFunction<Immutable> function;
    protected MonadicTransform<Immutable> transform;

    public BladeMonad(final Reactor _reactor) {
        _initialize(_reactor);
        root = this;
    }

    public BladeMonad(final Reactor _reactor, final Immutable _immutable) {
        _initialize(_reactor);
        immutable = _immutable;
        root = this;
    }

    public BladeMonad(final MonadicFunction<Immutable> _function, BladeMonad<Immutable> _parent) {
        _initialize(_parent.getReactor());
        function = _function;
        parent = _parent;
        root = parent.root;
    }

    public BladeMonad(final MonadicTransform<Immutable> _transform, BladeMonad<Immutable> _parent) {
        _initialize(_parent.getReactor());
        transform = _transform;
        parent = _parent;
        root = parent.root;
    }

    public Immutable getImmutable(final Reactor _sourceReactor) {
        directCheck(_sourceReactor);
        return immutable;
    }

    public BladeMonad<Immutable> bind(final MonadicFunction<Immutable> _function) {
        return new BladeMonad(_function, this);
    }

    public BladeMonad<Immutable> bind(final MonadicTransform<Immutable> _transform) {
        return new BladeMonad(_transform, this);
    }

    public AsyncRequest<Immutable> evalAReq() {
        return new AsyncBladeRequest<Immutable>() {
            AsyncRequest<Immutable> dis = this;

            private AsyncResponseProcessor<Immutable> transformResponseProcessor = new AsyncResponseProcessor<Immutable>() {
                @Override
                public void processAsyncResponse(Immutable _response) throws Exception {
                    root.immutable = _response;
                    dis.processAsyncResponse(_response);
                }
            };

            private AsyncResponseProcessor<Immutable> evalResponseProcessor = new AsyncResponseProcessor<Immutable>() {
                @Override
                public void processAsyncResponse(Immutable _response) throws Exception {
                    if (transform != null) {
                        transform.t(_response, transformResponseProcessor);
                    } else if (function != null) {
                        immutable = function.f(_response);
                        root.immutable = _response;
                        dis.processAsyncResponse(_response);
                    } else {
                        root.immutable = _response;
                        dis.processAsyncResponse(_response);
                    }
                }
            };

            @Override
            public void processAsyncRequest() throws Exception {
                if (parent == null) {
                    dis.processAsyncResponse(immutable);
                    return;
                }
                send(parent.evalAReq(), evalResponseProcessor);
            }
        };
    }
}
