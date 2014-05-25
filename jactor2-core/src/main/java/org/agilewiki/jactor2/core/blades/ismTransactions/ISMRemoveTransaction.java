package org.agilewiki.jactor2.core.blades.ismTransactions;

import org.agilewiki.jactor2.core.blades.filters.Filter;
import org.agilewiki.jactor2.core.blades.transactions.ISMap;
import org.agilewiki.jactor2.core.blades.transactions.ImmutableSource;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Removes properties that pass a filter.
 */
public class ISMRemoveTransaction<VALUE> extends ISMSyncTransaction<VALUE> {
    public final Filter filter;
    private Set<String> removed;

    /**
     * Create a transaction to remove properties.
     *
     * @param _filter    The filter used to select the keys to be removed.
     */
    public ISMRemoveTransaction(final Filter _filter) {
        filter = _filter;
    }

    /**
     * Compose a transaction to remove properties.
     *
     * @param _filter    The filter used to select the keys to be removed.
     * @param _parent        The property transaction to be applied before this one.
     */
    public ISMRemoveTransaction(final Filter _filter, final ISMTransaction<VALUE> _parent) {
        super(_parent);
        filter = _filter;
    }

    @Override
    protected void update(ImmutableSource<ISMap<VALUE>> source) throws Exception {
        ISMap<VALUE> immutableProperties = source.getImmutable();
        removed = new HashSet<String>();
        Set<String> keys = immutableProperties.keySet();
        Iterator<String> it = keys.iterator();
        while(it.hasNext()) {
            String key = it.next();
            immutableProperties = immutableProperties.minus(key);
            removed.add(key);
        }
        immutable = immutableProperties;
    }

    @Override
    protected void updateTrace() {
        trace.insert(0, "\nTRACE: keys removed - " + removed);
    }
}
