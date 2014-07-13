package org.agilewiki.jactor2.core.blades.pubSub;

import org.agilewiki.jactor2.core.blades.filters.Filter;
import org.agilewiki.jactor2.core.reactors.CommonReactor;
import org.agilewiki.jactor2.core.requests.AOp;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;

/**
 * A request to subscribe to the content published by a RequestBus.
 * Note that one of the processContent methods must be overridden or
 * an UnsupportedOperationException will be thrown when content is received.
 *
 * @param <CONTENT> The type of content.
 */
public class SubscribeAOp<CONTENT> extends AOp<Subscription<CONTENT>> {
    private final RequestBus<CONTENT> requestBus;
    private final CommonReactor subscriberReactor;
    private final Filter<CONTENT> filter;

    /**
     * Creates a request to subscribe to all the content published by a RequestBus.
     * When invoked, the request passes back a subscription which can be used to unsubscribe.
     *
     * @param _requestBus        The RequestBus being subscribed to.
     * @param _subscriberReactor The reactor of the subscriber blade.
     */
    public SubscribeAOp(final RequestBus<CONTENT> _requestBus,
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
    public SubscribeAOp(final RequestBus<CONTENT> _requestBus,
                        final CommonReactor _subscriberReactor,
                        final Filter<CONTENT> _filter) {
        super("subscribe", _subscriberReactor);
        requestBus = _requestBus;
        subscriberReactor = _subscriberReactor;
        filter = _filter;
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

    @Override
    public void processAsyncOperation(AsyncRequestImpl _asyncRequestImpl,
                                      AsyncResponseProcessor<Subscription<CONTENT>> _asyncResponseProcessor)
            throws Exception {
        final Subscription<CONTENT> subscription = new Subscription<CONTENT>(
                requestBus, subscriberReactor, filter) {
            @Override
            protected void processContent(final CONTENT _content,
                                          AsyncRequestImpl _asyncRequestImpl,
                                          AsyncResponseProcessor<Void> _asyncResponseProcessor) throws Exception {
                SubscribeAOp.this.processContent(_content, _asyncRequestImpl, _asyncResponseProcessor);
            }
        };
        requestBus.subscriptions.put(subscription, Boolean.TRUE);
        subscriberReactor.addCloseable(subscription);
        _asyncResponseProcessor.processAsyncResponse(subscription);
    }
}
