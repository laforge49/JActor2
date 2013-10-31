package org.agilewiki.jactor2.core.blades.transactions;

import org.agilewiki.jactor2.core.blades.oldRequestBus.RequestBus;
import org.agilewiki.jactor2.core.blades.oldRequestBus.Subscription;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class ChangeSubscription<IMMUTABLE_CHANGES> extends Subscription<IMMUTABLE_CHANGES, Void> {
    private final ChangeNotificationSubscriber<IMMUTABLE_CHANGES> subscriber;

    public ChangeSubscription(final ChangeNotificationSubscriber<IMMUTABLE_CHANGES> _subscriber,
                              RequestBus<IMMUTABLE_CHANGES, Void> _requestBus) throws Exception {
        super((NonBlockingReactor) _subscriber.getReactor(), _requestBus);
        subscriber = _subscriber;
    }

    @Override
    public AsyncRequest<Void> notificationAReq(IMMUTABLE_CHANGES _content) {
        return subscriber.changeNotificationAReq(_content);
    }
}
