package org.agilewiki.jactor2.core.blades.pubSub.transactions.properties;

/**
 * Represents a change made to a property in the immutable properties map.
 */
public class PropertyChange {

    /**
     * The name of the property.
     */
    public final String name;

    /**
     * The old value of the property, or null.
     */
    public final Object oldValue;

    /**
     * The new value of the property, or null.
     */
    public final Object newValue;

    PropertyChange(final String _name, final Object _oldValue,
                          final Object _newValue) {
        name = _name;
        oldValue = _oldValue;
        newValue = _newValue;
    }
}
