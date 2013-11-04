package org.agilewiki.jactor2.core.messages;

import org.agilewiki.jactor2.core.reactors.Reactor;

/**
 * Test code.
 */
public class Blade2 {
    private final Reactor reactor;

    public Blade2(final Reactor mbox) {
        this.reactor = mbox;
    }

    abstract public class AsyncBladeRequest<RESPONSE_TYPE> extends
            AsyncRequest<RESPONSE_TYPE> {

        /**
         * Create a SyncRequest.
         */
        public AsyncBladeRequest() {
            super(Blade2.this.reactor);
        }
    }

    /**
     * Process the request immediately.
     *
     * @param _request        The request to be processed.
     * @param <RESPONSE_TYPE> The type of value returned.
     */
    protected <RESPONSE_TYPE> void send(
            final RequestBase<RESPONSE_TYPE> _request,
            final AsyncResponseProcessor<RESPONSE_TYPE> _responseProcessor)
            throws Exception {
        RequestBase.doSend(reactor, _request, _responseProcessor);
    }

    public AsyncRequest<String> hi2AReq(final Blade1 blade1) {
        return new AsyncBladeRequest<String>() {
            @Override
            protected void processAsyncRequest() throws Exception {
                send(blade1.hiSReq(), this);
            }
        };
    }
}
