package org.agilewiki.jactor2.core.blades.transmutable.tssmTransactions;

import org.agilewiki.jactor2.core.blades.filters.Filter;

import java.util.Iterator;
import java.util.SortedMap;

/**
 * A RequestBus filter for use with TSSMChanges that selects based on a name prefix.
 */
public class TSSMPrefixFilter<VALUE> implements Filter<TSSMChanges<VALUE>> {
    /**
     * The prefix used for filtering names.
     */
    public final String prefix;

    /**
     * Create a TSSMPrefixFilter.
     *
     * @param _prefix The prefix that names of interest must start with.
     */
    public TSSMPrefixFilter(final String _prefix) {
        prefix = _prefix;
    }

    @Override
    public boolean match(TSSMChanges<VALUE> _changes) {
        SortedMap<String, TSSMChange<VALUE>> unmodifiableChanges = _changes.unmodifiableChanges.tailMap(prefix);
        if (unmodifiableChanges.size() == 0)
            return false;
        Iterator<String> it = unmodifiableChanges.keySet().iterator();
        return it.next().startsWith(prefix);
    }
}
