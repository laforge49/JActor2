package org.agilewiki.jactor2.modules.pubSub;

import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.closeable.CloseableImpl;
import org.agilewiki.jactor2.core.closeable.CloseableImpl1;
import org.agilewiki.jactor2.core.closeable.Closeable;
import org.agilewiki.jactor2.core.reactors.CommonReactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;

/**
 * A subscription allows a subscriber to receive content of interest from a RequestBus.
 *
 * @param <CONTENT> The type of content.
 */
abstract public class Subscription<CONTENT> extends NonBlockingBladeBase implements
        Closeable {
    private final CloseableImpl closeableImpl;
    private final RequestBus<CONTENT> requestBus;
    private final CommonReactor subscriberReactor;
    final Filter<CONTENT> filter;

    Subscription(final RequestBus<CONTENT> _requestBus,
                 final CommonReactor _subscriberReactor,
                 final Filter<CONTENT> _filter) throws Exception {
        super(_requestBus.getReactor());
        closeableImpl = new CloseableImpl1(this);
        requestBus = _requestBus;
        subscriberReactor = _subscriberReactor;
        if (_filter == null) {
            filter = new NullFilter<CONTENT>();
        } else {
            filter = _filter;
        }
    }

    @Override
    public CloseableImpl asCloseableImpl() {
        return closeableImpl;
    }

    /**
     * Returns a request to stop receiving the published content.
     *
     * @return The request.
     */
    public boolean unsubscribe() throws Exception {
        if (!requestBus.subscriptions.remove(Subscription.this))
            return false;
        subscriberReactor.removeCloseable(Subscription.this);
        return true;
    }

    /**
     * Stops the receipt of published content.
     */
    @Override
    public void close() throws Exception {
        unsubscribe();
        closeableImpl.close();
    }

    AsyncRequest<Void> publicationAReq(final CONTENT _content) {
        return new AsyncRequest<Void>(subscriberReactor) {
            @Override
            public void processAsyncRequest() throws Exception {
                processContent(_content, this);
            }
        };
    }

    /**
     * Process the content of interest using the reactor of the subscriber.
     *
     * @param _content                The received content.
     * @param _asyncRequest     The request context.
     */
    abstract protected void processContent(CONTENT _content,
                                           AsyncRequest<Void> _asyncRequest)
            throws Exception;
}
