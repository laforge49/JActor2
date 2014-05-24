package org.agilewiki.jactor2.core.blades.ismTransactions;

/**
 * Represents a change made to a items in the immutable string map.
 */
public class ImmutableChange<VALUE> {

    /**
     * The name of the item changed.
     */
    public final String name;

    /**
     * The old value of the item, or null.
     */
    public final VALUE oldValue;

    /**
     * The new value of the item, or null.
     */
    public final VALUE newValue;

    public ImmutableChange(final String _name, final VALUE _oldValue,
                           final VALUE _newValue) {
        name = _name;
        oldValue = _oldValue;
        newValue = _newValue;
    }
}
