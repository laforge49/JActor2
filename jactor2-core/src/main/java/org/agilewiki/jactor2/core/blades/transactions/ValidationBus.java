package org.agilewiki.jactor2.core.blades.transactions;

import org.agilewiki.jactor2.core.blades.ExceptionHandler;
import org.agilewiki.jactor2.core.blades.oldRequestBus.RequestBus;
import org.agilewiki.jactor2.core.blades.oldRequestBus.Subscription;
import org.agilewiki.jactor2.core.facilities.ServiceClosedException;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

import java.util.Iterator;

public class ValidationBus<
        STATE,
        STATE_WRAPPER extends AutoCloseable,
        IMMUTABLE_CHANGES extends ImmutableChanges,
        IMMUTABLE_STATE> extends RequestBus<IMMUTABLE_CHANGES, Void> {
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
                Iterator<Subscription<IMMUTABLE_CHANGES, Void>> it = subscriptions.iterator();
                while (it.hasNext()) {
                    ValidationSubscription<STATE, STATE_WRAPPER, IMMUTABLE_CHANGES, IMMUTABLE_STATE>
                            subscription = (ValidationSubscription) it.next();
                    Validator<IMMUTABLE_CHANGES> validator = subscription.validator;
                    String prefix = validator.getPrefix();
                    if (_changes.hasMatchingChange(prefix))
                        send(subscription.notificationAReq(_changes), notificationResponseProcessor);
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