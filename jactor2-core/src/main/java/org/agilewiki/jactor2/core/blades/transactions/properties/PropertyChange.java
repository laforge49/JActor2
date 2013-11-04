package org.agilewiki.jactor2.core.blades.transactions.properties;

public class PropertyChange {
    public final String name;
    public final Object oldValue;
    public final Object newValue;

    public PropertyChange(final String _name, final Object _oldValue,
            final Object _newValue) {
        name = _name;
        oldValue = _oldValue;
        newValue = _newValue;
    }
}
