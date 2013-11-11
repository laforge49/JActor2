package org.agilewiki.jactor2.core.blades.transactions.properties;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.blades.pubSub.RequestBus;
import org.agilewiki.jactor2.core.blades.pubSub.SubscribeAReq;
import org.agilewiki.jactor2.core.util.immutable.ImmutableProperties;
import org.agilewiki.jactor2.core.facilities.Plant;
import org.agilewiki.jactor2.core.reactors.CommonReactor;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

import java.util.Iterator;
import java.util.SortedMap;

public class PTest extends TestCase {
    public void testI() throws Exception {
        final Plant plant = new Plant();
        try {
            PropertiesProcessor propertiesProcessor = new PropertiesProcessor(new IsolationReactor(plant));
            final CommonReactor reactor = new NonBlockingReactor(plant);

            RequestBus<ImmutablePropertyChanges> validationBus = propertiesProcessor.validationBus;
            new SubscribeAReq<ImmutablePropertyChanges>(validationBus, reactor){
                @Override
                protected void processContent(final ImmutablePropertyChanges _content)
                        throws Exception {
                    SortedMap<String, PropertyChange> readOnlyChanges = _content.readOnlyChanges;
                    final Iterator<PropertyChange> it = readOnlyChanges.values().iterator();
                    while (it.hasNext()) {
                        final PropertyChange propertyChange = it.next();
                        if (propertyChange.name.equals("fudge")) {
                            throw new IllegalArgumentException("no way");
                        }
                    }
                }
            }.call();

            RequestBus<ImmutablePropertyChanges> changeBus = propertiesProcessor.changeBus;
            new SubscribeAReq<ImmutablePropertyChanges>(changeBus, reactor){
                @Override
                protected void processContent(final ImmutablePropertyChanges _content)
                        throws Exception {
                    SortedMap<String, PropertyChange> readOnlyChanges = _content.readOnlyChanges;
                    System.out.println("\nchanges: " + readOnlyChanges.size());
                    final Iterator<PropertyChange> it = readOnlyChanges.values().iterator();
                    while (it.hasNext()) {
                        final PropertyChange propertyChange = it.next();
                        System.out.println("key=" + propertyChange.name + " old="
                                + propertyChange.oldValue + " new="
                                + propertyChange.newValue);
                    }
                }
            }.call();

            ImmutableProperties<Object> immutableState = propertiesProcessor.getImmutableState();
            assertEquals(0, immutableState.size());

            propertiesProcessor.putAReq("1", "first").call();
            assertEquals(0, immutableState.size());
            immutableState = propertiesProcessor.getImmutableState();
            assertEquals(1, immutableState.size());

            propertiesProcessor.putAReq("1", "second").call();
            assertEquals(1, immutableState.size());
            immutableState = propertiesProcessor.getImmutableState();
            assertEquals(1, immutableState.size());

            String msg = null;
            try {
                propertiesProcessor.putAReq("fudge", "second").call();
            } catch (final Exception e) {
                msg = e.getMessage();
            }
            assertEquals("no way", msg);
            assertEquals(1, immutableState.size());
            immutableState = propertiesProcessor.getImmutableState();
            assertEquals(1, immutableState.size());

            propertiesProcessor.putAReq("1", null).call();
            immutableState = propertiesProcessor.getImmutableState();
            assertEquals(0, immutableState.size());
        } finally {
            plant.close();
        }
    }
}
