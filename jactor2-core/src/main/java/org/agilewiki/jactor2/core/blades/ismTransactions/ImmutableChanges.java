package org.agilewiki.jactor2.core.blades.ismTransactions;

import java.util.SortedMap;

import org.agilewiki.jactor2.core.blades.transactions.ISMap;

/**
 * The content passed to subscribers of the validation and change RequestBus instances.
 */
public class ImmutableChanges<VALUE> {

    /**
     * The new version of the immutable string map.
     */
    public final ISMap<VALUE> isMap;

    /**
     * An unmodifiable sorted map of the item changes.
     */
    public final SortedMap<String, ImmutableChange<VALUE>> readOnlyChanges;

    public ImmutableChanges(
            final ImmutableChangeManager<VALUE> immutableChangeManager) {
        isMap = immutableChangeManager.getISMap();
        readOnlyChanges = immutableChangeManager.readOnlyChanges;
    }
}
