package org.agilewiki.jactor2.core.blades.pubSub.transactions.properties;

import org.agilewiki.jactor2.core.blades.pubSub.transactions.properties.immutable.ImmutableProperties;

import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

public class PropertiesChangeManager implements AutoCloseable {
    final ImmutableProperties<Object> immutableProperties;
    final TreeMap<String, PropertyChange> changes = new TreeMap<String, PropertyChange>();
    final public SortedMap<String, PropertyChange> readOnlyChanges = Collections.unmodifiableSortedMap(changes);
    private boolean closed;

    public PropertiesChangeManager(final ImmutableProperties<Object> _immutableProperties) {
        immutableProperties = _immutableProperties;
    }

    @Override
    public void close() throws Exception {
        closed = true;
    }
}
