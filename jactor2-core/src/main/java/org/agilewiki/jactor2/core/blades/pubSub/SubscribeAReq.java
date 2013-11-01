package org.agilewiki.jactor2.core.blades.pubSub;

import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

/**
 * A request to subscribe to the content published by a RequestBus.
 *
 * @param <CONTENT> The type of content.
 */
abstract public class SubscribeAReq<CONTENT>
        extends AsyncRequest<Subscription<CONTENT>> {
    private final RequestBus<CONTENT> requestBus;
    private final NonBlockingReactor subscriberReactor;
    private final Filter<CONTENT> filter;
    AsyncResponseProcessor<Subscription<CONTENT>> dis = this;

    /**
     * Creates a request to subscribe to all the content published by a RequestBus.
     *
     * @param _requestBus        The RequestBus being subscribed to.
     * @param _subscriberReactor The reactor of the subscriber blade.
     */
    public SubscribeAReq(final RequestBus<CONTENT> _requestBus,
                         final NonBlockingReactor _subscriberReactor) {
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
                    protected void processContent(final CONTENT _content,
                                                  final AsyncResponseProcessor<Void> _asyncResponseProcessor)
                            throws Exception {
                        SubscribeAReq.this.processContent(_content, _asyncResponseProcessor);
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

    /**
     * Process the content of interest using the reactor of the subscriber.
     *
     * @param _content                The received content.
     * @param _asyncResponseProcessor Used to indicate when processing is complete.
     */
    abstract protected void processContent(CONTENT _content,
                                           AsyncResponseProcessor<Void> _asyncResponseProcessor)
            throws Exception;
}
