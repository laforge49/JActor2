package org.agilewiki.jactor2.core.blade.transactions.properties;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.blades.transactions.ChangeNotificationSubscriber;
import org.agilewiki.jactor2.core.blades.transactions.Validator;
import org.agilewiki.jactor2.core.blades.transactions.properties.PropertiesBlade;
import org.agilewiki.jactor2.core.blades.transactions.properties.PropertiesProcessor;
import org.agilewiki.jactor2.core.blades.transactions.properties.PropertyChange;
import org.agilewiki.jactor2.core.blades.transactions.properties.PropertyChanges;
import org.agilewiki.jactor2.core.facilities.Plant;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

import java.util.Iterator;
import java.util.SortedMap;

public class PTest extends TestCase {
    public void testI() throws Exception {
        final Plant plant = new Plant();
        try {
            PTDriver ptDriver = new PTDriver(new NonBlockingReactor(plant));
            PropertiesProcessor propertiesProcessor = ptDriver.getPropertiesProcessor();
            propertiesProcessor.addValidatorAReq(ptDriver).call();
            propertiesProcessor.addChangeNotificationSubscriberAReq(ptDriver).call();

            SortedMap<String, Object> immutableState = propertiesProcessor.getImmutableState();
            assertEquals(0, immutableState.size());

            assertNull(ptDriver.putAReq("1", "first").call());
            assertEquals(0, immutableState.size());
            immutableState = propertiesProcessor.getImmutableState();
            assertEquals(1, immutableState.size());

            assertNull(ptDriver.putAReq("1", "second").call());
            assertEquals(1, immutableState.size());
            immutableState = propertiesProcessor.getImmutableState();
            assertEquals(1, immutableState.size());

            assertEquals("no way", ptDriver.putAReq("fudge", "second").call());
            assertEquals(1, immutableState.size());
            immutableState = propertiesProcessor.getImmutableState();
            assertEquals(1, immutableState.size());

            assertNull(ptDriver.putAReq("1", null).call());
            immutableState = propertiesProcessor.getImmutableState();
            assertEquals(0, immutableState.size());
        } finally {
            plant.close();
        }
    }
}

class PTDriver extends PropertiesBlade
        implements ChangeNotificationSubscriber<PropertyChanges>, Validator<PropertyChanges> {
    PTDriver(final NonBlockingReactor _reactor) throws Exception {
        super(_reactor);
    }

    @Override
    public AsyncRequest<Void> changeNotificationAReq(final PropertyChanges _changes) {
        return new AsyncBladeRequest<Void>() {
            AsyncResponseProcessor<Void> dis = this;

            @Override
            protected void processAsyncRequest() throws Exception {
                SortedMap<String, PropertyChange> changes = _changes.readOnlyPropertyChanges;
                System.out.println("\nchanges: " + changes.size());
                Iterator<PropertyChange> it = changes.values().iterator();
                while (it.hasNext()) {
                    PropertyChange propertyChange = it.next();
                    System.out.println("key=" + propertyChange.name +
                            " old=" + propertyChange.oldValue +
                            " new=" + propertyChange.newValue);
                }
                dis.processAsyncResponse(null);
            }
        };
    }

    @Override
    public AsyncRequest<String> validateAReq(final PropertyChanges _changes) {
        return new AsyncBladeRequest<String>() {
            AsyncResponseProcessor<String> dis = this;

            @Override
            protected void processAsyncRequest() throws Exception {
                SortedMap<String, PropertyChange> changes = _changes.readOnlyPropertyChanges;
                Iterator<PropertyChange> it = changes.values().iterator();
                while (it.hasNext()) {
                    PropertyChange propertyChange = it.next();
                    if (propertyChange.name.equals("fudge")) {
                        dis.processAsyncResponse("no way");
                        return;
                    }
                }
                dis.processAsyncResponse(null);
            }
        };
    }
}