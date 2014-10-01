package org.agilewiki.jactor2.core.impl.blades.transmutable.transactions;

import org.agilewiki.jactor2.core.blades.transmutable.TransmutableString;
import org.agilewiki.jactor2.core.blades.transmutable.transactions.SyncTransaction;
import org.agilewiki.jactor2.core.blades.transmutable.transactions.TransmutableReference;
import org.agilewiki.jactor2.core.impl.CallTestBase;
import org.agilewiki.jactor2.core.impl.Plant;

public class SyncTest extends CallTestBase {
    public void testI() throws Exception {
        new Plant();

        final SyncTransaction<String, TransmutableString> addGood =
                new SyncTransaction<String, TransmutableString>() {
            @Override
            protected void update(final TransmutableString transmutable)
                    throws Exception {
                transmutable.stringBuilder.insert(0, "good ");
            }
        };

        try {
            TransmutableReference<String, TransmutableString> t =
                    new TransmutableReference<String, TransmutableString>(new TransmutableString("fun"));
            System.out.println(t.getUnmodifiable()); // fun
            call(addGood.applyAOp(t));
            System.out.println(t.getUnmodifiable()); // good fun
        } finally {
            Plant.close();
        }
    }
}
