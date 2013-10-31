package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.RequestBase;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

public class BladeE {
    private final Reactor reactor;

    public BladeE(final Facility _facility) throws Exception {
        this.reactor = new IsolationReactor(_facility);
    }

    /**
     * Process the request immediately.
     *
     * @param _request        The request to be processed.
     * @param <RESPONSE_TYPE> The type of value returned.
     */
    protected <RESPONSE_TYPE> void send(final RequestBase<RESPONSE_TYPE> _request,
                                        final AsyncResponseProcessor<RESPONSE_TYPE> _responseProcessor)
            throws Exception {
        RequestBase.doSend(reactor, _request, _responseProcessor);
    }

    public AsyncRequest<Void> throwRequest(final BladeA bladeA) {
        return new AsyncRequest<Void>(reactor) {
            AsyncRequest<Void> dis = this;

            @Override
            protected void processAsyncRequest()
                    throws Exception {
                // Note: we only respond to responseProcessor if we get a
                // response to our own request, which should NOT happen.
                // Therefore, responseProcessor is NOT called.
                try {
                    send(bladeA.throwRequest,
                            new AsyncResponseProcessor<Void>() {

                                @Override
                                public void processAsyncResponse(final Void response)
                                        throws Exception {
                                    // Should NOT happen!
                                    dis.processAsyncException(new IllegalStateException(
                                            "We should have never got here!"));
                                }
                            });
                } catch (final Exception e) {
                    // Make sure we also don't throw anything, which would be
                    // passed to responseProcessor as a response ...
                    e.printStackTrace();
                }
            }
        };
    }
}
