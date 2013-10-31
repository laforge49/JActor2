package org.agilewiki.jactor2.core.blades.requestBus;

import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

abstract public class SubscribeAReq<FILTER, CONTENT extends Content<FILTER>>
        extends AsyncRequest<Subscription<FILTER, CONTENT>> {
    private final RequestBus<FILTER, CONTENT> requestBus;
    private final NonBlockingReactor subscriberReactor;
    private final FILTER filter;
    AsyncResponseProcessor<Subscription<FILTER, CONTENT>> dis = this;

    public SubscribeAReq(final RequestBus<FILTER, CONTENT> _requestBus,
                         final NonBlockingReactor _subscriberReactor,
                         final FILTER _filter) {
        super(_requestBus.getReactor());
        requestBus = _requestBus;
        subscriberReactor = _subscriberReactor;
        filter = _filter;
    }

    @Override
    protected void processAsyncRequest() throws Exception {
        final Subscription<FILTER, CONTENT> subscription =
                new Subscription<FILTER, CONTENT>(requestBus, subscriberReactor, filter) {
                    @Override
                    protected void processNotification(final CONTENT _content,
                                                       final AsyncResponseProcessor<Void> _asyncResponseProcessor)
                            throws Exception {
                        processNotification(_content, _asyncResponseProcessor);
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
