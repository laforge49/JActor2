package org.agilewiki.jactor2.core.impl.blades;

import org.agilewiki.jactor2.core.blades.pubSub.RequestBus;
import org.agilewiki.jactor2.core.blades.pubSub.SubscribeAOp;
import org.agilewiki.jactor2.core.blades.pubSub.Subscription;
import org.agilewiki.jactor2.core.impl.CallTestBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.reactors.CommonReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.ReactorClosedException;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;

import java.util.concurrent.atomic.AtomicInteger;

public class PubSubTest extends CallTestBase {
    public void testI() throws Exception {
        System.out.println("I");
        new Plant();
        try {
            final NonBlockingReactor reactor = new NonBlockingReactor();
            final RequestBus<Void> requestBus = new RequestBus<Void>(reactor);
            call(requestBus.signalsContentSOp(null));
            final Subscription<Void> s1 = call(new SubscribeAOp<Void>(
                    requestBus, reactor) {
                @Override
                protected void processContent(final Void _content,
                                              AsyncRequestImpl _asyncRequestImpl,
                                              AsyncResponseProcessor<Void> _asyncResponseProcessor)
                        throws Exception {
                    System.out.println("ping");
                    _asyncResponseProcessor.processAsyncResponse(null);
                }
            });
            call(requestBus.signalsContentSOp(null));
            s1.unsubscribe();
            call(requestBus.signalsContentSOp(null));
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
            call(requestBus.sendsContentAOp(null));
            assertEquals(counter.get(), 0);
            call(new SubscribeAOp<Void>(requestBus, subscriberReactor) {
                @Override
                protected void processContent(final Void _content,
                                              AsyncRequestImpl _asyncRequestImpl,
                                              AsyncResponseProcessor<Void> _asyncResponseProcessor)
                        throws Exception {
                    System.out.println("ping");
                    counter.incrementAndGet();
                    _asyncResponseProcessor.processAsyncResponse(null);
                }
            });
            call(requestBus.sendsContentAOp(null));
            assertEquals(counter.get(), 1);
            subscriberReactor.close();
            try {
                call(requestBus.sendsContentAOp(null));
            } catch (final ReactorClosedException e) {
            }
            assertEquals(counter.get(), 1);
        } finally {
            Plant.close();
        }
    }
}
