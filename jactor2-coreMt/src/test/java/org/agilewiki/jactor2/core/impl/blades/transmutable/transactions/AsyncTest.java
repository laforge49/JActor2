package org.agilewiki.jactor2.core.impl.blades.transmutable.transactions;

import org.agilewiki.jactor2.core.blades.transmutable.TransmutableString;
import org.agilewiki.jactor2.core.blades.transmutable.transactions.AsyncTransaction;
import org.agilewiki.jactor2.core.blades.transmutable.transactions.SyncTransaction;
import org.agilewiki.jactor2.core.blades.transmutable.transactions.TransmutableReference;
import org.agilewiki.jactor2.core.impl.CallTestBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.plant.DelayAOp;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;

public class AsyncTest extends CallTestBase {
    public void testI() throws Exception {
        new Plant();

        final AsyncTransaction<String, TransmutableString> addGood = new AsyncTransaction<String, TransmutableString>() {
            @Override
            protected void update(final TransmutableString transmutable,
                                  final AsyncResponseProcessor<Void> asyncResponseProcessor)
                    throws Exception {
                applyAReq.send(new DelayAOp(1000),
                        new AsyncResponseProcessor<Void>() {
                            @Override
                            public void processAsyncResponse(
                                    final Void _response) throws Exception {
                                transmutable.stringBuilder.insert(0, "good ");
                                asyncResponseProcessor
                                        .processAsyncResponse(null);
                            }
                        });
            }
        };

        final SyncTransaction<String, TransmutableString> addMoreGood =
                new SyncTransaction<String, TransmutableString>(addGood) {
            @Override
            public void update(final TransmutableString transmutable) {
                transmutable.stringBuilder.insert(0, "more ");
            }
        };

        final SyncTransaction<String, TransmutableString> noop =
                new SyncTransaction<String, TransmutableString>() {
                    @Override
                    protected void update(TransmutableString transmutable) throws Exception {
                    }
                };

        try {
            TransmutableReference<String, TransmutableString> t =
                    new TransmutableReference<String, TransmutableString>(new TransmutableString("fun"));
            System.out.println(t.getUnmodifiable()); // fun
            call(t.applyAOp(addGood));
            System.out.println(t.getUnmodifiable()); // good fun
            call(t.applyAOp(noop));
            System.out.println(t.getUnmodifiable()); // good fun
            t = new TransmutableReference<String, TransmutableString>(new TransmutableString("grapes"));
            System.out.println(t.getUnmodifiable()); // grapes
            call(t.applyAOp(addMoreGood));
            System.out.println(t.getUnmodifiable()); // more good grapes
            call(t.applyAOp(noop));
            System.out.println(t.getUnmodifiable()); // more good grapes
        } finally {
            Plant.close();
        }
    }
}
