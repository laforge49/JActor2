package org.agilewiki.jactor2.core.blades.pubSub;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.facilities.Plant;
import org.agilewiki.jactor2.core.facilities.ServiceClosedException;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.reactors.CommonReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

import java.util.concurrent.atomic.AtomicInteger;

public class PubSubTest extends TestCase {
    public void testI() throws Exception {
        System.out.println("I");
        final Plant plant = new Plant();
        try {
            final CommonReactor reactor = new NonBlockingReactor(plant);
            final RequestBus<Void> requestBus = new RequestBus<Void>(reactor);
            requestBus.signalsContentSReq(null).call();
            final Subscription<Void> s1 = new SubscribeAReq<Void>(requestBus,
                    reactor) {
                @Override
                protected void processContent(
                        final Void _content,
                        final AsyncResponseProcessor<Void> _asyncResponseProcessor)
                        throws Exception {
                    System.out.println("ping");
                    _asyncResponseProcessor.processAsyncResponse(null);
                }
            }.call();
            requestBus.signalsContentSReq(null).call();
            s1.unsubscribeSReq().call();
            requestBus.signalsContentSReq(null).call();
        } finally {
            plant.close();
        }
    }

    public void testJ() throws Exception {
        System.out.println("J");
        final Plant plant = new Plant();
        try {
            final AtomicInteger counter = new AtomicInteger();
            final CommonReactor busReactor = new NonBlockingReactor(plant);
            final CommonReactor subscriberReactor = new NonBlockingReactor(
                    plant);
            final RequestBus<Void> requestBus = new RequestBus<Void>(busReactor);
            requestBus.sendsContentAReq(null).call();
            assertEquals(counter.get(), 0);
            final Subscription<Void> s1 = new SubscribeAReq<Void>(requestBus,
                    subscriberReactor) {
                @Override
                protected void processContent(
                        final Void _content,
                        final AsyncResponseProcessor<Void> _asyncResponseProcessor)
                        throws Exception {
                    System.out.println("ping");
                    counter.incrementAndGet();
                    _asyncResponseProcessor.processAsyncResponse(null);
                }
            }.call();
            requestBus.sendsContentAReq(null).call();
            assertEquals(counter.get(), 1);
            subscriberReactor.closeAReq().call();
            try {
             requestBus.sendsContentAReq(null).call();
            } catch (ServiceClosedException e) {
            }
            assertEquals(counter.get(), 1);
        } finally {
            plant.close();
        }
    }
}
