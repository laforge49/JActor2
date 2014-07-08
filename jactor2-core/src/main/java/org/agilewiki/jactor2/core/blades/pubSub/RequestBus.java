package org.agilewiki.jactor2.core.blades.pubSub;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.blades.filters.Filter;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.SOp;
import org.agilewiki.jactor2.core.requests.impl.RequestImpl;

/**
 * A blade that publishes content to interested subscribers, using either signals or sends.
 *
 * @param <CONTENT> The type of content.
 */
public class RequestBus<CONTENT> extends NonBlockingBladeBase {
    final Map<Subscription<CONTENT>, Boolean> subscriptions = new ConcurrentHashMap<Subscription<CONTENT>, Boolean>();

    public RequestBus() throws Exception {
    }

    public RequestBus(final NonBlockingReactor _reactor) {
        super(_reactor);
    }

    private void signalContent(final CONTENT _content) {
        final Iterator<Subscription<CONTENT>> it = subscriptions.keySet()
                .iterator();
        while (it.hasNext()) {
            final Subscription<CONTENT> subscription = it.next();
            final Filter<CONTENT> filter = subscription.filter;
            if (filter.match(_content)) {
                subscription.publicationAReq(_content).signal();
            }
        }
    }

    /**
     * Sends some content to all the interested subscribers via a signal.
     *
     * @param _content
     * @param _sourceReactor
     */
    public void signalContent(final CONTENT _content,
            final NonBlockingReactor _sourceReactor) {
        directCheck(_sourceReactor);
        signalContent(_content);
    }

    /**
     * Returns a request to send some content to all the interested subscribers
     * without waiting for those subscribers to process the content.
     *
     * @param _content The content to be published.
     * @return The request.
     */
    public SOp<Void> signalsContentSOp(final CONTENT _content) {
        return new SOp<Void>("signalsContent", getReactor()) {
            @Override
            public Void processSyncOperation(RequestImpl _requestImpl) throws Exception {
                signalContent(_content);
                return null;
            }
        };
    }

    /**
     * Returns a request to send some content to all the interested subscribers.
     *
     * @param _content The content to be published.
     * @return The request.
     */
    public AsyncRequest<Void> sendsContentAReq(final CONTENT _content) {
        return new AsyncBladeRequest<Void>() {
            final AsyncResponseProcessor<Void> dis = this;

            int count;
            int i;

            final AsyncResponseProcessor<Void> sendResponse = new AsyncResponseProcessor<Void>() {
                @Override
                public void processAsyncResponse(final Void _response)
                        throws Exception {
                    i++;
                    if (i == count) {
                        dis.processAsyncResponse(null);
                    }
                }
            };

            @Override
            public void processAsyncRequest() throws Exception {
                setNoHungRequestCheck();
                count = subscriptions.size();
                final Iterator<Subscription<CONTENT>> it = subscriptions
                        .keySet().iterator();
                while (it.hasNext()) {
                    final Subscription<CONTENT> subscription = it.next();
                    final Filter<CONTENT> filter = subscription.filter;
                    if (filter.match(_content)) {
                        send(subscription.publicationAReq(_content),
                                sendResponse);
                    } else {
                        count--;
                    }
                }
                if (count == 0) {
                    dis.processAsyncResponse(null);
                    return;
                }
            }
        };
    }
}
