package org.agilewiki.jactor2.core.blades.ismTransactions;

import org.agilewiki.jactor2.core.blades.filters.Filter;

import java.util.Iterator;
import java.util.SortedMap;

/**
 * A RequestBus filter for use with ImmutablePropertyChanges that selects based on a property name prefix.
 */
public class ChangesFilter<VALUE> implements Filter<ImmutableChanges<VALUE>> {
    private final String prefix;

    /**
     * Create a PropertyChangesFilter.
     *
     * @param _prefix    The prefix that property names of interest must start with.
     */
    public ChangesFilter(final String _prefix) {
        prefix = _prefix;
    }

    @Override
    public boolean match(ImmutableChanges<VALUE> _changes) {
        SortedMap<String, ImmutableChange<VALUE>> readOnlyChanges = _changes.readOnlyChanges;
        Iterator<String> it = readOnlyChanges.keySet().iterator();
        while (it.hasNext()) {
            if (it.next().startsWith(prefix))
                return true;
        }
        return false;
    }
}
