package org.agilewiki.jactor2.core.blades.pubSub;

import org.agilewiki.jactor2.core.facilities.Plant;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.reactors.CommonReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class PubSubSample {
    public static void main(final String[] args) throws Exception {
        final Plant plant = new Plant();
        try {
            CommonReactor reactor = new NonBlockingReactor(plant);
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
                protected void processContent(String _content, AsyncRequest<Void> _asyncRequest) throws Exception {
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
