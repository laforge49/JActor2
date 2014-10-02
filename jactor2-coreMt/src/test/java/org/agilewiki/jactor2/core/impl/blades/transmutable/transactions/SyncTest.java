package org.agilewiki.jactor2.core.impl.blades.transmutable.transactions;

import org.agilewiki.jactor2.core.blades.transmutable.TransmutableString;
import org.agilewiki.jactor2.core.blades.transmutable.transactions.SyncTransaction;
import org.agilewiki.jactor2.core.blades.transmutable.transactions.TransmutableReference;
import org.agilewiki.jactor2.core.impl.CallTestBase;
import org.agilewiki.jactor2.core.impl.Plant;

import java.io.IOException;

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

        final SyncTransaction<String, TransmutableString> addMoreGood =
                new SyncTransaction<String, TransmutableString>(addGood) {
            @Override
            public void update(final TransmutableString transmutable) {
                transmutable.stringBuilder.insert(0, "more ");
            }
        };

        final SyncTransaction<String, TransmutableString> bogus =
                new SyncTransaction<String, TransmutableString>(addGood) {
            @Override
            public void update(final TransmutableString transmutable)
                    throws Exception {
                throw new IOException();
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
            call(addGood.applyAOp(t));
            System.out.println(t.getUnmodifiable()); // good fun
            call(noop.applyAOp(t));
            System.out.println(t.getUnmodifiable()); // good fun
            t = new TransmutableReference<String, TransmutableString>(new TransmutableString("grapes"));
            System.out.println(t.getUnmodifiable()); // grapes
            call(addMoreGood.applyAOp(t));
            System.out.println(t.getUnmodifiable()); // more good grapes
            call(noop.applyAOp(t));
            System.out.println(t.getUnmodifiable()); // more good grapes
            t = new TransmutableReference<String, TransmutableString>(new TransmutableString("times"));
            System.out.println(t.getUnmodifiable()); // times
            try {
                call(bogus.applyAOp(t));
            } catch (final Exception e) {
                System.out.println("*** " + e);
            }
            System.out.println(t.getUnmodifiable()); // times
            call(noop.applyAOp(t));
            System.out.println(t.getUnmodifiable()); // times
        } finally {
            Plant.close();
        }
    }
}
