package org.agilewiki.jactor2.core.blades.transactions.properties;

import java.util.NavigableMap;
import java.util.SortedMap;

public class PropertiesWrapper extends PropertyChanges implements AutoCloseable {
    final private NavigableMap<String, Object> newProperties;
    final private NavigableMap<String, PropertyChange> propertyChanges;
    private boolean closed;

    public PropertiesWrapper(final SortedMap<String, Object> _oldProperties,
            final NavigableMap<String, Object> _newProperties,
            final SortedMap<String, Object> _newReadOnlyProperties,
            final NavigableMap<String, PropertyChange> _propertyChanges,
            final SortedMap<String, PropertyChange> _readOnlyPropertyChanges) {
        super(_oldProperties, _newReadOnlyProperties, _readOnlyPropertyChanges);
        newProperties = _newProperties;
        propertyChanges = _propertyChanges;
    }

    public Object put(final String _key, final Object _newValue) {
        if (closed) {
            throw new IllegalStateException(
                    "Already closed, the transaction is complete.");
        }
        Object oldValue = oldReadOnlyProperties.get(_key);
        final Object oldOldValue = oldValue;
        final PropertyChange oldPropertyChange = propertyChanges.get(_key);
        if (oldPropertyChange != null) {
            if (oldPropertyChange.newValue == _newValue) {
                return _newValue;
            }
            if ((_newValue != null)
                    && _newValue.equals(oldPropertyChange.newValue)) {
                return _newValue;
            }
            oldValue = oldPropertyChange.newValue;
        }
        propertyChanges.put(_key, new PropertyChange(_key, oldOldValue,
                _newValue));
        if (_newValue == null) {
            newProperties.remove(_key);
        } else {
            newProperties.put(_key, _newValue);
        }
        return oldValue;
    }

    @Override
    public void close() throws Exception {
        closed = true;
    }
}
