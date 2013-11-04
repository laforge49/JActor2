package org.agilewiki.jactor2.core.blades.oldRequestBus;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class RequestBus<CONTENT, RESPONSE> extends BladeBase {
    protected final Set<Subscription<CONTENT, RESPONSE>> subscriptions = new HashSet<Subscription<CONTENT, RESPONSE>>();

    public RequestBus(final NonBlockingReactor _reactor) throws Exception {
        initialize(_reactor);
    }

    public SyncRequest<Void> signalSReq(final CONTENT _content) {
        return new SyncBladeRequest<Void>() {
            @Override
            protected Void processSyncRequest() throws Exception {
                final Iterator<Subscription<CONTENT, RESPONSE>> it = subscriptions
                        .iterator();
                while (it.hasNext()) {
                    final Subscription<CONTENT, RESPONSE> subscription = it
                            .next();
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
