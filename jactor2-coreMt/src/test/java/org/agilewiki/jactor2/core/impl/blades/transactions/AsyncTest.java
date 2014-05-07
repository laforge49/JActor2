package org.agilewiki.jactor2.core.impl.blades.transactions;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.blades.transactions.AsyncTransaction;
import org.agilewiki.jactor2.core.blades.transactions.ImmutableReference;
import org.agilewiki.jactor2.core.blades.transactions.SyncTransaction;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.plant.DelayAReq;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;

public class AsyncTest extends TestCase {
    public void testI() throws Exception {
        new Plant();

        AsyncTransaction<String> addGood = new AsyncTransaction<String>() {
            @Override
            protected void update(final ImmutableReference<String> source,
                                  final AsyncResponseProcessor<Void> asyncResponseProcessor) throws Exception {
                applyAReq.send(new DelayAReq(1000), new AsyncResponseProcessor<Void>() {
                    @Override
                    public void processAsyncResponse(Void _response) throws Exception {
                        immutable = "good " + source.getImmutable();
                        asyncResponseProcessor.processAsyncResponse(null);
                    }
                });
            }
        };

        SyncTransaction<String> addMoreGood = new SyncTransaction<String>(addGood) {
            @Override
            public void update(ImmutableReference<String> source) {
                immutable = "more " + source.getImmutable();
            }
        };

        try {
            ImmutableReference m = new ImmutableReference<String>("fun");
            System.out.println(m.getImmutable()); // fun
            addGood.applyAReq(m).call();
            System.out.println(m.getImmutable()); // good fun
            m = new ImmutableReference<String>("grapes");
            System.out.println(m.getImmutable()); // grapes
            addMoreGood.applyAReq(m).call();
            System.out.println(m.getImmutable()); // more good grapes
        } finally {
            Plant.close();
        }
    }
}
