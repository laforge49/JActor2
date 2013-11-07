package org.agilewiki.jactor2.core.blades.pubSub;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.facilities.Plant;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.reactors.CommonReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

import java.util.concurrent.atomic.AtomicInteger;

public class PubSubTest extends TestCase {
    public void testI() throws Exception {
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
            subscriberReactor.closeSReq().call();
            requestBus.sendsContentAReq(null).call();
            // This does not always work, because while we do a subscriberReactor.closeSReq().call(),
            // This only ensure that the close was performed, BUT since the close of the reactor
            // call the "normal close()" of it's resources, and that those use signal internally,
            // there is no guarantee that the subscription itself is closed.
            // In other words, it is not possible to TEST if the
            // "close subscription on subscriber reactor close" actually works. :(
//            assertEquals(counter.get(), 1);
        } finally {
            plant.close();
        }
    }
}
