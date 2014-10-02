package org.agilewiki.jactor2.core.blades.transmutable;

/**
 * Represents a change made to a items in the TSSMap.
 */
public class TSSMChange<VALUE> {

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

    public TSSMChange(final String _name,
                      final VALUE _oldValue,
                      final VALUE _newValue) {
        name = _name;
        oldValue = _oldValue;
        newValue = _newValue;
    }
}
