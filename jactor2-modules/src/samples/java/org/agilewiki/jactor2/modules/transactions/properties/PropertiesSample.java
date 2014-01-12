package org.agilewiki.jactor2.modules.transactions.properties;

import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.reactors.CommonReactor;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.modules.MPlant;
import org.agilewiki.jactor2.modules.pubSub.RequestBus;
import org.agilewiki.jactor2.modules.pubSub.SubscribeAReq;

import java.util.Iterator;
import java.util.SortedMap;

public class PropertiesSample {
    public static void main(final String[] _args) throws Exception {
        new MPlant();
        try {
            PropertiesProcessor propertiesProcessor = new PropertiesProcessor(Plant.getInternalReactor());
            final CommonReactor reactor = new NonBlockingReactor();
            RequestBus<ImmutablePropertyChanges> validationBus = propertiesProcessor.validationBus;

            new SubscribeAReq<ImmutablePropertyChanges>(
                    validationBus,
                    reactor,
                    new PropertyChangesFilter("immutable.")){
                @Override
                protected void processContent(final ImmutablePropertyChanges _content)
                        throws Exception {
                    SortedMap<String, PropertyChange> readOnlyChanges = _content.readOnlyChanges;
                    final Iterator<PropertyChange> it = readOnlyChanges.values().iterator();
                    while (it.hasNext()) {
                        final PropertyChange propertyChange = it.next();
                        if (propertyChange.name.startsWith("immutable.") && propertyChange.oldValue != null) {
                            throw new IllegalArgumentException("Immutable property can not be changed: " +
                                    propertyChange.name);
                        }
                    }
                }
            }.call();

            try {
                propertiesProcessor.putAReq("pie", "apple").call();
                propertiesProcessor.putAReq("pie", "peach").call();
                propertiesProcessor.putAReq("pie", null).call();
                propertiesProcessor.putAReq("fruit", "pear").call();
                propertiesProcessor.putAReq("fruit", "orange").call();
                propertiesProcessor.putAReq("immutable.fudge", "fun").call();
                propertiesProcessor.putAReq("immutable.fudge", null).call();
            } catch (final Exception e) {
                System.out.println(e.getMessage());
            }
            System.out.println(propertiesProcessor.getImmutableState().sortedKeySet());
        } finally {
            Plant.close();
        }
    }
}
