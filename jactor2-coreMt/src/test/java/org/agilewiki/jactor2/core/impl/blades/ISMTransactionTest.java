package org.agilewiki.jactor2.core.impl.blades;

import java.io.IOException;
import java.util.Iterator;
import java.util.SortedMap;

import org.agilewiki.jactor2.core.blades.ismTransactions.ISMReference;
import org.agilewiki.jactor2.core.blades.ismTransactions.ISMUpdateTransaction;
import org.agilewiki.jactor2.core.blades.ismTransactions.ImmutableChange;
import org.agilewiki.jactor2.core.blades.ismTransactions.ImmutableChanges;
import org.agilewiki.jactor2.core.blades.pubSub.RequestBus;
import org.agilewiki.jactor2.core.blades.pubSub.SubscribeAOp;
import org.agilewiki.jactor2.core.blades.transactions.ISMap;
import org.agilewiki.jactor2.core.impl.CallTestBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.reactors.CommonReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class ISMTransactionTest extends CallTestBase {
    public void testI() throws Exception {
        new Plant();
        try {
            final ISMReference<String> propertiesReference = new ISMReference<String>();
            final CommonReactor reactor = new NonBlockingReactor();

            final RequestBus<ImmutableChanges<String>> validationBus = propertiesReference.validationBus;
            call(new SubscribeAOp<ImmutableChanges<String>>(validationBus,
                    reactor) {
                @Override
                protected void processContent(
                        final ImmutableChanges<String> _content)
                        throws Exception {
                    final SortedMap<String, ImmutableChange<String>> readOnlyChanges = _content.readOnlyChanges;
                    final Iterator<ImmutableChange<String>> it = readOnlyChanges
                            .values().iterator();
                    while (it.hasNext()) {
                        final ImmutableChange<String> propertyChange = it
                                .next();
                        if (propertyChange.name.equals("fudge")) {
                            throw new IOException("no way");
                        }
                    }
                }
            });

            final RequestBus<ImmutableChanges<String>> changeBus = propertiesReference.changeBus;
            call(new SubscribeAOp<ImmutableChanges<String>>(changeBus, reactor) {
                @Override
                protected void processContent(
                        final ImmutableChanges<String> _content)
                        throws Exception {
                    final SortedMap<String, ImmutableChange<String>> readOnlyChanges = _content.readOnlyChanges;
                    System.out.println("\nchanges: " + readOnlyChanges.size());
                    final Iterator<ImmutableChange<String>> it = readOnlyChanges
                            .values().iterator();
                    while (it.hasNext()) {
                        final ImmutableChange<String> propertyChange = it
                                .next();
                        System.out.println("key=" + propertyChange.name
                                + " old=" + propertyChange.oldValue + " new="
                                + propertyChange.newValue);
                    }
                }
            });

            ISMap<String> immutableState = propertiesReference.getImmutable();
            assertEquals(0, immutableState.size());

            call(new ISMUpdateTransaction<String>("1", "first")
                    .applyAOp(propertiesReference));
            assertEquals(0, immutableState.size());
            immutableState = propertiesReference.getImmutable();
            assertEquals(1, immutableState.size());

            call(new ISMUpdateTransaction<String>("1", "second")
                    .applyAOp(propertiesReference));
            assertEquals(1, immutableState.size());
            immutableState = propertiesReference.getImmutable();
            assertEquals(1, immutableState.size());

            String msg = null;
            try {
                call(new ISMUpdateTransaction<String>("fudge", "second")
                        .applyAOp(propertiesReference));
            } catch (final Exception e) {
                msg = e.getMessage();
            }
            assertEquals("no way", msg);
            assertEquals(1, immutableState.size());
            immutableState = propertiesReference.getImmutable();
            assertEquals(1, immutableState.size());

            call(new ISMUpdateTransaction<String>("1", (String) null)
                    .applyAOp(propertiesReference));
            immutableState = propertiesReference.getImmutable();
            assertEquals(0, immutableState.size());
        } finally {
            Plant.close();
        }
    }
}
