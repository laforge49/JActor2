package org.agilewiki.jactor2.core.blades.oldTransactions.oldProperties;

import java.util.Iterator;
import java.util.SortedMap;

import junit.framework.TestCase;

import org.agilewiki.jactor2.core.blades.oldTransactions.ChangeNotificationSubscriber;
import org.agilewiki.jactor2.core.blades.oldTransactions.Validator;
import org.agilewiki.jactor2.core.facilities.Plant;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class PTest extends TestCase {
    public void testI() throws Exception {
        final Plant plant = new Plant();
        try {
            final PTDriver ptDriver = new PTDriver(
                    new NonBlockingReactor(plant));
            ptDriver.addValidatorAReq(ptDriver).call();
            ptDriver.addChangeNotificationSubscriberAReq(ptDriver).call();

            SortedMap<String, Object> immutableState = ptDriver
                    .getImmutableState();
            assertEquals(0, immutableState.size());

            ptDriver.putAReq("1", "first").call();
            assertEquals(0, immutableState.size());
            immutableState = ptDriver.getImmutableState();
            assertEquals(1, immutableState.size());

            ptDriver.putAReq("1", "second").call();
            assertEquals(1, immutableState.size());
            immutableState = ptDriver.getImmutableState();
            assertEquals(1, immutableState.size());

            String msg = null;
            try {
                ptDriver.putAReq("fudge", "second").call();
            } catch (final Exception e) {
                msg = e.getMessage();
            }
            assertEquals("no way", msg);
            assertEquals(1, immutableState.size());
            immutableState = ptDriver.getImmutableState();
            assertEquals(1, immutableState.size());

            ptDriver.putAReq("1", null).call();
            immutableState = ptDriver.getImmutableState();
            assertEquals(0, immutableState.size());
        } finally {
            plant.close();
        }
    }
}

class PTDriver extends PropertiesBlade implements
        ChangeNotificationSubscriber<PropertyChanges>,
        Validator<PropertyChanges> {
    PTDriver(final NonBlockingReactor _reactor) throws Exception {
        super(_reactor);
    }

    @Override
    public AsyncRequest<Void> changeNotificationAReq(
            final PropertyChanges _changes) {
        return new AsyncBladeRequest<Void>() {
            AsyncResponseProcessor<Void> dis = this;

            @Override
            protected void processAsyncRequest() throws Exception {
                final SortedMap<String, PropertyChange> changes = _changes.readOnlyPropertyChanges;
                System.out.println("\nchanges: " + changes.size());
                final Iterator<PropertyChange> it = changes.values().iterator();
                while (it.hasNext()) {
                    final PropertyChange propertyChange = it.next();
                    System.out.println("key=" + propertyChange.name + " old="
                            + propertyChange.oldValue + " new="
                            + propertyChange.newValue);
                }
                dis.processAsyncResponse(null);
            }
        };
    }

    @Override
    public String getPrefix() {
        return "";
    }

    @Override
    public AsyncRequest<Void> validateAReq(final PropertyChanges _changes) {
        return new AsyncBladeRequest<Void>() {
            AsyncResponseProcessor<Void> dis = this;

            @Override
            protected void processAsyncRequest() throws Exception {
                final SortedMap<String, PropertyChange> changes = _changes.readOnlyPropertyChanges;
                final Iterator<PropertyChange> it = changes.values().iterator();
                while (it.hasNext()) {
                    final PropertyChange propertyChange = it.next();
                    if (propertyChange.name.equals("fudge")) {
                        throw new IllegalArgumentException("no way");
                    }
                }
                dis.processAsyncResponse(null);
            }
        };
    }
}