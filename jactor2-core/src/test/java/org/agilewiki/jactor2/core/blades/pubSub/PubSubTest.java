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
            RequestBus<Void, NullContent> requestBus =
                    new RequestBus<Void, NullContent>(reactor);
            requestBus.signalsContentSReq(new NullContent()).call();
            Subscription<Void, NullContent> s1 = new SubscribeAReq<Void, NullContent>(requestBus, reactor, null) {
                @Override
                protected void processNotification(NullContent _content, AsyncResponseProcessor<Void> _asyncResponseProcessor) throws Exception {
                    System.out.println("ping");
                    _asyncResponseProcessor.processAsyncResponse(null);
                }
            }.call();
            requestBus.signalsContentSReq(new NullContent()).call();
            s1.unsubscribeSReq().call();
            requestBus.signalsContentSReq(new NullContent()).call();
        } finally {
            plant.close();
        }
    }
}

class NullContent implements Content<Void> {
    @Override
    public boolean match(Void o) {
        return true;
    }
}

