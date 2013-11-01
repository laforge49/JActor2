package org.agilewiki.jactor2.core.blades.pubSub;

import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class RequestBus<CONTENT> extends BladeBase {
    final Set<Subscription<CONTENT>> subscriptions = new HashSet<Subscription<CONTENT>>();

    public RequestBus(final NonBlockingReactor _reactor) throws Exception {
        initialize(_reactor);
    }

    public SyncRequest<Void> signalsContentSReq(final CONTENT _content) {
        return new SyncBladeRequest<Void>() {
            @Override
            protected Void processSyncRequest() throws Exception {
                Iterator<Subscription<CONTENT>> it = subscriptions.iterator();
                while (it.hasNext()) {
                    Subscription<CONTENT> subscription = it.next();
                    Filter<CONTENT> filter = subscription.filter;
                    if (filter.match(_content))
                        subscription.notificationAReq(_content).signal();
                }
                return null;
            }
        };
    }

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
                        send(subscription.notificationAReq(_content), sendResponse);
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
