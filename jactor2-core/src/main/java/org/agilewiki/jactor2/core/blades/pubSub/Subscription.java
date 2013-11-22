package org.agilewiki.jactor2.core.blades.pubSub;

import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.reactors.CommonReactor;
import org.agilewiki.jactor2.core.util.Closeable;
import org.agilewiki.jactor2.core.util.CloseableBase;

/**
 * A subscription allows a subscriber to receive content of interest from a RequestBus.
 *
 * @param <CONTENT> The type of content.
 */
abstract public class Subscription<CONTENT> extends CloseableBase implements
        AutoCloseable {
    private final RequestBus<CONTENT> requestBus;
    private final CommonReactor subscriberReactor;
    final Filter<CONTENT> filter;

    Subscription(final RequestBus<CONTENT> _requestBus,
                 final CommonReactor _subscriberReactor,
                 final Filter<CONTENT> _filter) throws Exception {
        initialize(_requestBus.getReactor());
        requestBus = _requestBus;
        subscriberReactor = _subscriberReactor;
        if (_filter == null) {
            filter = new NullFilter<CONTENT>();
        } else {
            filter = _filter;
        }
    }

    /**
     * Returns a request to stop receiving the published content.
     *
     * @return The request.
     */
    public SyncRequest<Void> unsubscribeSReq() {
        return new SyncRequest<Void>(subscriberReactor) {
            @Override
            protected Void processSyncRequest() throws Exception {
                new SyncRequest<Void>(getReactor()) {
                    @Override
                    protected Void processSyncRequest() throws Exception {
                        requestBus.subscriptions.remove(Subscription.this);
                        return null;
                    }
                }.signal();
                subscriberReactor.removeCloseableSReq(Subscription.this).signal();
                return null;
            }
        };
    }

    /**
     * Stops the receipt of published content.
     */
    @Override
    public void close() throws Exception {
        unsubscribeSReq().signal();
    }

    AsyncRequest<Void> publicationAReq(final CONTENT _content) {
        return new AsyncRequest<Void>(subscriberReactor) {
            @Override
            protected void processAsyncRequest() throws Exception {
                processContent(_content, this);
            }
        };
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
