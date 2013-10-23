package org.agilewiki.jactor2.core.blades.transactions.properties;

import java.util.NavigableMap;
import java.util.SortedMap;

public class PropertiesWrapper extends PropertyChanges {
    final private NavigableMap<String, Object> newProperties;
    final private NavigableMap<String, PropertyChange> propertyChanges;

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
        Object oldValue = oldReadOnlyProperties.get(_key);
        Object oldOldValue = oldValue;
        PropertyChange oldPropertyChange = propertyChanges.get(_key);
        if (oldPropertyChange != null) {
            if (oldPropertyChange.newValue == _newValue)
                return _newValue;
            if (_newValue != null && _newValue.equals(oldPropertyChange.newValue))
                return _newValue;
            oldValue = oldPropertyChange.newValue;
        }
        propertyChanges.put(_key, new PropertyChange(_key, oldOldValue, _newValue));
        if (_newValue == null)
            newProperties.remove(_key);
        else
            newProperties.put(_key, _newValue);
        return oldValue;
    }
}
