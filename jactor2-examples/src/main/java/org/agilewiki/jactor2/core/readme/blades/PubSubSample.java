package org.agilewiki.jactor2.core.readme.blades;

import org.agilewiki.jactor2.core.blades.filters.EqualsFilter;
import org.agilewiki.jactor2.core.blades.pubSub.RequestBus;
import org.agilewiki.jactor2.core.blades.pubSub.SubscribeAOp;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.impl.AsyncRequestImpl;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;

public class PubSubSample {
    public static void main(final String[] args) throws Exception {
        final Plant plant = new Plant();
        try {
            IsolationReactor reactor = new IsolationReactor();
            RequestBus<String> requestBus =
                    new RequestBus<String>(reactor);
            new SubscribeAOp<String>(requestBus, reactor) {
                @Override
                protected void processContent(String _content) throws Exception {
                    System.out.println("got " + _content);
                }
            }.call();
            new SubscribeAOp<String>(requestBus, reactor, new EqualsFilter<String>("ribit")) {
                @Override
                protected void processContent(String _content, AsyncRequestImpl _asyncRequestImpl,
                                              AsyncResponseProcessor<Void> _asyncResponseProcessor)
                        throws Exception {
                    System.out.println("*** Ribit! ***");
                    _asyncResponseProcessor.processAsyncResponse(null);
                }
            }.call();
            System.out.println("\nPublishing null.");
            requestBus.sendsContentAOp(null).call();
            System.out.println("\nPublishing ribit");
            requestBus.sendsContentAOp("ribit").call();
            System.out.println("\nPublishing abc");
            requestBus.sendsContentAOp("abc").call();
        } finally {
            plant.close();
        }
    }
}
