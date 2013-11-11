package org.agilewiki.jactor2.core.blades.transactions.properties;

import org.agilewiki.jactor2.core.blades.pubSub.Filter;

import java.util.Iterator;
import java.util.SortedMap;

/**
 * A RequestBus filter for use with ImmutablePropertyChanges that selects based on a property name prefix.
 */
public class PropertyChangesFilter implements Filter<ImmutablePropertyChanges> {
    private final String prefix;

    /**
     * Create a PropertyChangesFilter.
     *
     * @param _prefix    The prefix that property names of interest must start with.
     */
    public PropertyChangesFilter(final String _prefix) {
        prefix = _prefix;
    }

    @Override
    public boolean match(ImmutablePropertyChanges _changes) {
        SortedMap<String, PropertyChange> readOnlyChanges = _changes.readOnlyChanges;
        Iterator<String> it = readOnlyChanges.keySet().iterator();
        while (it.hasNext()) {
            if (it.next().startsWith(prefix))
                return true;
        }
        return false;
    }
}
