package org.agilewiki.jactor2.core.blades.transactions.properties;

import org.agilewiki.jactor2.core.blades.transactions.ImmutableChanges;

import java.util.Iterator;
import java.util.SortedMap;

public class PropertyChanges implements ImmutableChanges {
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

    @Override
    public boolean hasMatchingChange(final String _prefix) {
        Iterator<String> it = readOnlyPropertyChanges.keySet().iterator();
        while (it.hasNext()) {
            if (it.next().startsWith(_prefix))
                return true;
        }
        return false;
    }

    public SortedMap<String, PropertyChange> matchingPropertyChanges(final String _prefix) throws Exception {
        return readOnlyPropertyChanges.subMap(_prefix + Character.MIN_VALUE, _prefix + Character.MAX_VALUE);
    }
}
