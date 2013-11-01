package org.agilewiki.jactor2.core.blades.pubSub;

import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A blade that publishes content to interested subscribers, using either signals or sends.
 *
 * @param <CONTENT> The type of content.
 */
public class RequestBus<CONTENT> extends BladeBase {
    final Set<Subscription<CONTENT>> subscriptions = new HashSet<Subscription<CONTENT>>();

    /**
     * Create a RequestBus blade
     *
     * @param _reactor The blade's reactor.
     */
    public RequestBus(final NonBlockingReactor _reactor) throws Exception {
        initialize(_reactor);
    }

    /**
     * Returns a request to send some content to all the interested subscribers
     * without waiting for those subscribers to process the content.
     *
     * @param _content The content to be published.
     * @return The request.
     */
    public SyncRequest<Void> signalsContentSReq(final CONTENT _content) {
        return new SyncBladeRequest<Void>() {
            @Override
            protected Void processSyncRequest() throws Exception {
                Iterator<Subscription<CONTENT>> it = subscriptions.iterator();
                while (it.hasNext()) {
                    Subscription<CONTENT> subscription = it.next();
                    Filter<CONTENT> filter = subscription.filter;
                    if (filter.match(_content))
                        subscription.publicationAReq(_content).signal();
                }
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
                public void processAsyncResponse(Void _response) throws Exception {
                    i++;
                    if (i == count)
                        dis.processAsyncResponse(null);
                }
            };

            @Override
            protected void processAsyncRequest() throws Exception {
                count = subscriptions.size();
                Iterator<Subscription<CONTENT>> it = subscriptions.iterator();
                while (it.hasNext()) {
                    Subscription<CONTENT> subscription = it.next();
                    Filter<CONTENT> filter = subscription.filter;
                    if (filter.match(_content))
                        send(subscription.publicationAReq(_content), sendResponse);
                    else
                        count--;
                }
                if (count == 0) {
                    dis.processAsyncResponse(null);
                    return;
                }
            }
        };
    }
}
