package org.agilewiki.jactor2.core.impl.blades.transmutable.tssmTransactions;

import org.agilewiki.jactor2.core.blades.pubSub.RequestBus;
import org.agilewiki.jactor2.core.blades.pubSub.SubscribeAOp;
import org.agilewiki.jactor2.core.blades.transmutable.tssmTransactions.TSSMChange;
import org.agilewiki.jactor2.core.blades.transmutable.tssmTransactions.TSSMChanges;
import org.agilewiki.jactor2.core.blades.transmutable.tssmTransactions.TSSMReference;
import org.agilewiki.jactor2.core.blades.transmutable.tssmTransactions.TSSMUpdateTransaction;
import org.agilewiki.jactor2.core.impl.CallTestBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.reactors.CommonReactor;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;

public class TSSMTransactionTest extends CallTestBase {
    public void testI() throws Exception {
        new Plant();
        try {
            final TSSMReference<String> propertiesReference = new TSSMReference<String>();
            final IsolationReactor reactor = new IsolationReactor();

            final RequestBus<TSSMChanges<String>> validationBus = propertiesReference.validationBus;
            call(new SubscribeAOp<TSSMChanges<String>>(validationBus,
                    reactor) {
                @Override
                protected void processContent(
                        final TSSMChanges<String> _content)
                        throws Exception {
                    final List<TSSMChange<String>> readOnlyChanges = _content.unmodifiableChanges;
                    final Iterator<TSSMChange<String>> it = readOnlyChanges.iterator();
                    while (it.hasNext()) {
                        final TSSMChange<String> propertyChange = it
                                .next();
                        if (propertyChange.name.equals("fudge")) {
                            throw new IOException("no way");
                        }
                    }
                }
            });

            final RequestBus<TSSMChanges<String>> changeBus = propertiesReference.changeBus;
            call(new SubscribeAOp<TSSMChanges<String>>(changeBus, reactor) {
                @Override
                protected void processContent(
                        final TSSMChanges<String> _content)
                        throws Exception {
                    final List<TSSMChange<String>> readOnlyChanges = _content.unmodifiableChanges;
                    System.out.println("\nchanges: " + readOnlyChanges.size());
                    final Iterator<TSSMChange<String>> it = readOnlyChanges.iterator();
                    while (it.hasNext()) {
                        final TSSMChange<String> propertyChange = it
                                .next();
                        System.out.println("key=" + propertyChange.name
                                + " old=" + propertyChange.oldValue + " new="
                                + propertyChange.newValue);
                    }
                }
            });

            SortedMap<String, String> immutableState = propertiesReference.getUnmodifiable();
            assertEquals(0, immutableState.size());

            call(propertiesReference.applyAOp(new TSSMUpdateTransaction<String>("1", "first")));
            assertEquals(0, immutableState.size());
            immutableState = propertiesReference.getUnmodifiable();
            assertEquals(1, immutableState.size());

            call(propertiesReference.applyAOp(new TSSMUpdateTransaction<String>("1", "second")));
            assertEquals(1, immutableState.size());
            immutableState = propertiesReference.getUnmodifiable();
            assertEquals(1, immutableState.size());

            String msg = null;
            try {
                call(propertiesReference.applyAOp(new TSSMUpdateTransaction<String>("fudge", "second")));
            } catch (final IOException e) {
                msg = e.getMessage();
            }

            assertEquals("no way", msg);
            assertEquals(1, immutableState.size());
            immutableState = propertiesReference.getUnmodifiable();
            assertEquals(1, immutableState.size());

            TSSMUpdateTransaction ut = new TSSMUpdateTransaction<String>("1", (String) null);
            call(propertiesReference.applyAOp(ut));
            System.out.println(ut);
            immutableState = propertiesReference.getUnmodifiable();
            assertEquals(0, immutableState.size());
        } finally {
            Plant.close();
        }
    }
}
