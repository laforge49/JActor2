package org.agilewiki.jactor2.core.blades.transmutable.tssmTransactions;

import java.util.SortedMap;

/**
 * The content passed to subscribers of the validation and change RequestBus instances.
 */
public class TSSMChanges<VALUE> {
    /**
     * Current version of the unmodifiable sorted map.
     */
    public final SortedMap<String, VALUE> unmodifiableSortedMap;

    /**
     * An unmodifiable sorted map of the item changes.
     */
    public final SortedMap<String, TSSMChange<VALUE>> unmodifiableChanges;

    public TSSMChanges(
            final TSSMChangeManager<VALUE> tssmChangeManager) {
        unmodifiableSortedMap = tssmChangeManager.getTSSMap().createUnmodifiable();
        unmodifiableChanges = tssmChangeManager.unmodifiableChanges;
    }
}
