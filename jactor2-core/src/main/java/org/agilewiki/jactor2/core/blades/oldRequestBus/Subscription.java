package org.agilewiki.jactor2.core.blades.oldRequestBus;

import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

abstract public class Subscription<CONTENT, RESPONSE> extends BladeBase
        implements AutoCloseable {
    public final RequestBus<CONTENT, RESPONSE> requestBus;

    public Subscription(final NonBlockingReactor _reactor,
            final RequestBus<CONTENT, RESPONSE> _requestBus) throws Exception {
        initialize(_reactor);
        requestBus = _requestBus;
    }

    abstract public AsyncRequest<RESPONSE> notificationAReq(CONTENT _content);

    @Override
    public void close() throws Exception {
        requestBus.unsubscribeSReq(this).signal();
    }

    public AsyncRequest<Boolean> subscribeAReq() {
        return new AsyncBladeRequest<Boolean>() {
            AsyncResponseProcessor<Boolean> dis = this;

            @Override
            protected void processAsyncRequest() throws Exception {
                if (getReactor() != requestBus.getReactor()) {
                    send(getReactor().getFacility().addAutoClosableSReq(
                            Subscription.this),
                            new AsyncResponseProcessor<Boolean>() {
                                @Override
                                public void processAsyncResponse(
                                        final Boolean _response)
                                        throws Exception {
                                    send(requestBus
                                            .subscribeSReq(Subscription.this),
                                            dis);
                                }
                            });
                } else {
                    send(requestBus.subscribeSReq(Subscription.this), dis);
                }
            }
        };
    }

    public AsyncRequest<Boolean> unsubscribeAReq() {
        return new AsyncBladeRequest<Boolean>() {
            AsyncResponseProcessor<Boolean> dis = this;

            @Override
            protected void processAsyncRequest() throws Exception {
                if (getReactor() != requestBus.getReactor()) {
                    send(getReactor().getFacility().removeAutoClosableSReq(
                            Subscription.this),
                            new AsyncResponseProcessor<Boolean>() {
                                @Override
                                public void processAsyncResponse(
                                        final Boolean _response)
                                        throws Exception {
                                    send(requestBus
                                            .unsubscribeSReq(Subscription.this),
                                            dis);
                                }
                            });
                } else {
                    send(requestBus.unsubscribeSReq(Subscription.this), dis);
                }
            }
        };
    }
}
