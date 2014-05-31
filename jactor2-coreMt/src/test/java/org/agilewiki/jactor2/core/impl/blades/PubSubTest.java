package org.agilewiki.jactor2.core.impl.blades;

import java.util.concurrent.atomic.AtomicInteger;

import org.agilewiki.jactor2.core.blades.pubSub.RequestBus;
import org.agilewiki.jactor2.core.blades.pubSub.SubscribeAReq;
import org.agilewiki.jactor2.core.blades.pubSub.Subscription;
import org.agilewiki.jactor2.core.impl.CallTestBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.reactors.CommonReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.ReactorClosedException;
import org.agilewiki.jactor2.core.requests.AsyncRequest;

public class PubSubTest extends CallTestBase {
    public void testI() throws Exception {
        System.out.println("I");
        new Plant();
        try {
            final NonBlockingReactor reactor = new NonBlockingReactor();
            final RequestBus<Void> requestBus = new RequestBus<Void>(reactor);
            call(requestBus.signalsContentSReq(null));
            final Subscription<Void> s1 = call(new SubscribeAReq<Void>(
                    requestBus, reactor) {
                @Override
                protected void processContent(final Void _content,
                        final AsyncRequest<Void> _asyncRequest)
                        throws Exception {
                    System.out.println("ping");
                    _asyncRequest.processAsyncResponse(null);
                }
            });
            call(requestBus.signalsContentSReq(null));
            s1.unsubscribe();
            call(requestBus.signalsContentSReq(null));
        } finally {
            Plant.close();
        }
    }

    public void testJ() throws Exception {
        System.out.println("J");
        new Plant();
        try {
            final AtomicInteger counter = new AtomicInteger();
            final NonBlockingReactor busReactor = new NonBlockingReactor();
            final CommonReactor subscriberReactor = new NonBlockingReactor();
            final RequestBus<Void> requestBus = new RequestBus<Void>(busReactor);
            call(requestBus.sendsContentAReq(null));
            assertEquals(counter.get(), 0);
            final Subscription<Void> s1 = call(new SubscribeAReq<Void>(
                    requestBus, subscriberReactor) {
                @Override
                protected void processContent(final Void _content,
                        final AsyncRequest<Void> _asyncRequest) {
                    System.out.println("ping");
                    counter.incrementAndGet();
                    _asyncRequest.processAsyncResponse(null);
                }
            });
            call(requestBus.sendsContentAReq(null));
            assertEquals(counter.get(), 1);
            subscriberReactor.close();
            try {
                call(requestBus.sendsContentAReq(null));
            } catch (final ReactorClosedException e) {
            }
            assertEquals(counter.get(), 1);
        } finally {
            Plant.close();
        }
    }
}
