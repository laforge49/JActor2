package org.agilewiki.jactor2.core.blades.pubSub;

import org.agilewiki.jactor2.core.blades.filters.Filter;
import org.agilewiki.jactor2.core.reactors.CommonReactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;

/**
 * A request to subscribe to the content published by a RequestBus.
 * Note that one of the processContent methods must be overridden or
 * an UnsupportedOperationException will be thrown when content is received.
 *
 * @param <CONTENT> The type of content.
 */
public class SubscribeAReq<CONTENT> extends AsyncRequest<Subscription<CONTENT>> {
    private final RequestBus<CONTENT> requestBus;
    private final CommonReactor subscriberReactor;
    private final Filter<CONTENT> filter;
    AsyncResponseProcessor<Subscription<CONTENT>> dis = this;

    /**
     * Creates a request to subscribe to all the content published by a RequestBus.
     * When invoked, the request passes back a subscription which can be used to unsubscribe.
     *
     * @param _requestBus        The RequestBus being subscribed to.
     * @param _subscriberReactor The reactor of the subscriber blade.
     */
    public SubscribeAReq(final RequestBus<CONTENT> _requestBus,
            final CommonReactor _subscriberReactor) {
        this(_requestBus, _subscriberReactor, null);
    }

    /**
     * Creates a request to subscribe to selected content published by a RequestBus.
     *
     * @param _requestBus        The RequestBus being subscribed to.
     * @param _subscriberReactor The reactor of the subscriber blade.
     * @param _filter            A Filter that selects content of interest.
     */
    public SubscribeAReq(final RequestBus<CONTENT> _requestBus,
            final CommonReactor _subscriberReactor,
            final Filter<CONTENT> _filter) {
        super(_subscriberReactor);
        requestBus = _requestBus;
        subscriberReactor = _subscriberReactor;
        filter = _filter;
    }

    @Override
    public void processAsyncRequest() throws Exception {
        final Subscription<CONTENT> subscription = new Subscription<CONTENT>(
                requestBus, subscriberReactor, filter) {
            @Override
            protected void processContent(final CONTENT _content,
                                          AsyncRequestImpl _asyncRequestImpl,
                                          AsyncResponseProcessor<Void> _asyncResponseProcessor) throws Exception {
                SubscribeAReq.this.processContent(_content, _asyncRequestImpl, _asyncResponseProcessor);
            }
        };
        requestBus.subscriptions.put(subscription, Boolean.TRUE);
        subscriberReactor.addCloseable(subscription);
        dis.processAsyncResponse(subscription);
    }

    /**
     * Process the content of interest using the reactor of the subscriber.
     *
     * @param _content The received content.
     */
    protected void processContent(final CONTENT _content) throws Exception {
        throw new UnsupportedOperationException(
                "The processContent method was not overridden.");
    }

    /**
     * Process the content of interest using the reactor of the subscriber.
     */
    protected void processContent(final CONTENT _content,
                                  AsyncRequestImpl _asyncRequestImpl,
                                  AsyncResponseProcessor<Void> _asyncResponseProcessor) throws Exception {
        processContent(_content);
        _asyncResponseProcessor.processAsyncResponse(null);
    }
}
