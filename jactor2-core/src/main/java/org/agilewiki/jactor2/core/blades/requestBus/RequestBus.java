package org.agilewiki.jactor2.core.blades.requestBus;

import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class RequestBus<CONTENT, RESPONSE> extends BladeBase {
    private final Set<Subscription<CONTENT, RESPONSE>> subscriptions =
            new HashSet<Subscription<CONTENT, RESPONSE>>();

    public RequestBus(final NonBlockingReactor _reactor) throws Exception {
        initialize(_reactor);
    }

    public Set<Subscription<CONTENT, RESPONSE>> getReadOnlySubscribers() {
        return Collections.unmodifiableSet(new HashSet<Subscription<CONTENT, RESPONSE>>(subscriptions));
    }

    public SyncRequest<Void> signalSReq(final CONTENT _content) {
        return new SyncBladeRequest<Void>() {
            @Override
            protected Void processSyncRequest() throws Exception {
                Iterator<Subscription<CONTENT, RESPONSE>> it = subscriptions.iterator();
                while (it.hasNext()) {
                    Subscription<CONTENT, RESPONSE> subscription = it.next();
                    subscription.notificationAReq(_content).signal();
                }
                return null;
            }
        };
    }

    SyncRequest<Boolean> subscribeSReq(
            final Subscription<CONTENT, RESPONSE> _subscription) {
        return new SyncBladeRequest<Boolean>() {
            @Override
            protected Boolean processSyncRequest() throws Exception {
                return subscriptions.add(_subscription);
            }
        };
    }

    SyncRequest<Boolean> unsubscribeSReq(
            final Subscription<CONTENT, RESPONSE> _subscription) {
        return new SyncBladeRequest<Boolean>() {
            @Override
            protected Boolean processSyncRequest() throws Exception {
                return subscriptions.remove(_subscription);
            }
        };
    }
}
