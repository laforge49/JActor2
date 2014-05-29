package org.agilewiki.jactor2.core.xtend.blades;

import org.agilewiki.jactor2.core.blades.filters.EqualsFilter;
import org.agilewiki.jactor2.core.blades.pubSub.RequestBus;
import org.agilewiki.jactor2.core.blades.pubSub.SubscribeAReq;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;

class PubSubSample {
    def static void main(String[] args) throws Exception {
        val plant = new Plant();
        try {
            val reactor = new NonBlockingReactor();
            val requestBus = new RequestBus<String>(reactor);
            new SubscribeAReq<String>(requestBus, reactor) {
                override void processContent(String _content)
                        throws Exception {
                    System.out.println("got " + _content);
                }
            }.call();
            new SubscribeAReq<String>(requestBus, reactor,
                    new EqualsFilter<String>("ribit")) {
                override void processContent(String _content,
                        AsyncRequest<Void> _asyncRequest) {
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
            Plant.close();
        }
    }
}
