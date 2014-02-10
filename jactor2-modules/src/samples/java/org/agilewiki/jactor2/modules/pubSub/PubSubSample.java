package org.agilewiki.jactor2.modules.pubSub;

import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;

public class PubSubSample {
    public static void main(final String[] args) throws Exception {
        final Plant plant = new Plant();
        try {
            NonBlockingReactor reactor = new NonBlockingReactor();
            RequestBus<String> requestBus =
                    new RequestBus<String>(reactor);
            new SubscribeAReq<String>(requestBus, reactor) {
                @Override
                protected void processContent(String _content) throws Exception {
                    System.out.println("got " + _content);
                }
            }.call();
            new SubscribeAReq<String>(requestBus, reactor, new EqualsFilter<String>("ribit")) {
                @Override
                protected void processContent(String _content, AsyncRequest<Void> _asyncRequest) {
                    System.out.println("*** Ribit! ***");
                    _asyncRequest.processAsyncResponse(null);
                }
            }.call();
            System.out.println("\nPublishing null.");
            requestBus.sendsContentAReq(null).call();
            System.out.println("\nPublishing ribit");
            requestBus.sendsContentAReq("ribit").call();
            System.out.println("\nPublishing abc");
            requestBus.sendsContentAReq("abc").call();
        } finally {
            plant.close();
        }
    }
}
