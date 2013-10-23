package org.agilewiki.jactor2.core.blades.transactions.properties;

import java.util.SortedMap;

public class PropertyChanges {
    final public SortedMap<String, Object> oldReadOnlyProperties;
    final public SortedMap<String, Object> newReadOnlyProperties;
    final public SortedMap<String, PropertyChange> readOnlyPropertyChanges;

    public PropertyChanges(final SortedMap<String, Object> _oldProperties,
                           final SortedMap<String, Object> _newReadOnlyProperties,
                           final SortedMap<String, PropertyChange> _readOnlyPropertyChanges) {
        oldReadOnlyProperties = _oldProperties;
        newReadOnlyProperties = _newReadOnlyProperties;
        readOnlyPropertyChanges = _readOnlyPropertyChanges;
    }
}
