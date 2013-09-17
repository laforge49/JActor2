package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.RequestBase;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.reactors.Reactor;

/**
 * <p>
 * BladeBase is a convenience class that implements an Blade. Initialization is not
 * thread-safe, so it should be done before a reference to the blade is shared.
 * </p>
 * <h3>Sample Usage:</h3>
 * <pre>
 * public class BladeBaseSample extends BladeBase {
 *     public BladeBaseSample(final Reactor _messageProcessor) throws Exception {
 *         initialize(_messageProcessor);
 *     }
 * }
 * </pre>
 */
public class BladeBase implements Blade {
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
    public void initialize(final Reactor _reactor) throws Exception {
        if (initialized)
            throw new IllegalStateException("Already initialized");
        if (_reactor == null)
            throw new IllegalArgumentException("Reactor may not be null");
        initialized = true;
        reactor = _reactor;
    }

    @Override
    public Reactor getReactor() {
        return reactor;
    }

    abstract public class SyncBladeRequest<RESPONSE_TYPE> extends SyncRequest<RESPONSE_TYPE> {

        /**
         * Create a SyncRequest.
         */
        public SyncBladeRequest() {
            super(BladeBase.this.reactor);
        }
    }

    abstract public class AsyncBladeRequest<RESPONSE_TYPE> extends AsyncRequest<RESPONSE_TYPE> {

        /**
         * Create a SyncRequest.
         */
        public AsyncBladeRequest() {
            super(BladeBase.this.reactor);
        }
    }

    /**
     * Process the request immediately.
     *
     * @param _syncRequest    The request to be processed.
     * @param <RESPONSE_TYPE> The type of value returned.
     * @return The response from the request.
     */
    protected <RESPONSE_TYPE> RESPONSE_TYPE local(final SyncRequest<RESPONSE_TYPE> _syncRequest)
            throws Exception {
        return SyncRequest.doLocal(reactor, _syncRequest);
    }

    /**
     * Process the request immediately.
     *
     * @param _request    The request to be processed.
     * @param <RESPONSE_TYPE> The type of value returned.
     */
    protected <RESPONSE_TYPE> void send(final RequestBase<RESPONSE_TYPE> _request,
                                        final AsyncResponseProcessor<RESPONSE_TYPE> _responseProcessor)
            throws Exception {
        RequestBase.doSend(reactor, _request, _responseProcessor);
    }
}
