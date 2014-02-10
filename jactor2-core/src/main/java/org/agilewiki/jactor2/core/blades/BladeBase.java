package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.Request;
import org.agilewiki.jactor2.core.requests.SyncRequest;

/**
 * Optional base class for blades.
 */
public abstract class BladeBase implements Blade {
    /**
     * The blade's targetReactor.
     */
    private Reactor reactor;

    /**
     * True when initialized, this flag is used to prevent the targetReactor from being changed.
     */
    private boolean initialized;

    /**
     * Returns true when the blade has been initialized.
     *
     * @return True when the blade has been initialized.
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Initialize a blade. This method can only be called once
     * without raising an illegal state exception, as the targetReactor
     * can not be changed.
     *
     * @param _reactor The blade's targetReactor.
     */
    protected void _initialize(final Reactor _reactor) {
        if (initialized) {
            throw new IllegalStateException("Already initialized " + this);
        }
        if (_reactor == null) {
            throw new IllegalArgumentException("Reactor may not be null");
        }
        initialized = true;
        reactor = _reactor;
    }

    @Override
    public Reactor getReactor() {
        return reactor;
    }

    /**
     * An AsyncRequest targeted to this blade.
     *
     * @param <RESPONSE_TYPE> The type of response value.
     */
    protected abstract class AsyncBladeRequest<RESPONSE_TYPE> extends
            AsyncRequest<RESPONSE_TYPE> {

        /**
         * Create an AyncRequest targetd to this blade.
         */
        public AsyncBladeRequest() {
            super(BladeBase.this.reactor);
        }
    }

    /**
     * Pass a request to the blade's reactor.
     *
     * @param _request        The request to be passed.
     */
    protected <RESPONSE_TYPE> void send(
            final Request<RESPONSE_TYPE> _request)
            throws Exception {
        _request.asRequestImpl().doSend(getReactor().asReactorImpl(), null);
    }

    /**
     * A SyncRequest targeted to this blade.
     * @param <RESPONSE_TYPE>
     */
    protected abstract class SyncBladeRequest<RESPONSE_TYPE> extends
            SyncRequest<RESPONSE_TYPE> {

        /**
         * Create a SyncRequest targeted to this blade.
         */
        public SyncBladeRequest() {
            super(BladeBase.this.reactor);
        }
    }

    /**
     * Validate that the source reactor is the same as the target and that the source reactor is active.
     *
     * @param _sourceReactor    The source reactor.
     */
    protected void directCheck(final Reactor _sourceReactor) {
        if (reactor != _sourceReactor)
            throw new UnsupportedOperationException("Not thread safe: source reactor is not the same");
        if (!reactor.asReactorImpl().isRunning())
            throw new IllegalStateException("Not thread safe: not called from within an active request");
    }
}
