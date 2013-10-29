package org.agilewiki.jactor2.core.blades.transactions;

import org.agilewiki.jactor2.core.blades.ExceptionHandler;
import org.agilewiki.jactor2.core.blades.requestBus.RequestBus;
import org.agilewiki.jactor2.core.blades.requestBus.Subscription;
import org.agilewiki.jactor2.core.facilities.ServiceClosedException;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

import java.util.Iterator;

public class ValidationBus<IMMUTABLE_CHANGES> extends RequestBus<IMMUTABLE_CHANGES, Void> {
    public ValidationBus(NonBlockingReactor _reactor) throws Exception {
        super(_reactor);
    }

    public AsyncRequest<String> sendAReq(final IMMUTABLE_CHANGES _changes) {
        return new AsyncBladeRequest<String>() {
            AsyncResponseProcessor<String> dis = this;
            int count;
            int i;

            AsyncResponseProcessor<Void> notificationResponseProcessor =
                    new AsyncResponseProcessor<Void>() {
                        @Override
                        public void processAsyncResponse(final Void _response) throws Exception {
                            i++;
                            if (count == i)
                                dis.processAsyncResponse(null);
                        }
                    };

            @Override
            protected void processAsyncRequest() throws Exception {
                setExceptionHandler(new ExceptionHandler<String>() {
                    @Override
                    public String processException(Exception e) throws Exception {
                        if (e instanceof ServiceClosedException)
                            return null;
                        throw e;
                    }
                });
                count = subscriptions.size();
                if (count == 0) {
                    dis.processAsyncResponse(null);
                    return;
                }
                Iterator<Subscription<IMMUTABLE_CHANGES, Void>> it = subscriptions.iterator();
                while (it.hasNext()) {
                    Subscription<IMMUTABLE_CHANGES, Void> subscription = it.next();
                    send(subscription.notificationAReq(_changes), notificationResponseProcessor);
                }
            }
        };
    }
}
