package org.agilewiki.jactor2.core.blades.transmutable.tssmTransactions;

import org.agilewiki.jactor2.core.blades.filters.Filter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Composible transaction to remove properties that pass a filter.
 */
public class TSSMRemoveTransaction<VALUE> extends TSSMTransaction<VALUE> {
    public final Filter<String> filter;
    private Set<String> removed;

    /**
     * Create a transaction to remove properties.
     *
     * @param _filter    The filter used to select the keys to be removed.
     */
    public TSSMRemoveTransaction(final Filter<String> _filter) {
        filter = _filter;
    }

    /**
     * Compose a transaction to remove properties.
     *
     * @param _filter    The filter used to select the keys to be removed.
     * @param _parent        The property transaction to be applied before this one.
     */
    public TSSMRemoveTransaction(final Filter<String> _filter,
                                final TSSMRemoveTransaction<VALUE> _parent) {
        super(_parent);
        filter = _filter;
    }

    @Override
    protected void update(final TSSMap<VALUE> _transmutable) throws Exception {
        removed = new HashSet<String>();
        final Set<String> keys = _transmutable.keySet();
        final Iterator<String> it = keys.iterator();
        while (it.hasNext()) {
            final String key = it.next();
            if (filter.match(key)) {
                it.remove();
                removed.add(key);
            }
        }
    }

    @Override
    protected void updateTrace() {
        trace.insert(0, "\nTRACE: keys removed - " + removed);
    }
}
