package org.agilewiki.jactor2.core.blades.pubSub;

import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.blades.filters.Filter;
import org.agilewiki.jactor2.core.blades.filters.NullFilter;
import org.agilewiki.jactor2.core.closeable.Closeable;
import org.agilewiki.jactor2.core.closeable.impl.CloseableImpl;
import org.agilewiki.jactor2.core.plant.impl.PlantImpl;
import org.agilewiki.jactor2.core.reactors.CommonReactor;
import org.agilewiki.jactor2.core.requests.AIOp;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;

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
        closeableImpl = PlantImpl.getSingleton().createCloseableImpl(this);
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

    AIOp<Void> publicationAOp(final CONTENT _content) {
        return new AIOp<Void>("publication", subscriberReactor) {
            @Override
            protected void processAsyncOperation(AsyncRequestImpl _asyncRequestImpl,
                                              AsyncResponseProcessor<Void> _asyncResponseProcessor) throws Exception {
                processContent(_content, _asyncRequestImpl, _asyncResponseProcessor);
            }
        };
    }

    /**
     * Process the content of interest using the reactor of the subscriber.
     */
    abstract protected void processContent(CONTENT _content,
                                           AsyncRequestImpl _asyncRequestImpl,
                                           AsyncResponseProcessor<Void> _asyncResponseProcessor)
            throws Exception;
}
