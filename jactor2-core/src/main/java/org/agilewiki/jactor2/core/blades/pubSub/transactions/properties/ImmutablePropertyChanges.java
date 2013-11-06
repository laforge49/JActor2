package org.agilewiki.jactor2.core.blades.pubSub.transactions.properties;

import org.agilewiki.jactor2.core.blades.pubSub.transactions.properties.immutable.ImmutableProperties;

import java.util.SortedMap;

public class ImmutablePropertyChanges {
    public final ImmutableProperties<Object> immutableProperties;
    public final SortedMap<String, PropertyChange> readOnlyChanges;

    public ImmutablePropertyChanges(final PropertiesChangeManager propertiesChangeManager) {
        immutableProperties = propertiesChangeManager.immutableProperties;
        readOnlyChanges = propertiesChangeManager.readOnlyChanges;
    }
}
