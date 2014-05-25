package org.agilewiki.jactor2.core.impl.blades;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.blades.ismTransactions.ISMReference;
import org.agilewiki.jactor2.core.blades.ismTransactions.ISMUpdateTransaction;
import org.agilewiki.jactor2.core.blades.ismTransactions.ImmutableChange;
import org.agilewiki.jactor2.core.blades.ismTransactions.ImmutableChanges;
import org.agilewiki.jactor2.core.blades.pubSub.RequestBus;
import org.agilewiki.jactor2.core.blades.pubSub.SubscribeAReq;
import org.agilewiki.jactor2.core.blades.transactions.ISMap;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.reactors.CommonReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

import java.io.IOException;
import java.util.Iterator;
import java.util.SortedMap;

public class ISMTransactionTest extends TestCase {
    public void testI() throws Exception {
        new Plant();
        try {
            ISMReference<String> propertiesReference = new ISMReference<String>();
            final CommonReactor reactor = new NonBlockingReactor();

            RequestBus<ImmutableChanges<String>> validationBus = propertiesReference.validationBus;
            new SubscribeAReq<ImmutableChanges<String>>(validationBus, reactor){
                @Override
                protected void processContent(final ImmutableChanges<String> _content)
                        throws Exception {
                    SortedMap<String, ImmutableChange<String>> readOnlyChanges = _content.readOnlyChanges;
                    final Iterator<ImmutableChange<String>> it = readOnlyChanges.values().iterator();
                    while (it.hasNext()) {
                        final ImmutableChange<String> propertyChange = it.next();
                        if (propertyChange.name.equals("fudge")) {
                            throw new IOException("no way");
                        }
                    }
                }
            }.call();

            RequestBus<ImmutableChanges<String>> changeBus = propertiesReference.changeBus;
            new SubscribeAReq<ImmutableChanges<String>>(changeBus, reactor){
                @Override
                protected void processContent(final ImmutableChanges<String> _content)
                        throws Exception {
                    SortedMap<String, ImmutableChange<String>> readOnlyChanges = _content.readOnlyChanges;
                    System.out.println("\nchanges: " + readOnlyChanges.size());
                    final Iterator<ImmutableChange<String>> it = readOnlyChanges.values().iterator();
                    while (it.hasNext()) {
                        final ImmutableChange<String> propertyChange = it.next();
                        System.out.println("key=" + propertyChange.name + " old="
                                + propertyChange.oldValue + " new="
                                + propertyChange.newValue);
                    }
                }
            }.call();

            ISMap<String> immutableState = propertiesReference.getImmutable();
            assertEquals(0, immutableState.size());

            new ISMUpdateTransaction<String>("1", "first").applyAReq(propertiesReference).call();
            assertEquals(0, immutableState.size());
            immutableState = propertiesReference.getImmutable();
            assertEquals(1, immutableState.size());

            new ISMUpdateTransaction<String>("1", "second").applyAReq(propertiesReference).call();
            assertEquals(1, immutableState.size());
            immutableState = propertiesReference.getImmutable();
            assertEquals(1, immutableState.size());

            String msg = null;
            try {
                new ISMUpdateTransaction<String>("fudge", "second").applyAReq(propertiesReference).call();
            } catch (final Exception e) {
                msg = e.getMessage();
            }
            assertEquals("no way", msg);
            assertEquals(1, immutableState.size());
            immutableState = propertiesReference.getImmutable();
            assertEquals(1, immutableState.size());

            new ISMUpdateTransaction<String>("1", (String) null).applyAReq(propertiesReference).call();
            immutableState = propertiesReference.getImmutable();
            assertEquals(0, immutableState.size());
        } finally {
            Plant.close();
        }
    }
}
