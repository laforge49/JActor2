package org.agilewiki.jactor2.core.blades.pubSub;

import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

abstract public class SubscribeAReq<CONTENT>
        extends AsyncRequest<Subscription<CONTENT>> {
    private final RequestBus<CONTENT> requestBus;
    private final NonBlockingReactor subscriberReactor;
    private final Filter<CONTENT> filter;
    AsyncResponseProcessor<Subscription<CONTENT>> dis = this;

    public SubscribeAReq(final RequestBus<CONTENT> _requestBus,
                         final NonBlockingReactor _subscriberReactor,
                         final Filter<CONTENT> _filter) {
        super(_requestBus.getReactor());
        requestBus = _requestBus;
        subscriberReactor = _subscriberReactor;
        filter = _filter;
    }

    @Override
    protected void processAsyncRequest() throws Exception {
        final Subscription<CONTENT> subscription =
                new Subscription<CONTENT>(requestBus, subscriberReactor, filter) {
                    @Override
                    protected void processNotification(final CONTENT _content,
                                                       final AsyncResponseProcessor<Void> _asyncResponseProcessor)
                            throws Exception {
                        SubscribeAReq.this.processNotification(_content, _asyncResponseProcessor);
                    }
                };
        requestBus.subscriptions.add(subscription);
        send(subscriberReactor.getFacility().addAutoClosableSReq(this),
                new AsyncResponseProcessor<Boolean>() {
                    @Override
                    public void processAsyncResponse(Boolean _response) throws Exception {
                        dis.processAsyncResponse(subscription);
                    }
                });
    }

    abstract protected void processNotification(CONTENT _content,
                                                AsyncResponseProcessor<Void> _asyncResponseProcessor)
            throws Exception;
}
