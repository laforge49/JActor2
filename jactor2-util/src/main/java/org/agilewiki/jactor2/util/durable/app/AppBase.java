package org.agilewiki.jactor2.util.durable.app;

import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.util.Ancestor;

/**
 * An optional base class for serializable application classes.
 */
public class AppBase implements App {

    /**
     * The durable part of the serializable object.
     */
    private Durable durable;

    @Override
    public void setDurable(final Durable _durable) {
        if (durable != null)
            throw new IllegalStateException("durable already set");
        durable = _durable;
    }

    @Override
    public Durable getDurable() {
        return durable;
    }

    @Override
    public Reactor getReactor() {
        return durable.getReactor();
    }

    @Override
    public Ancestor getParent() {
        return durable.getParent();
    }

    abstract public class SyncBladeRequest<RESPONSE_TYPE> extends SyncRequest<RESPONSE_TYPE> {

        /**
         * Create a SyncRequest.
         */
        public SyncBladeRequest() {
            super(getReactor());
        }
    }

    abstract public class AsyncBladeRequest<RESPONSE_TYPE> extends AsyncRequest<RESPONSE_TYPE> {

        /**
         * Create a SyncRequest.
         */
        public AsyncBladeRequest() {
            super(getReactor());
        }
    }

    /**
     * Process the request immediately.
     *
     * @param _syncRequest    The request to be processed.
     * @param <RESPONSE_TYPE> The type of value returned.
     * @return The response from the request.
     */
    protected <RESPONSE_TYPE> RESPONSE_TYPE local(final SyncRequest<RESPONSE_TYPE> _syncRequest) throws Exception {
        return SyncRequest.doLocal(getReactor(), _syncRequest);
    }
}
