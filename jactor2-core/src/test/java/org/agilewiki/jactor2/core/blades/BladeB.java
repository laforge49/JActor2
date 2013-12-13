package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.RequestBase;
import org.agilewiki.jactor2.core.reactors.Reactor;

public class BladeB {
    private final Reactor reactor;

    public BladeB(final Reactor mbox) {
        this.reactor = mbox;
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

    public AsyncRequest<Void> throwRequest(final BladeA bladeA) {
        return new AsyncRequest<Void>(reactor) {
            @Override
            protected void processAsyncRequest() throws Exception {
                send(bladeA.throwRequest, this);
            }
        };
    }
}
