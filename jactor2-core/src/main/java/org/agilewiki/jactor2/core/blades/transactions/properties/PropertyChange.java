package org.agilewiki.jactor2.core.blades.transactions.properties;

public class PropertyChange {
    final public String name;
    final public Object oldValue;
    final public Object newValue;

    public PropertyChange(final String _name,
                          final Object _oldValue,
                          final Object _newValue) {
        name = _name;
        oldValue = _oldValue;
        newValue = _newValue;
    }
}
