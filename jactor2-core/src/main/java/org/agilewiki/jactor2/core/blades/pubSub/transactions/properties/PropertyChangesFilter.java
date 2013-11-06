package org.agilewiki.jactor2.core.blades.pubSub.transactions.properties;

import org.agilewiki.jactor2.core.blades.pubSub.Filter;

import java.util.Iterator;
import java.util.SortedMap;

public class PropertyChangesFilter implements Filter<ImmutablePropertyChanges> {
    private final String prefix;

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
