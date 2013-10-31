package org.agilewiki.jactor2.core.blades.requestBus;

import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

abstract public class Subscription<FILTER, CONTENT extends Content<FILTER>> extends BladeBase implements AutoCloseable {
    private final RequestBus<FILTER, CONTENT> requestBus;
    private final NonBlockingReactor subscriberReactor;
    final FILTER filter;

    Subscription(final RequestBus<FILTER, CONTENT> _requestBus,
                 final NonBlockingReactor _subscriberReactor,
                 final FILTER _filter) throws Exception {
        initialize(_requestBus.getReactor());
        requestBus = _requestBus;
        subscriberReactor = _subscriberReactor;
        filter = _filter;
    }

    public SyncRequest<Boolean> unsubscribeSReq() {
        return new SyncBladeRequest<Boolean>() {
            @Override
            protected Boolean processSyncRequest() throws Exception {
                if (!requestBus.subscriptions.remove(this))
                    return false;
                subscriberReactor.getFacility().removeAutoClosableSReq(Subscription.this).signal();
                return true;
            }
        };
    }

    @Override
    public void close() throws Exception {
        unsubscribeSReq().signal();
    }

    AsyncRequest<Void> notificationAReq(final CONTENT _content) {
        return new AsyncRequest<Void>(subscriberReactor) {
            @Override
            protected void processAsyncRequest() throws Exception {
                processNotification(_content, this);
            }
        };
    }

    abstract protected void processNotification(CONTENT content,
                                                AsyncResponseProcessor<Void> asyncResponseProcessor)
            throws Exception;
}
