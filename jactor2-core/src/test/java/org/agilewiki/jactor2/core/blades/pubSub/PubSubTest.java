package org.agilewiki.jactor2.core.blades.pubSub;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.facilities.Plant;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class PubSubTest extends TestCase {
    public void testI() throws Exception {
        final Plant plant = new Plant();
        try {
            NonBlockingReactor reactor = new NonBlockingReactor(plant);
            RequestBus<Void> requestBus =
                    new RequestBus<Void>(reactor);
            requestBus.signalsContentSReq(null).call();
            Subscription<Void> s1 = new SubscribeAReq<Void>(requestBus, reactor) {
                @Override
                protected void processContent(Void _content, AsyncResponseProcessor<Void> _asyncResponseProcessor) throws Exception {
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
}

