package org.agilewiki.jactor2.core.blades.pubSub.transactions.properties;

import com.google.common.collect.ImmutableSortedMap;

import java.util.SortedMap;

public class PropertiesChangeManager implements AutoCloseable {
    public final SortedMap<String, Object> oldImmutableProperties;
    final private ImmutableSortedMap.Builder newPropertiesBuilder;
    final private ImmutableSortedMap.Builder propertyChangesBuilder;
    private boolean closed;
    public PropertiesChangeManager(final SortedMap<String, Object> _oldImmutableProperties,
                                   final ImmutableSortedMap.Builder<String, Object> _newPropertiesBuilder,
                                   final ImmutableSortedMap.Builder<String, PropertyChange> _propertyChangesBuilder) {
        oldImmutableProperties = _oldImmutableProperties;
        newPropertiesBuilder = _newPropertiesBuilder;
        propertyChangesBuilder = _propertyChangesBuilder;
    }

    public void put(final String _key, final Object _newValue) {
        Object oldValue = oldImmutableProperties.get(_key);

    }

    @Override
    public void close() throws Exception {
        closed = true;
    }
}
