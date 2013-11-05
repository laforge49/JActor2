package org.agilewiki.jactor2.core.blades.pubSub.transactions.properties;

import java.util.SortedMap;

public class ImmutablePropertyChanges {
    public final SortedMap<String, Object> oldImmutableProperties;
    public final SortedMap<String, Object> newImmutableProperties;
    public final SortedMap<String, PropertyChange> immutablePropertyChanges;

    public ImmutablePropertyChanges(final SortedMap<String, Object> _oldImmutableProperties,
                           final SortedMap<String, Object> _newImmutableProperties,
                           final SortedMap<String, PropertyChange> _immutablePropertyChanges) {
        oldImmutableProperties = _oldImmutableProperties;
        newImmutableProperties = _newImmutableProperties;
        immutablePropertyChanges = _immutablePropertyChanges;
    }
}
