package org.agilewiki.jactor2.core.blades.pubSub.transactions.properties;

import org.agilewiki.jactor2.core.blades.pubSub.transactions.properties.immutable.ImmutableProperties;

import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

public class PropertiesChangeManager implements AutoCloseable {
    ImmutableProperties<Object> immutableProperties;
    final TreeMap<String, PropertyChange> changes = new TreeMap<String, PropertyChange>();
    final public SortedMap<String, PropertyChange> readOnlyChanges = Collections.unmodifiableSortedMap(changes);
    private boolean closed;

    public PropertiesChangeManager(final ImmutableProperties<Object> _immutableProperties) {
        immutableProperties = _immutableProperties;
    }

    public ImmutableProperties<Object> getImmutableProperties() {
        return immutableProperties;
    }

    public void put(final String _key, final Object _newValue) {
        if (closed) {
            throw new IllegalStateException(
                    "Already closed, the transaction is complete.");
        }
        if (_key == null)
            throw new IllegalArgumentException("key may not be null");
        Object oldValue = immutableProperties.get(_key);
        if (oldValue == _newValue)
            return;
        if (_newValue == null)
            immutableProperties = immutableProperties.minus(_key);
        else
            immutableProperties = immutableProperties.plus(_key, _newValue);
        PropertyChange propertyChange = new PropertyChange(_key, oldValue, _newValue);
        changes.put(_key, propertyChange);
    }

    @Override
    public void close() throws Exception {
        closed = true;
    }
}
