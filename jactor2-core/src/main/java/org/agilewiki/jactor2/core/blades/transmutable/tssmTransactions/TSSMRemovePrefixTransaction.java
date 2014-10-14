package org.agilewiki.jactor2.core.blades.transmutable.tssmTransactions;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Removes properties that match a prefix.
 */
public class TSSMRemovePrefixTransaction<VALUE> extends TSSMTransaction<VALUE> {
    public final String prefix;
    private Set<String> removed;

    public TSSMRemovePrefixTransaction(final String _prefix) {
        prefix = _prefix;
    }

    public TSSMRemovePrefixTransaction(final String _prefix,
                                       final TSSMRemoveTransaction<VALUE> _parent) {
        super(_parent);
        prefix = _prefix;
    }

    @Override
    protected void update() throws Exception {
        final Set<String> keys = tssmChangeManager.getUnmodifiableTSSMap().
                subMap(prefix, prefix + Character.MAX_VALUE).keySet();
        removed = new HashSet<String>(keys);
        Iterator<String> it = removed.iterator();
        while (it.hasNext()) {
            final String key = it.next();
            tssmChangeManager.put(key, null);
        }
    }

    @Override
    protected void updateTrace() {
        trace.insert(0, "\nTRACE: keys removed - " + removed);
    }
}
